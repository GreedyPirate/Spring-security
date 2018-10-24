package com.ttyc.security.core.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.PathVariable;

@Data
@ConfigurationProperties("i-security")
public class SecurityProperties implements InitializingBean {

    // 必须new
    private BrowserProperties browser = new BrowserProperties();

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
