package com.ttyc.security.browser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SecurityBrowserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityBrowserApplication.class, args);
    }

    @GetMapping("test")
    public String test(){
        return "test";
    }
}
