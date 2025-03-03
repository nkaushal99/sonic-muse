package com.nikhil.sonicmuse.enumeration;

public enum S3BucketType
{
    DATA("sonic-muse-data");

    private final String expandedName;


    S3BucketType(String expandedName)
    {
        this.expandedName = expandedName;
    }

    public String getExpandedName()
    {
        return expandedName;
    }
}
