package com.nikhil.sonicmuse.repository;

import com.nikhil.sonicmuse.mapper.PartyMapper;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;

public class PartyRepository extends AbstractRepository<PartyMapper>
{

    private static PartyRepository instance;

    private PartyRepository()
    {
        super(PartyMapper.class);
    }

    public static PartyRepository getInstance()
    {
        if (instance == null)
            instance = new PartyRepository();
        return instance;
    }

    public PartyMapper findPartyById(String partyId)
    {
        String partitionKey = PartyMapper.partitionKeyGen();
        String sortKey = PartyMapper.sortKeyGen(partyId);
        return super.get(partitionKey, sortKey).stream().findFirst().orElse(null);
    }

    public SdkIterable<PartyMapper> findAllParties()
    {
        return super.get(PartyMapper.partitionKeyGen(), null);
    }
}
