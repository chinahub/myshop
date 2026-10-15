package com.rocket.myshop.service;

import java.util.Map;

import com.rocket.myshop.domain.ItemBrand;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;

public interface ItemBrandService {

	ShopQueryResult listItemBrands(Map<String, Object> params);

	ShopTxResult addItemBrand(ItemBrand brand);

	ShopTxResult updateItemBrand(ItemBrand brand);

	ShopTxResult deleteItemBrand(Integer id);
}
