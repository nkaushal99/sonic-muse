package com.nikhil.sonicmuse.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.sonicmuse.enumeration.WebSocketMessageType;
import com.nikhil.sonicmuse.mapper.AttendeeMapper;
import com.nikhil.sonicmuse.mapper.PartyMapper;
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
        AttendeeMapper attendee = new AttendeeMapper(connectionId);
        attendeeRepository.put(attendee);
        LOGGER.info("Attendee: {} created", connectionId);
    }

    public void handleDisconnect(String connectionId)
    {
        AttendeeMapper attendee = attendeeRepository.findAttendeeById(connectionId);
        if (attendee == null)
            return;

        String partyId = attendee.getPartyId();
        PartyMapper partyMapper = partyService.getPartyMapper(partyId);
        if (partyMapper != null)
        {
            partyMapper.removeAttendee(attendee.getId());
            // todo add a retry mechanism if method fails coz of DynamoDBConditionalCheckFailedException
            partyService.saveParty(partyMapper);
            LOGGER.info("Attendee: {} removed from party: {}", connectionId, partyId);
        }

        attendeeRepository.delete(attendee);
        LOGGER.info("Attendee: {} deleted", connectionId);
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
                case CREATE -> handleCreate(connectionId);
                case JOIN -> handleJoin(partyId, connectionId);
                case LEAVE -> handleLeave(partyId, connectionId);
                case PLAY, PAUSE, SEEK, SYNC_SONG_LIST -> broadcastMessage(partyId, payload);
                default -> LOGGER.warn("Unknown message type: {}", messageType);
            }
        } catch (IOException e)
        {
            LOGGER.error("Error parsing JSON: {}", payload, e);
            throw new RuntimeException(e);
        }
    }

    private void handleCreate(String connectionId) throws JsonProcessingException
    {
        PartyMapper party = partyService.createParty(connectionId);
        partyService.saveParty(party);

        WebSocketBaseResponse responseBody = new WebSocketBaseResponse();
        responseBody.setType(WebSocketMessageType.CREATE);
        responseBody.setPartyId(party.getId());
        responseBody.setHostId(party.getHostId());

        String message = objectMapper.writeValueAsString(responseBody);
        sendMessage(connectionId, message);

        LOGGER.info("Connection: {} created party: {}", connectionId, party.getId());
    }

    private void handleJoin(String partyId, String connectionId) throws IOException
    {
        PartyMapper party = partyService.getPartyMapper(partyId);
        if (party == null)
            throw new RuntimeException("No party found for id: " + partyId);

        party.addAttendee(connectionId);;
        partyService.saveParty(party);

        MemberActionResponse responseBody = new MemberActionResponse();
        responseBody.setType(WebSocketMessageType.MEMBER_JOIN);
        responseBody.setPartyId(party.getId());
        responseBody.setHostId(party.getHostId());

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(connectionId);
        memberDTO.setName("Nikhil " + party.getAttendeeIds().size());
        responseBody.setMember(memberDTO);

        String message = objectMapper.writeValueAsString(responseBody);
        broadcastMessage(partyId, message);

        LOGGER.info("Connection: {} joined party: {}", connectionId, partyId);
    }

    private void handleLeave(String partyId, String connectionId) throws IOException
    {
        PartyMapper party = partyService.getPartyMapper(partyId);
        party.removeAttendee(connectionId);
        partyService.saveParty(party);

        MemberActionResponse responseBody = new MemberActionResponse();
        responseBody.setType(WebSocketMessageType.MEMBER_LEAVE);
        responseBody.setPartyId(party.getId());
        responseBody.setHostId(party.getHostId());

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(connectionId);
        responseBody.setMember(memberDTO);

        String message = objectMapper.writeValueAsString(responseBody);
        broadcastMessage(connectionId, message);

        LOGGER.info("Connection: {} left party: {}", connectionId, party.getId());
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

    private void sendMessage(String connectionId, String message) throws GoneException
    {
        PostToConnectionRequest request = PostToConnectionRequest.builder()
                .connectionId(connectionId)
                .data(SdkBytes.fromUtf8String(message))
                .build();

        apiGatewayClient.postToConnection(request);
    }
}
