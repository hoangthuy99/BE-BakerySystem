package com.ra.bakerysystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.time")
@Getter
@Setter
public class TimeProperties {
    private String zone;
}
