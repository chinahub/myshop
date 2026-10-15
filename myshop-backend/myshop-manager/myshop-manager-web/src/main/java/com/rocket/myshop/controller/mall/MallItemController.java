package com.rocket.myshop.controller.mall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rocket.myshop.domain.Item;
import com.rocket.myshop.domain.ItemBrand;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.service.MallCatalogService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.web.annotation.GetMapping;
import cn.jiebaba.summer.web.annotation.RequestMapping;
import cn.jiebaba.summer.web.annotation.RequestParam;
import cn.jiebaba.summer.web.annotation.RestController;

/**
 * 商城商品浏览接口（只读，仅暴露上架商品），与后台管理 /items/* 语义隔离。
 */
@RestController
@RequestMapping("/mall/items")
public class MallItemController {

	@Autowired
	MallCatalogService mallCatalogService;

	@GetMapping("/list.json")
	public ShopQueryResult list(@RequestParam(required = false) Integer start,
			@RequestParam(required = false) Integer length,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String itemCategoryName,
			@RequestParam(required = false) String itemBrandName) {
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("length", length);
		params.put("keyword", keyword);
		params.put("itemCategoryName", itemCategoryName);
		params.put("itemBrandName", itemBrandName);
		return mallCatalogService.listItems(params);
	}

	@GetMapping("/detail.json")
	public Item detail(@RequestParam("id") Integer id) {
		return mallCatalogService.getItemOnSale(id);
	}

	@GetMapping("/categories.json")
	public List<Map<String, Object>> categories() {
		return mallCatalogService.categoryTree();
	}

	@GetMapping("/brands.json")
	public List<ItemBrand> brands() {
		return mallCatalogService.activeBrands();
	}
}
