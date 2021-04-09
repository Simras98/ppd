package com.uparis.ppd;

import com.uparis.ppd.properties.ConstantProperties;
import com.uparis.ppd.properties.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ConstantProperties.class, StorageProperties.class})
public class OurassoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OurassoApplication.class, args);
    }
}
