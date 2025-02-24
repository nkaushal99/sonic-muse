package com.nikhil.sonicmuse.pojo;

import com.nikhil.sonicmuse.util.CommonUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class Party
{
    private final String id;
    private final Set<Attendee> attendees;

    @Setter
    private String hostId;

    private Party(Attendee host)
    {
        this.id = CommonUtil.generateId();
        this.attendees = ConcurrentHashMap.newKeySet();
        setHostId(host.getSession().getId());
        this.addAttendee(host);
    }

    public static Party host(Attendee host)
    {
        return new Party(host);
    }

    public Set<Attendee> getGuests()
    {
        return getAttendees().stream()
                .filter(attendee -> !attendee.getId().equals(getHostId()))
                .collect(Collectors.toSet());
    }

    public Optional<Attendee> getHost()
    {
        return getAttendees().stream()
                .filter(attendee -> attendee.getId().equals(getHostId()))
                .findFirst();
    }

    public void changeHost(String newHostId)
    {
        setHostId(newHostId);
    }

    public boolean isHost(String connectionId)
    {
        return connectionId.equals(getHostId());
    }

    public void addAttendee(Attendee toAdd)
    {
        getAttendees().add(toAdd);
    }

    public void removeAttendee(Attendee toRemove)
    {
        getAttendees().remove(toRemove);
    }
}
