package com.meeting.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "meeting.email")
public class EmailProperties {
    private String emailProtocol;
    private String emailSMTPHost;
    private String emailSMTPPort;
    private String emailAccount;
    private String emailPassword;
}
