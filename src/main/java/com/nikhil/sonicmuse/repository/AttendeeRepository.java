package com.nikhil.sonicmuse.repository;

import com.nikhil.sonicmuse.mapper.AttendeeMapper;

public class AttendeeRepository extends AbstractRepository<AttendeeMapper>
{

    private static AttendeeRepository instance;

    private AttendeeRepository()
    {
        super(AttendeeMapper.class);
    }

    public static AttendeeRepository getInstance()
    {
        if (instance == null)
            instance = new AttendeeRepository();
        return instance;
    }

    public AttendeeMapper findAttendeeById(String connectionId)
    {
        String partitionKey = AttendeeMapper.partitionKeyGen();
        String sortKey = AttendeeMapper.sortKeyGen(connectionId);
        return super.get(partitionKey, sortKey).stream().findFirst().orElse(null);
    }

}
