package com.ttyc.security.demo.dao;

import com.ttyc.security.demo.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface UserMapper {

    @Insert("insert into i_user (username, password, regist_type) values(#{username}, #{password}, #{registType})")
    @Options(useGeneratedKeys = true)
    void save(User user);
}
