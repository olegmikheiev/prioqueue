package com.hanno.prioqueue.config;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriorityQueueApplicationConfig {

    @Bean
    public Mapper mapper() {
        return new DozerBeanMapper();
    }

}
