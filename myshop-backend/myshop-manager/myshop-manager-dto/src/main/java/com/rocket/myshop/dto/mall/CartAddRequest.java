package com.rocket.myshop.dto.mall;

import java.io.Serializable;

import cn.jiebaba.summer.web.validation.Max;
import cn.jiebaba.summer.web.validation.Min;
import cn.jiebaba.summer.web.validation.NotNull;

/**
 * 加入购物车请求。
 */
public class CartAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "商品ID不能为空")
    private Integer itemId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为1")
    @Max(value = 99, message = "单次加购数量最多99")
    private Integer num;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
