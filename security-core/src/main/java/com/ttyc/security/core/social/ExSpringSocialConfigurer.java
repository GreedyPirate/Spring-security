package com.ttyc.security.core.social;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

public class ExSpringSocialConfigurer extends SpringSocialConfigurer {

    private String filterProcessesUrl;

    public ExSpringSocialConfigurer(String filterProcessesUrl) {
        this.filterProcessesUrl = filterProcessesUrl;
    }

    @Override
    protected <T> T postProcess(T object) {
        T t = super.postProcess(object);
        SocialAuthenticationFilter filter = (SocialAuthenticationFilter) t;
        filter.setFilterProcessesUrl(filterProcessesUrl);
        return (T) filter;
    }
}
