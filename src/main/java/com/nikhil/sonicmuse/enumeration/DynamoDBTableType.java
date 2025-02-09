package com.nikhil.sonicmuse.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DynamoDBTableType
{
    MAIN("sonicMuseMain");

    private final String expandedName;
}
