package com.nikhil.sonicmuse.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Getter
@RequiredArgsConstructor
public class Attendee
{
    private final WebSocketSession session;

    public String getId()
    {
        return getSession().getId();
    }

    public boolean isOnline()
    {
        return getSession().isOpen();
    }

    public boolean isOffline()
    {
        return !isOnline();
    }

    public void sendMessage(WebSocketMessage<?> message)
    {
        try
        {
            getSession().sendMessage(message);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
