package com.rocket.myshop.dto.mall;

import java.io.Serializable;

import cn.jiebaba.summer.web.validation.Max;
import cn.jiebaba.summer.web.validation.Min;
import cn.jiebaba.summer.web.validation.NotNull;

/**
 * 更新购物车条目数量请求。
 */
public class CartUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "购物车条目ID不能为空")
    private Integer cartId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为1")
    @Max(value = 99, message = "单个商品最多购买99件")
    private Integer num;

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
