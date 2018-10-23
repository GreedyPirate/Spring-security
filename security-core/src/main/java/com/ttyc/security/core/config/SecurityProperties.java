package com.ttyc.security.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("i-security")
public class SecurityProperties {

    BrowserProperties browser;
}
