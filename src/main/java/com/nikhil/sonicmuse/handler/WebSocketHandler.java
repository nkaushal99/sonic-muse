package com.nikhil.sonicmuse.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import com.nikhil.sonicmuse.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketHandler implements RequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    private final WebSocketService webSocketService = WebSocketService.getInstance();

    @Override
    public APIGatewayV2WebSocketResponse handleRequest(APIGatewayV2WebSocketEvent event, Context context) {
        String routeKey = event.getRequestContext().getRouteKey();
        String connectionId = event.getRequestContext().getConnectionId();
        LOGGER.info("Received event: {} from connection: {}", routeKey, connectionId);

        switch (routeKey) {
            case "$connect" -> handleConnect(connectionId);
            case "$disconnect" -> handleDisconnect(connectionId);
            default -> handleMessage(event, connectionId);
        }

        APIGatewayV2WebSocketResponse response = new APIGatewayV2WebSocketResponse();
        response.setStatusCode(200);
        return response;
    }

    private void handleConnect(String connectionId) {
        LOGGER.info("New WebSocket connection: {}", connectionId);
        webSocketService.handleConnect(connectionId);
    }

    private void handleDisconnect(String connectionId) {
        LOGGER.info("WebSocket disconnected: {}", connectionId);
        webSocketService.handleDisconnect(connectionId);
    }

    private void handleMessage(APIGatewayV2WebSocketEvent event, String connectionId) {
        LOGGER.info("Received message: {} from: {}", event.getBody(), connectionId);
        webSocketService.handleMessage(event, connectionId);
    }
}