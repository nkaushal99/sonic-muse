//package com.nikhil.sonicmuse.lambda;
//
//import com.amazonaws.serverless.exceptions.ContainerInitializationException;
//import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
//import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
//import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
//import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder;
//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
//import com.nikhil.sonicmuse.SonicMuseApplication;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.time.Instant;
//
//public class AsynchronousLambdaHandler implements RequestStreamHandler
//{
//    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
//
//    static {
//        try {
//            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(SonicMuseApplication.class);
//            // For applications that take longer than 10 seconds to start, use the async builder:
//            handler = new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
//                    .defaultProxy()
//                    .asyncInit()
//                    .springBootApplication(SonicMuseApplication.class)
//                    .buildAndInitialize();
//        } catch (ContainerInitializationException e) {
//            // if we fail here. We re-throw the exception to force another cold start
//            e.printStackTrace();
//            throw new RuntimeException("Could not initialize Spring Boot application", e);
//        }
//    }
//
//    @Override
//    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException
//    {
//        handler.proxyStream(inputStream, outputStream, context);
//    }
//}