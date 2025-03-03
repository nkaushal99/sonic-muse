package com.nikhil.sonicmuse.repository;

import com.nikhil.sonicmuse.mapper.SongMapper;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;

public class SongRepository extends AbstractRepository<SongMapper>
{

    private static SongRepository instance;

    private SongRepository()
    {
        super(SongMapper.class);
    }

    public static SongRepository getInstance()
    {
        if (instance == null)
            instance = new SongRepository();
        return instance;
    }

    public SongMapper findSongById(String songId)
    {
        String partitionKey = SongMapper.partitionKeyGen();
        String sortKey = SongMapper.sortKeyGen(songId);
        return super.get(partitionKey, sortKey).stream().findFirst().orElse(null);
    }

    public SdkIterable<SongMapper> findAllSongs()
    {
        return super.get(SongMapper.partitionKeyGen(), null);
    }
}
