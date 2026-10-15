package com.rocket.myshop.service;


import java.util.Map;

import com.rocket.myshop.dto.ItemCategoryDto;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;

public interface ItemCategoryService {

	ShopQueryResult listItemCategory(Map<String, Object> params);

	ShopQueryResult getSubItemsCategory(Integer parentId);

	ShopTxResult addItemsCategory(ItemCategoryDto itemCategory) throws Exception;

	ShopTxResult deleteItemsCategory(Integer categoryId);

	ShopTxResult updateItemsCategory(ItemCategoryDto itemCategory) throws Exception;

}
