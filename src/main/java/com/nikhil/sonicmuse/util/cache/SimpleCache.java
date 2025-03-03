package com.nikhil.sonicmuse.util.cache;


public class SimpleCache<T>
{
    private final Factory<T> factory;
    private T instance;

    public SimpleCache(Factory<T> factory)
    {
        this.factory = factory;
    }

    public T getInstance()
    {
        if (instance == null)
        {
            try
            {
                instance = factory.create();
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public interface Factory<T>
    {
        T create() throws Exception;
    }
}
