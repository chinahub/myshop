package com.rocket.myshop.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import cn.jiebaba.summer.data.annotation.IdType;
import cn.jiebaba.summer.data.annotation.TableField;
import cn.jiebaba.summer.data.annotation.TableId;
import cn.jiebaba.summer.data.annotation.TableName;

/**
 * SKU 商品实体，对应表 shop_item（列名以下划线结尾）。
 */
@TableName("shop_item")
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id_", type = IdType.AUTO)
    @TableField("id_")
    private Integer id;

    @TableField("title_")
    private String title;

    @TableField("sell_point_")
    private String sellPoint;

    @TableField("price_")
    private BigDecimal price;

    @TableField("num_")
    private Integer num;

    @TableField("barcode_")
    private String barcode;

    @TableField("img_logo_")
    private String imgLogo;

    @TableField("status_")
    private String status;

    @TableField("create_time_")
    private Date createTime;

    @TableField("update_time_")
    private Date updateTime;

    @TableField("seller_id_")
    private String sellerId;

    @TableField("item_category_name_")
    private String itemCategoryName;

    @TableField("item_brand_name_")
    private String itemBrandName;

    @TableField("sale_clients_")
    private String saleClients;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getSellPoint() {
        return sellPoint;
    }

    public void setSellPoint(String sellPoint) {
        this.sellPoint = sellPoint == null ? null : sellPoint.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode == null ? null : barcode.trim();
    }

    public String getImgLogo() {
        return imgLogo;
    }

    public void setImgLogo(String imgLogo) {
        this.imgLogo = imgLogo == null ? null : imgLogo.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId == null ? null : sellerId.trim();
    }

    public String getItemCategoryName() {
        return itemCategoryName;
    }

    public void setItemCategoryName(String itemCategoryName) {
        this.itemCategoryName = itemCategoryName == null ? null : itemCategoryName.trim();
    }

    public String getItemBrandName() {
        return itemBrandName;
    }

    public void setItemBrandName(String itemBrandName) {
        this.itemBrandName = itemBrandName == null ? null : itemBrandName.trim();
    }

    public String getSaleClients() {
        return saleClients;
    }

    public void setSaleClients(String saleClients) {
        this.saleClients = saleClients == null ? null : saleClients.trim();
    }
}
