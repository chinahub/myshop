package com.rocket.myshop.exception;

/**
 * 商城资源（商品/订单/购物车条目）不存在或不可见：映射为 404，
 * message 为面向前端的中文提示。
 */
public class MallNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MallNotFoundException(String message) {
        super(message);
    }
}
