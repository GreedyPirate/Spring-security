package com.ttyc.security.demo.service;

import com.ttyc.security.demo.dao.UserMapper;
import com.ttyc.security.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProviderSignInUtils providerSignInUtils;

    public void regist(User user, HttpServletRequest request) {
        userMapper.save(user);

        // 是否是社交登录
        if (user.getRegistType().equals(2)) {
            //用保存到数据库里的id和openid做映射
            providerSignInUtils.doPostSignUp(user.getId().toString(), new ServletWebRequest(request));
        }
    }
}
