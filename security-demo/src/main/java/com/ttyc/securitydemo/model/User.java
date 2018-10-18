package com.ttyc.securitydemo.model;

import com.ttyc.securitydemo.validator.In;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class User {

    private Long id;

    @NotBlank(message = "请输入用户名")
    private String username;

    private String password;

    @In(values = {1,2,3}, message = "非法的用户类型")
    private Integer type;
}
