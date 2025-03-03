//package com.nikhil.sonicmuse.service;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import software.amazon.awssdk.core.SdkBytes;
//import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
//import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
//import software.amazon.awssdk.services.apigatewaymanagementapi.model.GoneException;
//
//import java.net.URI;
//
//import static com.nikhil.sonicmuse.config.ConfigConstants.DEFAULT_REGION;
//
//public class WebSocketHandler {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);
//
//    private final ApiGatewayManagementApiClient apiClient;
//
//    public WebSocketHandler(String endpoint) {
//        this.apiClient = ApiGatewayManagementApiClient.builder()
//                .endpointOverride(URI.create(endpoint))
//                .region(DEFAULT_REGION)
//                .build();
//    }
//
//    public void sendMessage(String connectionId, String message) {
//        PostToConnectionRequest request = PostToConnectionRequest.builder()
//                .connectionId(connectionId)
//                .data(SdkBytes.fromUtf8String(message))
//                .build();
//        try {
//            apiClient.postToConnection(request);
//        } catch (GoneException e) {
//            // Handle disconnection of clients gracefully
//            LOGGER.error("Connection is closed, client is no longer connected", e);
//        }
//    }
//}