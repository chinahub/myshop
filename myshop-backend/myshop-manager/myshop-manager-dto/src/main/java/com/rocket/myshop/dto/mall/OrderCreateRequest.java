package com.rocket.myshop.dto.mall;

import java.io.Serializable;

import cn.jiebaba.summer.web.validation.NotBlank;
import cn.jiebaba.summer.web.validation.Size;

/**
 * 提交订单请求：从当前会员购物车全部条目生成订单。
 */
public class OrderCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 40, message = "收货人姓名过长")
    private String consigneeName;

    @NotBlank(message = "收货人电话不能为空")
    @Size(max = 20, message = "收货人电话过长")
    private String consigneePhone;

    @NotBlank(message = "收货地址不能为空")
    @Size(max = 200, message = "收货地址过长")
    private String consigneeAddress;

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }
}
