package com.nikhil.sonicmuse.enumeration;

public enum DynamoDBTableType
{
    MAIN("sonicMuseMain");

    private final String expandedName;

    DynamoDBTableType(String expandedName)
    {
        this.expandedName = expandedName;
    }

    public String getExpandedName()
    {
        return expandedName;
    }
}
