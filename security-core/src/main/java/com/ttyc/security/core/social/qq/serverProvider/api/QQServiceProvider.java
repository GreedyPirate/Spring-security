package com.ttyc.security.core.social.qq.serverProvider.api;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

public class QQServiceProvider extends AbstractOAuth2ServiceProvider<QQApi>{

    public static final String authorizeUrl = "https://graph.qq.com/oauth2.0/authorize";
    public static final String accessTokenUrl = "https://graph.qq.com/oauth2.0/token";

    private String appId;

    public QQServiceProvider(String appId, String secret) {
        super(new QQOAuth2Template(appId, secret, authorizeUrl,accessTokenUrl));
        this.appId = appId;
    }

    @Override
    public QQApi getApi(String accessToken) {
        return new QQApiImpl(this.appId, accessToken);
    }
}
