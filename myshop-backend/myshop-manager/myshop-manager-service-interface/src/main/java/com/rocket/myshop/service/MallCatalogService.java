package com.rocket.myshop.service;

import java.util.List;
import java.util.Map;

import com.rocket.myshop.domain.Item;
import com.rocket.myshop.domain.ItemBrand;
import com.rocket.myshop.dto.common.ShopQueryResult;

/**
 * 商城目录浏览服务：只读上架商品/类别/品牌，与后台管理接口（/items/*）语义隔离。
 */
public interface MallCatalogService {

    /**
     * 上架商品分页列表。
     * params: start/length（DataTables 风格），keyword（标题模糊），itemCategoryName，itemBrandName
     */
    ShopQueryResult listItems(Map<String, Object> params);

    /** 上架商品详情，不存在或已下架抛 MallNotFoundException。 */
    Item getItemOnSale(Integer id);

    /** 供购物车/订单服务使用的上架商品加载，语义同 getItemOnSale。 */
    Item requireItemOnSale(Integer itemId);

    /** 两级类别树（status=1）：children 为 level 2 类别。 */
    List<Map<String, Object>> categoryTree();

    /** 在售品牌列表（status=1）。 */
    List<ItemBrand> activeBrands();
}
