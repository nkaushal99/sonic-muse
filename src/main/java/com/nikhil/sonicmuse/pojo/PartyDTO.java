package com.nikhil.sonicmuse.pojo;

import java.util.Set;

public class PartyDTO
{
    private String id;
    private Set<AttendeeDTO> attendees;
    private String hostId;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Set<AttendeeDTO> getAttendees()
    {
        return attendees;
    }

    public void setAttendees(Set<AttendeeDTO> attendees)
    {
        this.attendees = attendees;
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