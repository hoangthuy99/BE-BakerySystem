package com.ra.bakerysystem;

import com.ra.bakerysystem.config.TimeProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@Log4j2
@EnableConfigurationProperties(TimeProperties.class)
@EnableScheduling
public class BakerySystemApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(BakerySystemApplication.class, args);
    }

}
