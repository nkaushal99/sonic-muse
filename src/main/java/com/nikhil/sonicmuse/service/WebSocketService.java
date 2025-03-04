package com.nikhil.sonicmuse.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.sonicmuse.enumeration.PlayerMessageType;
import com.nikhil.sonicmuse.mapper.AttendeeMapper;
import com.nikhil.sonicmuse.mapper.PartyMapper;
import com.nikhil.sonicmuse.repository.AttendeeRepository;
import com.nikhil.sonicmuse.util.cache.InstanceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.GoneException;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;

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
            PlayerMessageType messageType = PlayerMessageType.valueOf(type.toUpperCase());

            switch (messageType)
            {
                case JOIN -> handleJoin(connectionId, json);
                case PLAY, PAUSE, SEEK, VOLUME, SYNC_SONG_LIST -> broadcastMessage(json);
                default -> LOGGER.warn("Unknown message type: {}", messageType);
            }
        } catch (IOException e)
        {
            LOGGER.error("Error parsing JSON: {}", payload, e);
            throw new RuntimeException(e);
        }
    }

    private void handleJoin(String attendeeId, JsonNode json)
    {
        String partyId = json.get("partyId").asText();

        AttendeeMapper attendee = attendeeRepository.findAttendeeById(attendeeId);
        PartyMapper party = partyService.getPartyCreateIfAbsent(partyId);
        party.addAttendee(attendeeId);
        partyService.saveParty(party);

        LOGGER.info("Attendee {} joined party {}", attendee.getId(), partyId);
    }

    private void broadcastMessage(JsonNode json) throws IOException
    {
        String partyId = json.get("partyId").asText();
        PartyMapper party = partyService.getPartyMapper(partyId);
        if (party != null)
        {
            for (String attendeeId : party.getAttendeeIds())
            {
                try
                {
                    sendMessage(attendeeId, json.asText());
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
