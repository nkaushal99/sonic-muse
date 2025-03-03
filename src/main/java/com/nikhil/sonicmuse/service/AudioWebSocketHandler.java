//package com.nikhil.sonicmuse.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nikhil.sonicmuse.enumeration.PlayerMessageType;
//import com.nikhil.sonicmuse.mapper.PartyMapper;
//import com.nikhil.sonicmuse.mapper.AttendeeMapper;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//import java.util.Locale;
//
//@Component
//@RequiredArgsConstructor
//public class AudioWebSocketHandler extends TextWebSocketHandler
//{
//    private static final Logger LOGGER = LoggerFactory.getLogger(AudioWebSocketHandler.class);
//
////    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
////    private final Map<String, Party> parties = new ConcurrentHashMap<>(); // partyId -> (sessionID -> session)
//    private final PartyService partyService;
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session)
//    {
////        parties.put(session.getId(), session);
//        LOGGER.info("WebSocket connection established: {}", session.getId());
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message)
//    {
//        String payload = message.getPayload();
//        try
//        {
//            JsonNode json = objectMapper.readTree(payload);
//            String type = json.get("type").asText();
//            PlayerMessageType messageType = PlayerMessageType.valueOf(type.toUpperCase(Locale.ROOT));
//
//            switch (messageType)
//            {
//                case JOIN -> handleJoin(session, json);
//                case PLAY, PAUSE, SEEK, VOLUME, SYNC_SONG_LIST -> broadcastMessage(json);
//                default -> LOGGER.warn("Unknown message type: {}", messageType);
//            }
//        } catch (IOException e)
//        {
//            LOGGER.error("Error parsing JSON: {}", payload, e);
//        }
//    }
//
//    private void handleJoin(WebSocketSession session, JsonNode json)
//    {
//        String partyId = json.get("partyId").asText();
//
//        AttendeeMapper attendeeMapper = new AttendeeMapper(session);
//        PartyMapper party = partyService.getPartyCreateIfAbsent(partyId);
//        party.addAttendee(attendeeMapper);
//        partyService.saveParty(party);
//
//        LOGGER.info("Attendee {} joined party {}", attendeeMapper.getId(),partyId);
//    }
//
//    private void broadcastMessage(JsonNode json) throws IOException
//    {
//        String partyId = json.get("partyId").asText();
//        PartyMapper party = partyService.getPartyMapper(partyId);
//        boolean modified = false;
//        if (party != null)
//        {
//            for (AttendeeMapper attendeeMapper : party.getAttendeeIds())
//            {
//                if(attendeeMapper.isOnline())
//                    attendeeMapper.sendMessage(new TextMessage(json.toString()));
//                else
//                {
//                    party.removeAttendee(attendeeMapper);
//                    modified = true;
//                }
//            }
//            if (modified)
//                partyService.saveParty(party);
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status)
//    {
//       LOGGER.info("WebSocket connection closed: {}", session.getId());
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception)
//    {
//        LOGGER.error("WebSocket transport error", exception);
//    }
//}