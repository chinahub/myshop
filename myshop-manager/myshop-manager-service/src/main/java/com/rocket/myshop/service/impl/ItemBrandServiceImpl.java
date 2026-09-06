package com.rocket.myshop.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.rocket.myshop.dto.ItemBrandDto;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.mapper.ItemBrandMapper;
import com.rocket.myshop.service.ItemBrandService;

@Service("ItemBrandService")
public class ItemBrandServiceImpl implements ItemBrandService{
	
	@Resource
	ItemBrandMapper itemBrandMapper;

	@Override
	public ShopQueryResult listItemBrands(Map<String, Object> params) {
		int count = itemBrandMapper.getCount(params);
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal(count);
		if (count > 0) {
			PageParams.normalize(params);
			List<ItemBrandDto> data = itemBrandMapper.listItemBrand(params);
			result.setList(data);
		}
		return result;
	}

}
