package com.ttyc.security.core.config;

import lombok.Data;

@Data
public class BrowserProperties  {

    private String loginPage = "/sg-login.html";

    private LoginRequestType loginType = LoginRequestType.JSON;

    /*@DurationUnit(ChronoUnit.SECONDS)
    private Duration remebermeTime = Duration.ofSeconds(60L);*/

    private int remebermeTime = 60 * 60;
}
