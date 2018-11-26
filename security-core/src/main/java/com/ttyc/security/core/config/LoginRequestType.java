package com.ttyc.security.core.config;

/**
 * 登录成功之后，前端需要JSON，还是重定向到原地址
 * JSON是不是原地址返回？
 * 失败就不需要了，前端自己跳登录页
 *
 * @author yangjie
 * @createTime 2018/10/24
 * @since 1.0.0
 */
public enum LoginRequestType {

    JSON,

    REDIRECT
}
