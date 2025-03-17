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
    private String name;
    private String partyId;

    public AttendeeMapper()
    {

    }

    public AttendeeMapper(AttendeeDTO attendeeDTO)
    {
        this(attendeeDTO.getId());
        this.name = attendeeDTO.getName();
        this.partyId = attendeeDTO.getPartyId();
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
        attendeeDTO.setName(name);
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPartyId()
    {
        return partyId;
    }

    public void setPartyId(String partyId)
    {
        this.partyId = partyId;
    }
}
