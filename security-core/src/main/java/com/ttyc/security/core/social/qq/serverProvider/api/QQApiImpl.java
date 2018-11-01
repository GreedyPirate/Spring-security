package com.ttyc.security.core.social.qq.serverProvider.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * API实现，最后一步，用于获取用户信息
 */
public class QQApiImpl extends AbstractOAuth2ApiBinding implements QQApi {

    private static final String OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me?access_token=";

    private static final String USER_INFO_URL = "https://graph.qq.com/user/get_user_info?oauth_consumer_key={oauth_consumer_key}&openid={openid}";

    private String appId;
    private String openid;

    public QQApiImpl(String appId, String accessToken) {
        // 默认是把accessToken放入请求头中
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.appId = appId;

        // 通过accessToken获取openid
        String url = OPEN_ID_URL + accessToken;
        String ret = getRestTemplate().getForObject(url, String.class);

        this.openid = getFieldFromSubJsonString(ret, "callback(", ")", "openid");
    }

    @Override
    public QQUserInfo getUserInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("oauth_consumer_key", appId);
        params.put("openid", this.openid);
        ResponseEntity<String> entity = getRestTemplate().getForEntity(USER_INFO_URL, String.class, params);
        String body = entity.getBody();
        return JSON.parseObject(body, QQUserInfo.class);
    }


    public String getFieldFromSubJsonString(String source, String start, String end, String field){
        String subString = StringUtils.substringBetween(source, start, end).trim();
        JSONObject subJSON = JSONObject.parseObject(subString);
        return subJSON.getString(field);
    }
}
