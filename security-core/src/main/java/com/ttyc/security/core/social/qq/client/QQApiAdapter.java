package com.ttyc.security.core.social.qq.client;

import com.ttyc.security.core.social.qq.serverProvider.api.QQApi;
import com.ttyc.security.core.social.qq.serverProvider.api.QQUserInfo;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

/**
 * userInfo的适配器
 */
public class QQApiAdapter implements ApiAdapter<QQApi> {
    @Override
    public boolean test(QQApi api) {
        return false;
    }

    @Override
    public void setConnectionValues(QQApi api, ConnectionValues values) {
        QQUserInfo userInfo = api.getUserInfo();
        values.setDisplayName(userInfo.getNickname());
        values.setImageUrl(userInfo.getFigureurl_qq_1());
        values.setProfileUrl(null);
        values.setProviderUserId(userInfo.getOpenId());

    }

    @Override
    public UserProfile fetchUserProfile(QQApi api) {
        return null;
    }

    @Override
    public void updateStatus(QQApi api, String message) {

    }
}
