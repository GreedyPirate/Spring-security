package com.ttyc.security.core;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class StringApiTest {
    @Test
    public void testBetween() {
        String ret = "callback( {\"client_id\":\"101514119\",\"openid\":\"3E601BED41D98CB9AE590C258AB4254D\"} );";
        String trim = StringUtils.substringBetween(ret, "callback(", ")" ).trim();
        System.out.println(trim);
    }
}
