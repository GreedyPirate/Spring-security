package com.ttyc.security.core.social.qq.serverProvider.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * API实现，最后一步，用于获取用户信息
 */
public class QQApiImpl extends AbstractOAuth2ApiBinding implements QQApi {

    private static final String OPEN_ID_URL = "";

    private static final String USER_INFO_URL = "";

    private String appId;
    private String openid;

    public QQApiImpl(String appId, String accessToken) {
        // 默认是把accessToken放入请求头中
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.appId = appId;

        // 通过accessToken获取openid
        String url = OPEN_ID_URL + accessToken;
        String ret = getRestTemplate().getForObject(url, String.class);
        JSONObject retJson = JSONObject.parseObject(ret);

        this.openid = retJson.getString("openid");
    }

    @Override
    public QQUserInfo getUserInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("oauth_consumer_key", appId);
        params.put("openid", this.openid);
        String ret = getRestTemplate().getForObject(USER_INFO_URL, String.class, params);
        return JSON.parseObject(ret, QQUserInfo.class);
    }
}
