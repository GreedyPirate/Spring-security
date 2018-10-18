package com.ttyc.securitydemo.error;

public enum UserError implements ExceptionEntity {

    NO_SUCH_USER(1, "用户不存在"),
    ERROR_PASSWORD(2, "密码错误"),
    ;

    private final Integer MODULE = 10000;

    private Integer code;

    private String msg;

    UserError(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return MODULE + this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
