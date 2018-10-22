package com.ttyc.security.browser.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .httpBasic() // basic认证
                .formLogin()
                .and()
                .authorizeRequests() //
                .anyRequest() //所有的请求
                .authenticated(); // 指定url可以被所有已认证用户访问
    }
}
