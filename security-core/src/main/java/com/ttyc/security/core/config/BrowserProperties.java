package com.ttyc.security.core.config;

import lombok.Data;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
public class BrowserProperties  {

    private String loginPage = "/sg-login.html";

    private LoginRequestType loginType = LoginRequestType.JSON;

    /*@DurationUnit(ChronoUnit.SECONDS)
    private Duration remebermeTime = Duration.ofSeconds(60L);*/

    private int remebermeTime = 60 * 60;
}
