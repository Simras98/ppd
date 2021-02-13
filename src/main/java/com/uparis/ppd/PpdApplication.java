package com.uparis.ppd;

import com.uparis.ppd.properties.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class PpdApplication {

  public static void main(String[] args) {
    SpringApplication.run(PpdApplication.class, args);
  }
}
