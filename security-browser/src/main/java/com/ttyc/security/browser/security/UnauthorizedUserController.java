package com.ttyc.security.browser.security;

import com.alibaba.fastjson.JSONObject;
import com.ttyc.security.core.config.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
