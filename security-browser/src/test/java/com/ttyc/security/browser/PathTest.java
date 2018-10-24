package com.ttyc.security.browser;

import org.junit.Test;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

public class PathTest {

    @Test
    public void testPath(){
        String url = "user/1";
        String pattern = "user/*";
        AntPathMatcher matcher = new AntPathMatcher();
        Assert.isTrue(matcher.match(pattern,url), "匹配失败");
    }
}
