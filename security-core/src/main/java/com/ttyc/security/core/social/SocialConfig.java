package com.ttyc.security.core.social;

import com.ttyc.security.core.config.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 过滤器SocialAuthenticationFilter 添加到security过滤链中
     *
     * @return
     */
    @Bean
    public SpringSocialConfigurer springSocialConfigurer() {
        // 默认配置类，进行组件的组装
        SpringSocialConfigurer springSocialConfigurer = new ExSpringSocialConfigurer("/login/oauth" );
        springSocialConfigurer.signupUrl(securityProperties.getBrowser().getSignupPage());
        return springSocialConfigurer;
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository usersConnectionRepository =
                new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
        usersConnectionRepository.setTablePrefix("i_" );

        return usersConnectionRepository;
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    //    @Bean
    public ConnectController connectController(
            ConnectionFactoryLocator connectionFactoryLocator,
            ConnectionRepository connectionRepository) {
        return new ConnectController(connectionFactoryLocator, connectionRepository);
    }

    @Bean
    public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator locator) {
        return new ProviderSignInUtils(locator, this.getUsersConnectionRepository(locator));
    }
}
