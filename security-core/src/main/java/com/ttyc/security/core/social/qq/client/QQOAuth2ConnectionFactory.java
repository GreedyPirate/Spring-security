package com.ttyc.security.core.social.qq.client;

import com.ttyc.security.core.social.qq.serverProvider.api.QQApi;
import com.ttyc.security.core.social.qq.serverProvider.api.QQServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class QQOAuth2ConnectionFactory extends OAuth2ConnectionFactory<QQApi> {
    /**
     * Create a {@link OAuth2ConnectionFactory}.
     *
     * @param providerId      the provider id e.g. "facebook"
     * @param serviceProvider the ServiceProvider model for conducting the authorization flow and obtaining a native service API instance.
     * @param apiAdapter      the ApiAdapter for mapping the provider-specific service API model to the uniform {@link Connection} interface.
     */
    public QQOAuth2ConnectionFactory(String providerId, String appId, String secret) {
        super(providerId, new QQServiceProvider(appId,secret), new QQApiAdapter());
    }
}
