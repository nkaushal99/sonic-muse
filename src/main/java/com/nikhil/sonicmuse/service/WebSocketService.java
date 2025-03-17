package com.nikhil.sonicmuse.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.sonicmuse.enumeration.WebSocketMessageType;
import com.nikhil.sonicmuse.mapper.AttendeeMapper;
import com.nikhil.sonicmuse.mapper.PartyMapper;
import com.nikhil.sonicmuse.pojo.AttendeeDTO;
import com.nikhil.sonicmuse.pojo.MemberActionResponse;
import com.nikhil.sonicmuse.pojo.MemberDTO;
import com.nikhil.sonicmuse.pojo.WebSocketBaseResponse;
import com.nikhil.sonicmuse.repository.AttendeeRepository;
import com.nikhil.sonicmuse.util.cache.InstanceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.GoneException;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;

import java.io.IOException;
import java.util.Optional;


public class WebSocketService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketService.class.getName());

    private static WebSocketService instance;

    private final PartyService partyService = PartyService.getInstance();
    private final AttendeeRepository attendeeRepository = AttendeeRepository.getInstance();
    private final ApiGatewayManagementApiClient apiGatewayClient = InstanceCache.apiGatewayClient.getInstance();
    private final ObjectMapper objectMapper = InstanceCache.objectMapper.getInstance();

    private WebSocketService()
    {
    }

    public static WebSocketService getInstance()
    {
        if (instance == null)
            instance = new WebSocketService();
        return instance;
    }

    public void handleConnect(String connectionId)
    {
        // leaving intentionally, coz we don't know whether to create a room or join one
    }

    public void handleDisconnect(String connectionId)
    {
        try
        {
            handleLeaveRoom(connectionId);
        } catch (IOException e)
        {
            LOGGER.error("Abrupt disconnection", e);
            throw new RuntimeException(e);
        }
    }

    public void handleMessage(APIGatewayV2WebSocketEvent event, String connectionId)
    {
        String payload = event.getBody();
        try
        {
            JsonNode json = objectMapper.readTree(payload);
            String type = json.get("type").asText();
            String partyId = Optional.ofNullable(json.get("partyId"))
                    .map(JsonNode::asText)
                    .orElse(null);

            WebSocketMessageType messageType = WebSocketMessageType.valueOf(type.toUpperCase());
            switch (messageType)
            {
                case CREATE -> handleCreateRoom(connectionId, json);
                case JOIN -> handleJoinRoom(connectionId, json);
                case LEAVE -> handleLeaveRoom(connectionId, json);
                case PLAY, PAUSE, SEEK, SYNC_SONG_LIST -> broadcastMessage(partyId, payload);
                default -> LOGGER.warn("Unknown message type: {}", messageType);
            }
        } catch (IOException e)
        {
            LOGGER.error("Error parsing JSON: {}", payload, e);
            throw new RuntimeException(e);
        }
    }

    private void handleCreateRoom(String connectionId, JsonNode json) throws JsonProcessingException
    {
        PartyMapper party = partyService.createParty(connectionId);
        partyService.saveParty(party);

        AttendeeDTO attendeeDTO = new AttendeeDTO();
        attendeeDTO.setId(connectionId);
        attendeeDTO.setName(Optional.ofNullable(json.get("member").get("name")).map(JsonNode::asText).orElse(null));
        attendeeDTO.setPartyId(party.getId());
        saveAttendee(attendeeDTO);

        WebSocketBaseResponse responseBody = new WebSocketBaseResponse();
        responseBody.setType(WebSocketMessageType.CREATE);
        responseBody.setPartyId(party.getId());
        responseBody.setHostId(party.getHostId());

        String message = objectMapper.writeValueAsString(responseBody);
        sendMessage(connectionId, message);

        LOGGER.info("Connection: {} created party: {}", connectionId, party.getId());
    }

    private void handleJoinRoom(String connectionId, JsonNode json) throws IOException
    {
        String partyId = Optional.of(json.get("partyId"))
                .map(JsonNode::asText)
                .orElse(null);
        PartyMapper party = partyService.getPartyMapper(partyId);
        if (party == null)
            throw new RuntimeException("No party found for id: " + partyId);

        party.addAttendee(connectionId);
        partyService.saveParty(party);

        AttendeeDTO attendeeDTO = new AttendeeDTO();
        attendeeDTO.setId(connectionId);
        attendeeDTO.setName(Optional.ofNullable(json.get("member").get("name")).map(JsonNode::asText).orElse(null));
        attendeeDTO.setPartyId(party.getId());
        saveAttendee(attendeeDTO);

        MemberActionResponse responseBody = new MemberActionResponse();
        responseBody.setType(WebSocketMessageType.MEMBER_JOIN);
        responseBody.setPartyId(party.getId());
        responseBody.setHostId(party.getHostId());

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(connectionId);
        memberDTO.setName(attendeeDTO.getName());
        responseBody.setMember(memberDTO);

        String message = objectMapper.writeValueAsString(responseBody);
        broadcastMessage(partyId, message);

        LOGGER.info("Connection: {} joined party: {}", connectionId, partyId);
    }

    private void handleLeaveRoom(String connectionId) throws IOException
    {
        handleLeaveRoom(connectionId, null);
    }

    private void handleLeaveRoom(String connectionId, JsonNode json) throws IOException
    {
        JsonNode partyNode = Optional.ofNullable(json).map(j -> j.get("partyId")).orElse(null);
        String partyId = Optional.ofNullable(partyNode)
                .map(JsonNode::asText)
                .orElse(
                        // try to extract partyId from attendee, if not disconnected already
                        Optional.ofNullable(attendeeRepository.findAttendeeById(connectionId))
                                .map(AttendeeMapper::getPartyId)
                                .orElse(null)
                );

        if (partyId == null)
        {
            LOGGER.warn("No party found for connection: {}", connectionId);
        }

        PartyMapper party = partyService.getPartyMapper(partyId);
        party.removeAttendee(connectionId);
        partyService.saveParty(party);

        AttendeeMapper attendeeMapper = attendeeRepository.findAttendeeById(connectionId);
        if (attendeeMapper != null)
            attendeeRepository.delete(attendeeMapper);

        MemberActionResponse responseBody = new MemberActionResponse();
        responseBody.setType(WebSocketMessageType.MEMBER_LEAVE);
        responseBody.setPartyId(party.getId());
        responseBody.setHostId(party.getHostId());

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(connectionId);
        responseBody.setMember(memberDTO);

        String message = objectMapper.writeValueAsString(responseBody);
        broadcastMessage(partyId, message);

        LOGGER.info("Connection: {} left party: {}", connectionId, partyId);
    }

    private void broadcastMessage(String partyId, String message) throws IOException
    {
        PartyMapper party = partyService.getPartyMapper(partyId);
        if (party != null)
        {
            for (String attendeeId : party.getAttendeeIds())
            {
                try
                {
                    sendMessage(attendeeId, message);
                } catch (GoneException e)
                {
                    LOGGER.error("Connection: {} is no longer connected", attendeeId, e);

                    // Handle disconnection of clients gracefully
                    handleDisconnect(attendeeId);
                }
            }
        }
    }

    private void saveAttendee(AttendeeDTO attendeeDTO)
    {
        AttendeeMapper attendee = new AttendeeMapper(attendeeDTO);
        attendeeRepository.put(attendee);
        LOGGER.info("Attendee: {} created", attendeeDTO.getId());
    }

    private void sendMessage(String connectionId, String message) throws GoneException
    {
        PostToConnectionRequest request = PostToConnectionRequest.builder()
                .connectionId(connectionId)
                .data(SdkBytes.fromUtf8String(message))
                .build();

        apiGatewayClient.postToConnection(request);
    }
}
