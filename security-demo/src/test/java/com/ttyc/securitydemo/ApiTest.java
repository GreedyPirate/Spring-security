package com.ttyc.securitydemo;

import com.ttyc.securitydemo.model.User;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;

public class ApiTest {

    @Test
    public void testToString(){
        User user = new User();
        user.setPassword("1234");
        user.setUsername("jay");

        System.out.println(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));
    }
}
