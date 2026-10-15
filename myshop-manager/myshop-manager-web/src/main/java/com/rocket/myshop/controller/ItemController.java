package com.rocket.myshop.controller;

import java.util.Map;

import com.rocket.myshop.domain.Item;
import com.rocket.myshop.domain.ItemBrand;
import com.rocket.myshop.dto.ItemCategoryDto;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.service.ItemBrandService;
import com.rocket.myshop.service.ItemCategoryService;
import com.rocket.myshop.service.ItemService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.web.annotation.GetMapping;
import cn.jiebaba.summer.web.annotation.PostMapping;
import cn.jiebaba.summer.web.annotation.RequestBody;
import cn.jiebaba.summer.web.annotation.RequestMapping;
import cn.jiebaba.summer.web.annotation.RequestParam;
import cn.jiebaba.summer.web.annotation.RestController;
import cn.jiebaba.summer.web.validation.Valid;

/**
* @Description: SKU商品管理 REST 接口
 */
@RestController
@RequestMapping("/items")
public class ItemController {

	@Autowired
	ItemService itemService;

	@Autowired
	ItemCategoryService itemCategoryService;

	@Autowired
	ItemBrandService itemBrandService;

	//------------------------- 商品----------------------------------------

	@PostMapping("/listItem.json")
	public ShopQueryResult listItem(@RequestBody Map<String, Object> params) {
		ShopQueryResult result = itemService.listItem(params);
		result.setSuccess(true);
		return result;
	}

	@GetMapping("/detail.json")
	public Item detail(@RequestParam("id") Integer id) {
		return itemService.getItem(id);
	}

	@PostMapping("/deleteItem")
	public ShopTxResult deleteItem(@RequestParam("id") Integer id) {
		return itemService.deleteItem(id);
	}

	@PostMapping("/updateItem")
	public ShopTxResult updateItem(@RequestBody Item input) {
		return itemService.updateItem(input);
	}

	@PostMapping("/addItem")
	public ShopTxResult addItem(@RequestBody Item input) {
		return itemService.addItem(input);
	}

	//------------------------- 商品品牌----------------------------------------

	@PostMapping("/listItemsBrand.json")
	public Object listItemBrand(@RequestBody Map<String, Object> params) {
		ShopQueryResult result = itemBrandService.listItemBrands(params);
		result.setSuccess(true);
		result.setVersion("1.0.0");
		return result;
	}

	@PostMapping("/addItemsBrand.json")
	public ShopTxResult addItemBrand(@RequestBody ItemBrand itemBrand) {
		return itemBrandService.addItemBrand(itemBrand);
	}

	@PostMapping("/updateItemsBrand.json")
	public ShopTxResult updateItemsBrand(@RequestBody ItemBrand itemBrand) {
		return itemBrandService.updateItemBrand(itemBrand);
	}

	@PostMapping("/deleteItemsBrand.json")
	public ShopTxResult deleteItemsBrand(@RequestParam("id") Integer id) {
		return itemBrandService.deleteItemBrand(id);
	}

	//------------------------- 商品类别----------------------------------------

	@PostMapping("/listItemsCategory.json")
	public Object listItemsCategory(@RequestBody Map<String, Object> params) {
		return itemCategoryService.listItemCategory(params);
	}

	@PostMapping("/getSubItemsCategory.json")
	public Object getSubItemsCategory(@RequestParam Integer parentId) {
		return itemCategoryService.getSubItemsCategory(parentId);
	}

	@PostMapping("/addItemsCategory.json")
	public Object addItemCategory(@RequestBody @Valid ItemCategoryDto itemCategory) throws Exception {
		return itemCategoryService.addItemsCategory(itemCategory);
	}

	@PostMapping("/updateItemsCategory.json")
	public Object updateItemsCategory(@RequestBody @Valid ItemCategoryDto itemCategory) throws Exception {
		return itemCategoryService.updateItemsCategory(itemCategory);
	}

	@PostMapping("/deleteItemsCategory.json")
	public Object deleteItemsCategory(@RequestParam Integer categoryId) {
		return itemCategoryService.deleteItemsCategory(categoryId);
	}

}
