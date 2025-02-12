package com.nikhil.sonicmuse.util;

import java.util.UUID;

public class CommonUtil
{
    /**
     * To be on a safer side, before saving, check in the db for any object with same key to prevent collision
     */
    public static String generateId()
    {
        return UUID.randomUUID().toString();
    }
}
