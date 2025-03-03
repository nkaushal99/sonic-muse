package com.nikhil.sonicmuse.enumeration;

public enum KeyPrefix
{
    SONG("SONG"),
    PARTY("PARTY"),
    ATTENDEE("ATTENDEE");

    private final String prefix;

    KeyPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public String keyGen(String... items)
    {
        if (items == null)
            return prefix;

        StringBuilder result = new StringBuilder(prefix);
        for (String item : items)
            result.append(item);
        return result.toString();
    }
}
