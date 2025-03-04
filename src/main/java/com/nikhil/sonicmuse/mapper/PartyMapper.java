package com.nikhil.sonicmuse.mapper;


import com.nikhil.sonicmuse.enumeration.KeyPrefix;
import com.nikhil.sonicmuse.util.CommonUtil;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@DynamoDbBean
public class PartyMapper extends AbstractMapper
{
    private static final KeyPrefix KEY_PREFIX = KeyPrefix.PARTY;

    private String id;
    private Set<String> attendeeIds;
    private String hostId;
    private boolean markForDeletion;

    public PartyMapper()
    {
        this.setId(CommonUtil.generateId());
//        this.setAttendees(new HashSet<>());
        this.buildPartitionKey();
        this.buildSortKey();
    }

    @Override
    public void buildPartitionKey()
    {
        setPartitionKey(partitionKeyGen());
    }

    @Override
    public void buildSortKey()
    {
        setSortKey(sortKeyGen(id));
    }

    public static String partitionKeyGen()
    {
        return KEY_PREFIX.keyGen();
    }

    public static String sortKeyGen(String id)
    {
        return KEY_PREFIX.keyGen(id);
    }

//    public static Party host(Attendee host)
//    {
//        return new Party(host);
//    }

    public Set<String> getGuests()
    {
        return getAttendeeIds().stream()
                .filter(id -> !id.equals(getHostId()))
                .collect(Collectors.toSet());
    }

    public void changeHost(String newHostId)
    {
        if (getAttendeeIds().stream().anyMatch(id -> id.equals(newHostId)))
            setHostId(newHostId);
        else
            throw new RuntimeException("New host is not a part of the party yet!");
    }

    public boolean isHost(String connectionId)
    {
        return connectionId.equals(getHostId());
    }

    public void addAttendee(String attendeeId)
    {
        Set<String> attendeeIds = getAttendeeIds();
        if (attendeeIds == null)
        {
            attendeeIds = new HashSet<>();
        }
        attendeeIds.add(attendeeId);
        if (attendeeIds.size() == 1)
        {
            setHostId(attendeeId);
        }
        setAttendeeIds(attendeeIds);
    }

    public void removeAttendee(String attendeeId)
    {
        getAttendeeIds().remove(attendeeId);

        // if party has no attendee, then delete party
        if (getAttendeeIds().isEmpty())
            this.markForDeletion();

        // todo check if host was removed
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Set<String> getAttendeeIds()
    {
        return attendeeIds;
    }

    public void setAttendeeIds(Set<String> attendeeIds)
    {
        this.attendeeIds = attendeeIds;
    }

    public String getHostId()
    {
        return hostId;
    }

    public void setHostId(String hostId)
    {
        this.hostId = hostId;
    }

    @DynamoDbIgnore
    public boolean isMarkedForDeletion()
    {
        return markForDeletion;
    }

    private void markForDeletion()
    {
        this.markForDeletion = true;
    }
}
