package com.uparis.ppd.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    private static final String LOCATION = "";

    public String getLocation() {
        return LOCATION;
    }

}
