package com.ttyc.securitydemo.interceptor;

import lombok.Data;

@Data
public class ResponseModel<T> {
    private T data;
    private Integer code;
    private String msg;
}