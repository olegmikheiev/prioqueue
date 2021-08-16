package com.hanno.prioqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class})
public class PriorityQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(PriorityQueueApplication.class, args);
    }

}
