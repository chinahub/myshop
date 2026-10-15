package com.rocket.myshop.domain;

import java.io.Serializable;
import java.util.Date;

import cn.jiebaba.summer.data.annotation.IdType;
import cn.jiebaba.summer.data.annotation.TableField;
import cn.jiebaba.summer.data.annotation.TableId;
import cn.jiebaba.summer.data.annotation.TableName;

/**
 * 后台用户实体，对应表 shop_user（userid_ 为业务主键，非自增）。
 */
@TableName("shop_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "userid_", type = IdType.INPUT)
    @TableField("userid_")
    private String userid;

    @TableField("username_")
    private String username;

    @TableField("password_")
    private String password;

    @TableField("op_date_")
    private Date opDate;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }
}
