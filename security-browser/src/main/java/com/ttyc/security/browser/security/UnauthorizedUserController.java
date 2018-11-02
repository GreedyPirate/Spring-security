package com.ttyc.security.browser.security;

import com.alibaba.fastjson.JSONObject;
import com.ttyc.security.core.config.SecurityProperties;
import com.ttyc.security.core.social.SocialUserInfo;
import com.ttyc.security.core.social.qq.serverProvider.api.QQUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
public class UnauthorizedUserController {

    // 请求缓存在了session中
    RequestCache requestCache = new HttpSessionRequestCache();

    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    SecurityProperties securityProperties;

    /**
     * 根据原地址类型判断未认证的时候的返回
     * 想了想没必要，不管访问接口还是页面，都重定向到登录页
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("access/authorize")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public JSONObject guide(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        // html请求处理
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            if (StringUtils.endsWith(targetUrl, ".html")) {
                // 重定向到配置的登录页
                redirectStrategy.sendRedirect(request, response, securityProperties.getBrowser().getLoginPage());
            }
        }

        // rest请求处理
        JSONObject guideJson = new JSONObject();
        guideJson.put("code", "401");
        guideJson.put("msg", "用户未登录，前端根据此信息跳转至登录页");

        return guideJson;
    }

    @RequestMapping("/v2/access/authorize")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void v2Guide(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 重定向到配置的登录页
        redirectStrategy.sendRedirect(request, response, securityProperties.getBrowser().getLoginPage());
    }

    @Autowired
    private ProviderSignInUtils providerSignInUtils;

    @GetMapping("/social/user")
    public SocialUserInfo getSocialUserInfo(HttpServletRequest request) {
        SocialUserInfo userInfo = new SocialUserInfo();
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        userInfo.setProviderId(connection.getKey().getProviderId());
        userInfo.setProviderUserId(connection.getKey().getProviderUserId());
        userInfo.setNickname(connection.getDisplayName());
        userInfo.setHeadimg(connection.getImageUrl());
        return userInfo;
    }

}
