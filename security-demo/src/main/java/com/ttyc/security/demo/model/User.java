package com.ttyc.security.demo.model;

import com.ttyc.security.demo.validator.NewUser;
import com.ttyc.security.demo.validator.In;
import com.ttyc.security.demo.validator.RMBUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;

@Data
public class User {

    private Long id;

    @NotBlank(groups = {Default.class}, message = "请输入用户名")
    private String username;

    @NotBlank(groups = {NewUser.class}, message = "请输入密码")
    private String password;

    @In(groups = {RMBUser.class}, values = {1,2,3}, message = "非法的用户类型")
    private Integer type;
}
