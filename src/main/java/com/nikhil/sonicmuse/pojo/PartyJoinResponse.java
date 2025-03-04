package com.nikhil.sonicmuse.pojo;

import com.nikhil.sonicmuse.enumeration.PlayerMessageType;

public class PartyJoinResponse
{
    PlayerMessageType type;
    String partyId;
    String hostId;

    public PlayerMessageType getType()
    {
        return type;
    }

    public void setType(PlayerMessageType type)
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
