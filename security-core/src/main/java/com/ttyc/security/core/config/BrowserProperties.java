package com.ttyc.security.core.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;

@Data
public class BrowserProperties  {

    private String loginPage = "/sg-login.html";

    private LoginRequestType loginType;


}
