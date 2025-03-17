package com.nikhil.sonicmuse.handler;

import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.nikhil.sonicmuse.resource.PartyResource;
import com.nikhil.sonicmuse.resource.SongResource;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JerseyHandler implements RequestStreamHandler
{
    private static final ResourceConfig jerseyApplication = new ResourceConfig()
            .register(CorsFilter.class)
            .register(PartyResource.class)
            .register(SongResource.class);

    private static final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler =
            JerseyLambdaContainerHandler.getAwsProxyHandler(jerseyApplication);

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException
    {
        handler.proxyStream(inputStream, outputStream, context);
    }

    public static class CorsFilter implements ContainerResponseFilter
    {

        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
        {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
//            responseContext.getHeaders().add("Access-Control-Expose-Headers", "custom-header");

            //Handle OPTIONS requests within lambda.
            if (requestContext.getMethod().equals("OPTIONS"))
            {
                responseContext.setStatus(204);
            }
        }
    }
}