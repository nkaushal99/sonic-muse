package com.nikhil.sonicmuse.pojo;

import com.nikhil.sonicmuse.enumeration.WebSocketMessageType;

public class WebSocketBaseResponse
{
    WebSocketMessageType type;
    String partyId;
    String hostId;

    public WebSocketMessageType getType()
    {
        return type;
    }

    public void setType(WebSocketMessageType type)
    {
        this.type = type;
    }

    public String getPartyId()
    {
        return partyId;
    }

    public void setPartyId(String partyId)
    {
        this.partyId = partyId;
    }

    public String getHostId()
    {
        return hostId;
    }

    public void setHostId(String hostId)
    {
        this.hostId = hostId;
    }
}
