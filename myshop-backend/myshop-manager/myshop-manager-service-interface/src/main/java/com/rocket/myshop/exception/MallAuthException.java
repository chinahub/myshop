package com.rocket.myshop.exception;

/**
 * 商城认证/授权失败：经 exceptionStatusMapping.properties 按类名映射为 401，
 * message 为面向前端的中文提示（GlobalExceptionHandler 会原样返回）。
 */
public class MallAuthException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MallAuthException(String message) {
        super(message);
    }
}
