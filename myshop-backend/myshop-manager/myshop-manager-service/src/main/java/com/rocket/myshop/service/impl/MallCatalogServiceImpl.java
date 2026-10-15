package com.rocket.myshop.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rocket.myshop.domain.Item;
import com.rocket.myshop.domain.ItemBrand;
import com.rocket.myshop.domain.ItemCategory;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.exception.MallNotFoundException;
import com.rocket.myshop.mapper.ItemBrandMapper;
import com.rocket.myshop.mapper.ItemCategoryMapper;
import com.rocket.myshop.mapper.ItemMapper;
import com.rocket.myshop.service.MallCatalogService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.core.annotation.Service;
import cn.jiebaba.summer.data.conditions.LambdaQueryWrapper;
import cn.jiebaba.summer.data.page.IPage;
import cn.jiebaba.summer.data.page.Page;
import cn.jiebaba.summer.data.transaction.Transactional;

@Service("MallCatalogService")
@Transactional(readOnly = true)
public class MallCatalogServiceImpl implements MallCatalogService {

	@Autowired
	ItemMapper itemMapper;

	@Autowired
	ItemCategoryMapper itemCategoryMapper;

	@Autowired
	ItemBrandMapper itemBrandMapper;

	@Override
	public ShopQueryResult listItems(Map<String, Object> params) {
		LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<Item>()
				.eq(Item::getStatus, "1");
		Object keyword = params.get("keyword");
		if (keyword instanceof String s && !s.isBlank()) {
			wrapper.like(Item::getTitle, s.trim());
		}
		Object category = params.get("itemCategoryName");
		if (category instanceof String s && !s.isBlank()) {
			wrapper.eq(Item::getItemCategoryName, s.trim());
		}
		Object brand = params.get("itemBrandName");
		if (brand instanceof String s && !s.isBlank()) {
			wrapper.eq(Item::getItemBrandName, s.trim());
		}
		wrapper.orderByDesc(Item::getId);
		long count = itemMapper.selectCount(wrapper);
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal((int) count);
		if (count > 0) {
			PageParams.normalize(params);
			int start = (Integer) params.get("start");
			int length = (Integer) params.get("length");
			IPage<Item> page = itemMapper.selectPage(new Page<>(length > 0 ? start / length + 1 : 1, length),
					wrapper);
			result.setList(page.records());
		}
		result.setSuccess(true);
		result.setVersion("1.0.0");
		return result;
	}

	@Override
	public Item getItemOnSale(Integer id) {
		return requireItemOnSale(id);
	}

	@Override
	public Item requireItemOnSale(Integer itemId) {
		Item item = itemMapper.selectById(itemId);
		if (item == null || !"1".equals(item.getStatus())) {
			throw new MallNotFoundException("商品不存在或已下架");
		}
		return item;
	}

	@Override
	public List<Map<String, Object>> categoryTree() {
		List<ItemCategory> tops = itemCategoryMapper.selectList(new LambdaQueryWrapper<ItemCategory>()
				.eq(ItemCategory::getLevel, (byte) 1)
				.eq(ItemCategory::getStatus, Boolean.TRUE)
				.orderByAsc(ItemCategory::getId));
		List<ItemCategory> subs = itemCategoryMapper.selectList(new LambdaQueryWrapper<ItemCategory>()
				.eq(ItemCategory::getLevel, (byte) 2)
				.eq(ItemCategory::getStatus, Boolean.TRUE)
				.orderByAsc(ItemCategory::getId));
		Map<Integer, Map<String, Object>> topIndex = new LinkedHashMap<>();
		Map<Integer, List<Map<String, Object>>> childrenByTop = new LinkedHashMap<>();
		for (ItemCategory top : tops) {
			Map<String, Object> node = new LinkedHashMap<>();
			node.put("id", top.getId());
			node.put("name", top.getName());
			List<Map<String, Object>> children = new ArrayList<>();
			node.put("children", children);
			topIndex.put(top.getId(), node);
			childrenByTop.put(top.getId(), children);
		}
		for (ItemCategory sub : subs) {
			List<Map<String, Object>> children = childrenByTop.get(sub.getParentId());
			if (children == null) {
				continue;
			}
			Map<String, Object> child = new LinkedHashMap<>();
			child.put("id", sub.getId());
			child.put("name", sub.getName());
			children.add(child);
		}
		return new ArrayList<>(topIndex.values());
	}

	@Override
	public List<ItemBrand> activeBrands() {
		return itemBrandMapper.selectList(new LambdaQueryWrapper<ItemBrand>()
				.eq(ItemBrand::getStatus, Boolean.TRUE)
				.orderByAsc(ItemBrand::getId));
	}
}
