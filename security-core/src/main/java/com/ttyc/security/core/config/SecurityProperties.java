package com.ttyc.security.core.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("i-security")
public class SecurityProperties implements InitializingBean {

    // 必须new
    private BrowserProperties browser = new BrowserProperties();

    private SocialProperties social = new SocialProperties();

    /**
     * 属性检查
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 防止有的人没加 /
        String loginPage = browser.getLoginPage();
        if(!loginPage.startsWith("/")) {
            browser.setLoginPage("/" + loginPage);
        }
    }
}
