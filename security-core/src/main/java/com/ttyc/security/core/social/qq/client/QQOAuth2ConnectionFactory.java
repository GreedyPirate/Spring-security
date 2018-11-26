package com.ttyc.security.core.social.qq.client;

import com.ttyc.security.core.social.qq.serverProvider.api.QQApi;
import com.ttyc.security.core.social.qq.serverProvider.api.QQServiceProvider;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;

public class QQOAuth2ConnectionFactory extends OAuth2ConnectionFactory<QQApi> {
    /**
     * Create a {@link OAuth2ConnectionFactory}.
     */
    public QQOAuth2ConnectionFactory(String providerId, String appId, String secret) {
        super(providerId, new QQServiceProvider(appId, secret), new QQApiAdapter());
    }
}
