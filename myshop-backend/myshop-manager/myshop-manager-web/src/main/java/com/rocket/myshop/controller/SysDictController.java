package com.rocket.myshop.controller;

import java.util.Map;

import com.rocket.myshop.domain.SysDict;
import com.rocket.myshop.domain.SysDictData;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.service.SysDictService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.web.annotation.GetMapping;
import cn.jiebaba.summer.web.annotation.PostMapping;
import cn.jiebaba.summer.web.annotation.RequestBody;
import cn.jiebaba.summer.web.annotation.RequestMapping;
import cn.jiebaba.summer.web.annotation.RequestParam;
import cn.jiebaba.summer.web.annotation.RestController;

/**
 * @Description: 系统字典 REST 接口（下拉框/状态枚举统一由字典驱动）
 */
@RestController
@RequestMapping("/sysDict")
public class SysDictController {

	@Autowired
	SysDictService sysDictService;

	//------------------------- 前端消费----------------------------------------

	/**
	 * 按字典类型查启用数据（不分页，sort 升序）。
	 * 管理端与商城前端共用；管理端字典数据列表（含停用）走 /data/list.json。
	 */
	@GetMapping("/data/enabled.json")
	public Object listEnabledData(@RequestParam("dictType") String dictType) {
		return sysDictService.listEnabledDictData(dictType);
	}

	//------------------------- 字典类型----------------------------------------

	@PostMapping("/list.json")
	public Object listDicts(@RequestBody Map<String, Object> params) {
		ShopQueryResult result = sysDictService.listDicts(params);
		result.setSuccess(true);
		result.setVersion("1.0.0");
		return result;
	}

	@PostMapping("/add.json")
	public ShopTxResult addDict(@RequestBody SysDict dict) {
		return sysDictService.addDict(dict);
	}

	@PostMapping("/update.json")
	public ShopTxResult updateDict(@RequestBody SysDict dict) {
		return sysDictService.updateDict(dict);
	}

	@PostMapping("/delete.json")
	public ShopTxResult deleteDict(@RequestParam("id") Integer id) {
		return sysDictService.deleteDict(id);
	}

	//------------------------- 字典数据----------------------------------------

	@PostMapping("/data/list.json")
	public Object listDictData(@RequestBody Map<String, Object> params) {
		ShopQueryResult result = sysDictService.listDictData(params);
		result.setSuccess(true);
		result.setVersion("1.0.0");
		return result;
	}

	@PostMapping("/data/add.json")
	public ShopTxResult addDictData(@RequestBody SysDictData data) {
		return sysDictService.addDictData(data);
	}

	@PostMapping("/data/update.json")
	public ShopTxResult updateDictData(@RequestBody SysDictData data) {
		return sysDictService.updateDictData(data);
	}

	@PostMapping("/data/delete.json")
	public ShopTxResult deleteDictData(@RequestParam("id") Integer id) {
		return sysDictService.deleteDictData(id);
	}
}
