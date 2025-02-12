package com.nikhil.sonicmuse.enumeration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum KeyPrefix
{
    SONG("SONG");

    private final String prefix;

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
