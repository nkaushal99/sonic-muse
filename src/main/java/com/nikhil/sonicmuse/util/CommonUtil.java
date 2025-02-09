package com.nikhil.sonicmuse.util;

import java.util.UUID;

public class CommonUtil
{

    /**
     * To be on a safer side, before saving, check in the db for any object with same key to prevent collision
     */
    public static String generateId(String prefix)
    {
        StringBuilder builder = new StringBuilder();

        if (prefix != null)
            builder.append(prefix);

        UUID uuid = UUID.randomUUID();
        builder.append(uuid);
        return builder.toString();
    }

    public static String generateId()
    {
        return generateId(null);
    }
}
