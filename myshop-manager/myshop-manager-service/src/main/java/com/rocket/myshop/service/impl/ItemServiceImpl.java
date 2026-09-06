package com.rocket.myshop.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rocket.myshop.dto.ItemDto;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.mapper.ItemMapper;
import com.rocket.myshop.service.ItemService;

@Service("ItemService")
public class ItemServiceImpl implements ItemService {

	@Resource
	ItemMapper itemMapper;

	@Override
	public ShopQueryResult listItem(Map<String, Object> params) {
		int count = itemMapper.getCount(params);
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal(count);
		if (count > 0) {
			PageParams.normalize(params);
			List<ItemDto> items = itemMapper.listItem(params);
			result.setList(items);
		}
		return result;
	}

}
