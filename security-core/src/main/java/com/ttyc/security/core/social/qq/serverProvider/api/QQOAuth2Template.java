package com.ttyc.security.core.social.qq.serverProvider.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

public class QQOAuth2Template extends OAuth2Template {

    public QQOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
        super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
        // 设置为true，获取accessToken时才会加client_id, client_key两个参数
        this.setUseParametersForClientAuthentication(true);
    }

    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = super.createRestTemplate();
        // 默认是ISO_8859_1
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8" )));
        return restTemplate;
    }

    @Override
    protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        String result = this.getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
        String[] items = StringUtils.splitByWholeSeparatorPreserveAllTokens(result, "&" );
        String accessToken = StringUtils.substringAfterLast(items[0], "=" );
        String expiresIn = StringUtils.substringAfterLast(items[1], "=" );
        String refreshToken = StringUtils.substringAfterLast(items[2], "=" );

        return new AccessGrant(accessToken, null, refreshToken, Long.parseLong(expiresIn));
    }
}
