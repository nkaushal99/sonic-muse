package com.nikhil.sonicmuse.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3BucketType
{
    DATA("sonic-muse-data");

    private final String expandedName;
}
