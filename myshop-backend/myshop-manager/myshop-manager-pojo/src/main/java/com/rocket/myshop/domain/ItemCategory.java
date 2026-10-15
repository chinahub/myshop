package com.rocket.myshop.domain;

import java.io.Serializable;
import java.util.Date;

import cn.jiebaba.summer.data.annotation.IdType;
import cn.jiebaba.summer.data.annotation.TableField;
import cn.jiebaba.summer.data.annotation.TableId;
import cn.jiebaba.summer.data.annotation.TableName;

/**
 * 商品类目实体，对应表 shop_item_category（parent_id_ 自关联成树）。
 */
@TableName("shop_item_category")
public class ItemCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id_", type = IdType.AUTO)
    @TableField("id_")
    private Integer id;

    @TableField("name_")
    private String name;

    @TableField("status_")
    private Boolean status;

    @TableField("create_time_")
    private Date createTime;

    @TableField("update_time_")
    private Date updateTime;

    @TableField("parent_id_")
    private Integer parentId;

    @TableField("items_num_")
    private Integer itemsNum;

    @TableField("rank_")
    private Short rank;

    @TableField("level_")
    private Byte level;

    @TableField("sub_category_num_")
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
        this.name = name == null ? null : name.trim();
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
