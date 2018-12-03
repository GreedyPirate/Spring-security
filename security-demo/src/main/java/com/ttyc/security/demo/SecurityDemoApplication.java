package com.ttyc.security.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ttyc.security.browser", "com.ttyc.security.demo", "com.ttyc.security.core"})
@MapperScan("com.ttyc.security.demo.dao" )
public class SecurityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityDemoApplication.class, args);
    }
}
