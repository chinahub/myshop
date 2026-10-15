package com.rocket.myshop.service;


import java.util.Map;

import com.rocket.myshop.domain.Item;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;

public interface ItemService {

	ShopQueryResult listItem(Map<String, Object> params);

	Item getItem(Integer id);

	ShopTxResult addItem(Item item);

	ShopTxResult updateItem(Item item);

	ShopTxResult deleteItem(Integer id);
}
