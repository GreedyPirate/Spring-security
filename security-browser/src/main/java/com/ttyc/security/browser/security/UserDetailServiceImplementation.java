package com.ttyc.security.browser.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

/**
 * 这是一个demo，最后是使用方实现
 */
@Component
@Slf4j
public class UserDetailServiceImplementation implements UserDetailsService, SocialUserDetailsService {

//    private UserDao userDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("user name is {}", username);
        /**
         * 1.密码是用Dao查询出来的，Spring-security会自动比较
         * 2.最后一个参数是角色
         * 3.需要加密，否则提示“Encoded password does not look like BCrypt”(应该在注册的时候加密)
         * 4.返回的User对象还可以根据数据库查询的信息返回，是否被锁定，是否过期等
         * 5.用户被删除了可以用isEnabled表示
         * 6.UserDetails应该重新实现
         */
        return new User(username, passwordEncoder.encode("12345"),
                true,true,true,true,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }


    /**
     * 类似上面的
     * @param userId
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        SocialUser admin = new SocialUser(userId, passwordEncoder.encode("12345"),
                true, true, true, true,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        return admin;
    }
}
