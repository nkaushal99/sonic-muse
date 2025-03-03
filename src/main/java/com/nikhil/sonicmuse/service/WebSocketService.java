package com.nikhil.sonicmuse.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.nikhil.sonicmuse.mapper.AttendeeMapper;
import com.nikhil.sonicmuse.repository.AttendeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebSocketService
{
private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketService.class.getName());

    private static WebSocketService instance;

    private final PartyService partyService = PartyService.getInstance();
    private final AttendeeRepository attendeeRepository = AttendeeRepository.getInstance();

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
        attendeeRepository.delete(attendee);
        LOGGER.info("Attendee: {} deleted", connectionId);
    }

    public void handleMessage(APIGatewayV2WebSocketEvent event, String connectionId) {
        // todo handle message types
    }
}
