package com.rocket.myshop.service;

import java.util.List;
import java.util.Map;

import com.rocket.myshop.domain.SysDict;
import com.rocket.myshop.domain.SysDictData;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;

/**
 * 系统字典服务：前端下拉/状态枚举统一由 sys_dict/sys_dict_data 驱动。
 */
public interface SysDictService {

	/** 按字典类型查启用数据（sort 升序），供前端下拉/Tag 渲染消费。 */
	List<SysDictData> listEnabledDictData(String dictType);

	ShopQueryResult listDicts(Map<String, Object> params);

	ShopTxResult addDict(SysDict dict);

	/** 更新字典类型；dictType 编码变更时同步级联更新 sys_dict_data.dict_type_。 */
	ShopTxResult updateDict(SysDict dict);

	/** 删除字典类型，并级联删除该类型全部字典数据。 */
	ShopTxResult deleteDict(Integer id);

	ShopQueryResult listDictData(Map<String, Object> params);

	ShopTxResult addDictData(SysDictData data);

	ShopTxResult updateDictData(SysDictData data);

	ShopTxResult deleteDictData(Integer id);
}
