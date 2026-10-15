package com.rocket.myshop.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import cn.jiebaba.summer.data.annotation.IdType;
import cn.jiebaba.summer.data.annotation.TableField;
import cn.jiebaba.summer.data.annotation.TableId;
import cn.jiebaba.summer.data.annotation.TableName;

/**
 * 订单实体，对应表 mall_order。
 * 状态：1 待付款，2 已付款，3 已取消（模拟支付，不接真实支付渠道）。
 */
@TableName("mall_order")
public class MallOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int STATUS_UNPAID = 1;
    public static final int STATUS_PAID = 2;
    public static final int STATUS_CANCELLED = 3;

    @TableId(value = "id_", type = IdType.AUTO)
    @TableField("id_")
    private Integer id;

    @TableField("order_no_")
    private String orderNo;

    @TableField("user_id_")
    private Integer userId;

    @TableField("total_amount_")
    private BigDecimal totalAmount;

    @TableField("status_")
    private Integer status;

    @TableField("consignee_name_")
    private String consigneeName;

    @TableField("consignee_phone_")
    private String consigneePhone;

    @TableField("consignee_address_")
    private String consigneeAddress;

    @TableField("create_time_")
    private Date createTime;

    @TableField("update_time_")
    private Date updateTime;

    @TableField("pay_time_")
    private Date payTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName == null ? null : consigneeName.trim();
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone == null ? null : consigneePhone.trim();
    }

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress == null ? null : consigneeAddress.trim();
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

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }
}
