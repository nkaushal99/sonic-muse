package com.nikhil.sonicmuse.mapper;

import com.nikhil.sonicmuse.enumeration.KeyPrefix;
import com.nikhil.sonicmuse.pojo.AttendeeDTO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;

@DynamoDbBean
public class AttendeeMapper extends AbstractMapper
{

    private static final KeyPrefix KEY_PREFIX = KeyPrefix.ATTENDEE;

    private String id;
    private String partyId;

    public AttendeeMapper()
    {
    }

    public AttendeeMapper(String connectionId)
    {
        this.setId(connectionId);
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

    @DynamoDbIgnore
    public AttendeeDTO getDTO()
    {
        AttendeeDTO attendeeDTO = new AttendeeDTO();
        attendeeDTO.setId(id);
        attendeeDTO.setPartyId(partyId);
        return attendeeDTO;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPartyId()
    {
        return partyId;
    }

    public void setPartyId(String partyId)
    {
        this.partyId = partyId;
    }

    //    public String getId()
//    {
//        return getSessionId().getId();
//    }
//
//    public boolean isOnline()
//    {
//        return getSessionId().isOpen();
//    }
//
//    public boolean isOffline()
//    {
//        return !isOnline();
//    }
//
//    public void sendMessage(WebSocketMessage<?> message)
//    {
//        try
//        {
//            getSessionId().sendMessage(message);
//        } catch (IOException e)
//        {
//            throw new RuntimeException(e);
//        }
//    }
}
