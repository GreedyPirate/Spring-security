package com.ttyc.security.core.social;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

public class ExSpringSocialConfigurer extends SpringSocialConfigurer {
    @Override
    protected <T> T postProcess(T object) {
        T t = super.postProcess(object);
        SocialAuthenticationFilter filter = (SocialAuthenticationFilter) t;
        filter.setFilterProcessesUrl("/login/oauth");
        return (T)filter;
    }
}
