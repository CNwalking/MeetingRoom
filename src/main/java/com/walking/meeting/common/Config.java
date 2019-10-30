package com.walking.meeting.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@ConfigurationProperties
public class Config {

    @Value("${spring.datasource.url}")
    private  String DBUrl;
    @Value("${spring.datasource.username}")
    private  String DBUsername;
    @Value("${spring.datasource.password}")
    private  String DBPassword;
}
