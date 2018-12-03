package com.ttyc.security.core.social.qq.config;

import com.ttyc.security.core.config.QQProperties;
import com.ttyc.security.core.config.SecurityProperties;
import com.ttyc.security.core.social.qq.client.QQOAuth2ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;

@Configuration
@ConditionalOnProperty(prefix = "i-security.social.qq", name = "app-id" )
public class QQAutoConfig extends SocialConfigurerAdapter {
    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(this.buildConnectionFactory());
    }

    public ConnectionFactory buildConnectionFactory() {
        QQProperties qq = securityProperties.getSocial().getQq();

        String appId = qq.getAppId();
        String appKey = qq.getAppKey();
        String providerId = securityProperties.getSocial().getQq().getProviderId();

        return new QQOAuth2ConnectionFactory(providerId, appId, appKey);
    }

    // 后补：做到处理注册逻辑的时候发现的一个bug：登录完成后，数据库没有数据，但是再次登录却不用注册了
    // 就怀疑是否是在内存中存储了。结果果然发现这里父类的内存ConnectionRepository覆盖了SocialConfig中配置的jdbcConnectionRepository
    // 这里需要返回null，否则会返回内存的 ConnectionRepository
    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return null;
    }
}
