package com.ttyc.security.browser.config;

import com.ttyc.security.browser.security.UserDetailServiceImplementation;
import com.ttyc.security.core.config.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter{

    @Bean
    public UserDetailsService userService(){
        return new UserDetailServiceImplementation();
    }

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private AuthSuccessHandler authSuccessHandler;

    @Autowired
    private AuthFailHandler authFailHandler;

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String defaultLoginUrl = securityProperties.getBrowser().getLoginPage();

        http
//                .httpBasic()
                // basic认证
                .formLogin()
                //默认登录url，第一个 / 必须有，否则报错isn't a valid redirect URL
                // 重定义，判断请求类型
                .loginPage("/access/authorize")
                //处理登录的接口,默认是/login，参考UsernamePasswordAuthenticationFilter
                //.loginProcessingUrl("/deal-login")
                .loginProcessingUrl("/deal-login")
                .successHandler(authSuccessHandler)
                .failureHandler(authFailHandler)
                .and()
                .userDetailsService(userDetailsService())
                .authorizeRequests()
                // defaultLoginUrl 用户自定义的登录页面也不需要拦截
                .antMatchers(defaultLoginUrl,"/access/authorize").permitAll()
                //所有的请求
                .anyRequest()
                // 指定url可以被所有已认证用户访问
                .authenticated()
                .and()
                .csrf().disable();
    }

    /**
     * 解决There is no PasswordEncoder mapped for the id "null"
     * spring security5之后不推荐NoOpPasswordEncoder
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 直接new BCryptPasswordEncoder()也行
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

}
