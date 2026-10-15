package com.rocket.myshop.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品品牌 DTO。
 */
public class ItemBrandDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private Date updateTime;

	private String name;

	private Date createTime;

	private String logo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
}
