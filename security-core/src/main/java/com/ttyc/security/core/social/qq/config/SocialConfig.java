package com.ttyc.security.core.social.qq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

public class SocialConfig extends SocialConfigurerAdapter{

    @Autowired
    private DataSource dataSource;

    @Bean
    public SpringSocialConfigurer imoocSocialSecurityConfig() {
        // 默认配置类，进行组件的组装
        // 包括了过滤器SocialAuthenticationFilter 添加到security过滤链中
        SpringSocialConfigurer springSocialConfigurer = new SpringSocialConfigurer();
        return springSocialConfigurer;
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository usersConnectionRepository =
                new JdbcUsersConnectionRepository(dataSource,connectionFactoryLocator, Encryptors.noOpText());
        usersConnectionRepository.setTablePrefix("i_");
        return super.getUsersConnectionRepository(connectionFactoryLocator);
    }

    @Override
    public UserIdSource getUserIdSource() {

        return new AuthenticationNameUserIdSource();

    }
}
