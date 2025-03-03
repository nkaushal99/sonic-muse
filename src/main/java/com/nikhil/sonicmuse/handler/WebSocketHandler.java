package com.nikhil.sonicmuse.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import com.nikhil.sonicmuse.service.WebSocketService;

public class WebSocketHandler implements RequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse>
{

    private final WebSocketService webSocketService = WebSocketService.getInstance();

    @Override
    public APIGatewayV2WebSocketResponse handleRequest(APIGatewayV2WebSocketEvent event, Context context) {
        String routeKey = event.getRequestContext().getRouteKey();
        String connectionId = event.getRequestContext().getConnectionId();
        context.getLogger().log("Received event: " + routeKey + " from connection: " + connectionId);

        switch (routeKey) {
            case "$connect" -> handleConnect(connectionId, context);
            case "$disconnect" -> handleDisconnect(connectionId, context);
//            case "sendMessage" -> handleMessage(event, connectionId, context);
            default -> handleMessage(event, connectionId, context);
        }

        APIGatewayV2WebSocketResponse response = new APIGatewayV2WebSocketResponse();
        response.setStatusCode(200);
        return response;
    }

    private void handleConnect(String connectionId, Context context) {
        context.getLogger().log("New WebSocket connection: " + connectionId);
        webSocketService.handleConnect(connectionId);
    }

    private void handleDisconnect(String connectionId, Context context) {
        context.getLogger().log("WebSocket disconnected: " + connectionId);
        webSocketService.handleDisconnect(connectionId);
    }

    private void handleMessage(APIGatewayV2WebSocketEvent event, String connectionId, Context context) {
        context.getLogger().log("Received message: " + event.getBody() + " from: " + connectionId);
        webSocketService.handleMessage(event, connectionId);
    }
}

//    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketLambdaHandler.class);
//    private final PartyService partyService = new PartyService();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final Map<String, Attendee> attendees = new ConcurrentHashMap<>();
//    private ApiGatewayManagementApiClient apiClient;
//
//    @Override
//    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context)
//    {
//        if (apiClient == null)
//        {
//            String endpoint = event.getRequestContext().getDomainName() + "/" + event.getRequestContext().getStage();
//            apiClient = ApiGatewayManagementApiClient.builder().endpointOverride(endpoint).build();
//        }
//
//        String routeKey = event.getRequestContext().getRouteKey();
//        String connectionId = event.getRequestContext().getConnectionId();
//
//        try
//        {
//            switch (routeKey)
//            {
//                case "$connect":
//                    handleConnect(connectionId);
//                    break;
//                case "$disconnect":
//                    handleDisconnect(connectionId);
//                    break;
//                case "$default":
//                    handleApiGatewayMessage(connectionId, event.getBody());
//                    break;
//                default:
//                    LOGGER.warn("Unknown routeKey: {}", routeKey);
//                    break;
//            }
//            return new APIGatewayProxyResponseEvent().withStatusCode(200);
//        } catch (Exception e)
//        {
//            LOGGER.error("Error processing request", e);
//            return new APIGatewayProxyResponseEvent().withStatusCode(500);
//        }
//    }
//
//    // ... (rest of the ApiGatewayHandler code from previous response)
//    private void handleConnect(String connectionId)
//    {
//        Attendee attendee = new Attendee(connectionId, apiClient);
//        attendees.put(connectionId, attendee);
//        LOGGER.info("Connection established: {}", connectionId);
//    }
//
//    private void handleDisconnect(String connectionId)
//    {
//        Attendee attendee = attendees.remove(connectionId);
//        if (attendee != null)
//        {
//            PartyMapper party = partyService.findPartyByAttendee(attendee);
//            if (party != null)
//            {
//                party.removeAttendee(attendee);
//                partyService.saveParty(party);
//            }
//            LOGGER.info("Connection closed: {}", connectionId);
//        }
//    }
//
//    private void handleApiGatewayMessage(String connectionId, String payload) throws IOException
//    {
//        try
//        {
//            JsonNode json = objectMapper.readTree(payload);
//            String type = json.get("type").asText();
//            PlayerMessageType messageType = PlayerMessageType.valueOf(type.toUpperCase(Locale.ROOT));
//            // ... (rest of the handleApiGatewayMessage code)
//        } catch (IOException e)
//        {
//            LOGGER.error("Error parsing JSON: {}", payload, e);
//            throw e;
//        }
//    }
//
//    //POJO Attendee
//    class Attendee
//    {
//        private String id;
//        private ApiGatewayManagementApiClient apiClient;
//
//        public Attendee(String id, ApiGatewayManagementApiClient apiClient)
//        {
//            this.id = id; this.apiClient = apiClient;
//        }
//
//        public String getId()
//        {
//            return id;
//        }
//
//        public void sendMessage(String message)
//        {
//            apiClient.postToConnection(PostToConnectionRequest.builder().connectionId(id).data(ByteBuffer.wrap(message.getBytes())).build());
//        }
//    }