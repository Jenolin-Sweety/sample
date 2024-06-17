package com.example.usagereporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchedulerUsagereporting {

  public static void main(String[] args) {
    SpringApplication.run(SchedulerUsagereporting.class, args);
  }

}
