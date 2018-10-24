package com.ttyc.security.core.config;

/**
 * 登录成功之后，前端需要JSON，还是重定向到原地址
 * JSON是不是原地址返回？
 * 失败就不需要了，前端自己跳登录页
 *
 * @author yangjie
 * @since 1.0.0
 * @createTime 2018/10/24
 */
public enum LoginRequestType {

    JSON,

    REDIRECT
}
