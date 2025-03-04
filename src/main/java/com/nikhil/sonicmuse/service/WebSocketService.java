package com.nikhil.sonicmuse.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.sonicmuse.enumeration.PlayerMessageType;
import com.nikhil.sonicmuse.mapper.AttendeeMapper;
import com.nikhil.sonicmuse.mapper.PartyMapper;
import com.nikhil.sonicmuse.pojo.PartyJoinResponse;
import com.nikhil.sonicmuse.repository.AttendeeRepository;
import com.nikhil.sonicmuse.util.cache.InstanceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.GoneException;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.utils.StringUtils;

import java.io.IOException;


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
            String partyId = json.get("partyId").asText();

            PlayerMessageType messageType = PlayerMessageType.valueOf(type.toUpperCase());
            switch (messageType)
            {
                case JOIN -> handleJoin(partyId, connectionId);
                case PLAY, PAUSE, SEEK, VOLUME, SYNC_SONG_LIST -> broadcastMessage(partyId, payload);
                default -> LOGGER.warn("Unknown message type: {}", messageType);
            }
        } catch (IOException e)
        {
            LOGGER.error("Error parsing JSON: {}", payload, e);
            throw new RuntimeException(e);
        }
    }

    private void handleJoin(String partyId, String connectionId) throws JsonProcessingException
    {
        PartyMapper party;
        if (StringUtils.isBlank(partyId))
        {
            party = partyService.createParty(connectionId);
        } else
        {
            party = partyService.getPartyMapper(partyId);
            if (party == null)
                throw new RuntimeException("No party found for id: " + partyId);

            party.addAttendee(connectionId);
        }
        partyService.saveParty(party);

        PartyJoinResponse responseBody = new PartyJoinResponse();
        responseBody.setType(PlayerMessageType.JOIN_RESPONSE);
        responseBody.setPartyId(party.getId());
        responseBody.setHostId(party.getHostId());

        String message = objectMapper.writeValueAsString(responseBody);
        sendMessage(connectionId, message);

        LOGGER.info("Connection: {} joined party: {}", connectionId, partyId);
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
