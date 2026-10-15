package com.rocket.myshop.dto;

import java.io.Serializable;
import java.util.Date;

import cn.jiebaba.summer.web.validation.NotBlank;

/**
 * 商品类目 DTO，name 必填（Summer 校验注解）。
 */
public class ItemCategoryDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	@NotBlank(message = "商品类别名称不能为空")
	private String name;

	private Boolean status;

	private Date createTime;

	private Date updateTime;

	private Integer parentId;

	private Integer itemsNum;

	private Short rank;

	private Byte level;

	private Short subCategoryNum;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
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

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getItemsNum() {
		return itemsNum;
	}

	public void setItemsNum(Integer itemsNum) {
		this.itemsNum = itemsNum;
	}

	public Short getRank() {
		return rank;
	}

	public void setRank(Short rank) {
		this.rank = rank;
	}

	public Byte getLevel() {
		return level;
	}

	public void setLevel(Byte level) {
		this.level = level;
	}

	public Short getSubCategoryNum() {
		return subCategoryNum;
	}

	public void setSubCategoryNum(Short subCategoryNum) {
		this.subCategoryNum = subCategoryNum;
	}
}
