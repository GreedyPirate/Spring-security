package com.ttyc.security.core.config;

import lombok.Data;

@Data
public class BrowserProperties  {

    private String loginPage = "/sg-login.html";

    private LoginRequestType loginType;


}
