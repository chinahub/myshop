package com.rocket.myshop.dto.mall;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车条目视图：购物车行 + 商品当前信息（价格/主图/库存为实时值，非快照）。
 */
public class CartItemView implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer itemId;

    private Integer num;

    private String title;

    private String sellPoint;

    private BigDecimal price;

    private String imgLogo;

    private Integer stock;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSellPoint() {
        return sellPoint;
    }

    public void setSellPoint(String sellPoint) {
        this.sellPoint = sellPoint;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImgLogo() {
        return imgLogo;
    }

    public void setImgLogo(String imgLogo) {
        this.imgLogo = imgLogo;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
