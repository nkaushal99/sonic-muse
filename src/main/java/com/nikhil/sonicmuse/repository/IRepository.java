package com.nikhil.sonicmuse.repository;

import com.nikhil.sonicmuse.mapper.AbstractMapper;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;

public interface IRepository<T extends AbstractMapper>
{
    SdkIterable<T> get(String partitionKey, String sortKey);
    void put(T... mappers);
    void delete(T... mappers);
}
