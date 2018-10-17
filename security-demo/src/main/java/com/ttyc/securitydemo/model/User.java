package com.ttyc.securitydemo.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class User {

    private Long id;

    @NotBlank(message = "请输入用户名")
    private String username;

    private String password;
}
