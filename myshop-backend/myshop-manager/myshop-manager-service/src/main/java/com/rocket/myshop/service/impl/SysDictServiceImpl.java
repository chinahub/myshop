package com.rocket.myshop.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rocket.myshop.domain.SysDict;
import com.rocket.myshop.domain.SysDictData;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.mapper.SysDictDataMapper;
import com.rocket.myshop.mapper.SysDictMapper;
import com.rocket.myshop.service.SysDictService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.core.annotation.Service;
import cn.jiebaba.summer.data.conditions.LambdaQueryWrapper;
import cn.jiebaba.summer.data.page.IPage;
import cn.jiebaba.summer.data.page.Page;
import cn.jiebaba.summer.data.transaction.Transactional;

@Service("SysDictService")
@Transactional(readOnly = true)
public class SysDictServiceImpl implements SysDictService {

	@Autowired
	SysDictMapper sysDictMapper;

	@Autowired
	SysDictDataMapper sysDictDataMapper;

	@Override
	public List<SysDictData> listEnabledDictData(String dictType) {
		if (dictType == null || dictType.isBlank()) {
			return List.of();
		}
		LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<SysDictData>()
				.eq(SysDictData::getDictType, dictType)
				.eq(SysDictData::getStatus, 1)
				.orderByAsc(SysDictData::getSort);
		return sysDictDataMapper.selectList(wrapper);
	}

	@Override
	public ShopQueryResult listDicts(Map<String, Object> params) {
		LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
		Object dictType = params.get("dictType");
		if (dictType instanceof String s && !s.isEmpty()) {
			wrapper.eq(SysDict::getDictType, s);
		}
		Object dictName = params.get("dictName");
		if (dictName instanceof String s && !s.isEmpty()) {
			wrapper.eq(SysDict::getDictName, s);
		}
		Object status = params.get("status");
		if (status != null) {
			wrapper.eq(SysDict::getStatus, status);
		}
		long count = sysDictMapper.selectCount(wrapper);
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal((int) count);
		if (count > 0) {
			PageParams.normalize(params);
			int start = (Integer) params.get("start");
			int length = (Integer) params.get("length");
			IPage<SysDict> page = sysDictMapper.selectPage(
					new Page<>(length > 0 ? start / length + 1 : 1, length), wrapper);
			result.setList(page.records());
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult addDict(SysDict dict) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		if (dict.getDictType() == null || dict.getDictType().isBlank()) {
			result.setReasons(new HashMap<>(Map.of("dictType", "字典类型编码不能为空！")));
			return result;
		}
		if (dict.getDictName() == null || dict.getDictName().isBlank()) {
			result.setReasons(new HashMap<>(Map.of("dictName", "字典名称不能为空！")));
			return result;
		}
		if (checkTypeIsExist(dict.getDictType(), null)) {
			result.setReasons(new HashMap<>(Map.of("dictType", "字典类型编码已存在！")));
			return result;
		}
		Date now = new Date();
		if (dict.getStatus() == null) {
			dict.setStatus(1);
		}
		dict.setCreateTime(now);
		dict.setUpdateTime(now);
		int insertCount = sysDictMapper.insert(dict);
		result.setMessage("成功添加" + insertCount + "记录");
		result.setSuccess(true);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult updateDict(SysDict dict) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		if (dict.getId() == null) {
			result.setReasons(new HashMap<>(Map.of("id", "字典ID不能为空！")));
			return result;
		}
		SysDict exist = sysDictMapper.selectById(dict.getId());
		if (exist == null) {
			result.setMessage("字典类型不存在！");
			return result;
		}
		// 局部更新：未传字段继承现值（与商品/品牌更新行为一致）
		if (dict.getDictType() == null || dict.getDictType().isBlank()) {
			dict.setDictType(exist.getDictType());
		}
		if (dict.getDictName() == null || dict.getDictName().isBlank()) {
			dict.setDictName(exist.getDictName());
		}
		if (checkTypeIsExist(dict.getDictType(), dict.getId())) {
			result.setReasons(new HashMap<>(Map.of("dictType", "字典类型编码已存在！")));
			return result;
		}
		// 类型编码变更时级联更新字典数据，避免数据行悬空
		if (!exist.getDictType().equals(dict.getDictType())) {
			List<SysDictData> olds = sysDictDataMapper.selectList(
					new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, exist.getDictType()));
			for (SysDictData data : olds) {
				data.setDictType(dict.getDictType());
				data.setUpdateTime(new Date());
				sysDictDataMapper.updateById(data);
			}
		}
		dict.setUpdateTime(new Date());
		int updateCount = sysDictMapper.updateById(dict);
		result.setMessage("成功更新" + updateCount + "记录");
		result.setSuccess(true);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult deleteDict(Integer id) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		SysDict dict = sysDictMapper.selectById(id);
		if (dict == null) {
			result.setMessage("字典类型不存在！");
			return result;
		}
		// 级联删除该类型全部字典数据
		List<SysDictData> datas = sysDictDataMapper.selectList(
				new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, dict.getDictType()));
		int deleted = 0;
		for (SysDictData data : datas) {
			deleted += sysDictDataMapper.deleteById(data.getId());
		}
		int deleteCount = sysDictMapper.deleteById(id);
		result.setMessage("成功删除" + deleteCount + "记录（含字典数据" + deleted + "条）");
		result.setSuccess(true);
		return result;
	}

	@Override
	public ShopQueryResult listDictData(Map<String, Object> params) {
		LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
		Object dictType = params.get("dictType");
		if (dictType instanceof String s && !s.isEmpty()) {
			wrapper.eq(SysDictData::getDictType, s);
		}
		Object status = params.get("status");
		if (status != null) {
			wrapper.eq(SysDictData::getStatus, status);
		}
		wrapper.orderByAsc(SysDictData::getSort);
		long count = sysDictDataMapper.selectCount(wrapper);
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal((int) count);
		if (count > 0) {
			PageParams.normalize(params);
			int start = (Integer) params.get("start");
			int length = (Integer) params.get("length");
			IPage<SysDictData> page = sysDictDataMapper.selectPage(
					new Page<>(length > 0 ? start / length + 1 : 1, length), wrapper);
			result.setList(page.records());
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult addDictData(SysDictData data) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		ShopTxResult check = checkDictData(data);
		if (check != null) {
			return check;
		}
		if (checkValueIsExist(data.getDictType(), data.getValue(), null)) {
			result.setReasons(new HashMap<>(Map.of("value", "该字典下已存在相同值！")));
			return result;
		}
		Date now = new Date();
		if (data.getSort() == null) {
			data.setSort(0);
		}
		if (data.getStatus() == null) {
			data.setStatus(1);
		}
		data.setCreateTime(now);
		data.setUpdateTime(now);
		int insertCount = sysDictDataMapper.insert(data);
		result.setMessage("成功添加" + insertCount + "记录");
		result.setSuccess(true);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult updateDictData(SysDictData data) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		if (data.getId() == null) {
			result.setReasons(new HashMap<>(Map.of("id", "字典数据ID不能为空！")));
			return result;
		}
		SysDictData exist = sysDictDataMapper.selectById(data.getId());
		if (exist == null) {
			result.setMessage("字典数据不存在！");
			return result;
		}
		// 局部更新：未传字段继承现值（与商品/品牌更新行为一致）
		if (data.getDictType() == null || data.getDictType().isBlank()) {
			data.setDictType(exist.getDictType());
		}
		if (data.getValue() == null || data.getValue().isBlank()) {
			data.setValue(exist.getValue());
		}
		if (data.getLabel() == null || data.getLabel().isBlank()) {
			data.setLabel(exist.getLabel());
		}
		ShopTxResult check = checkDictData(data);
		if (check != null) {
			return check;
		}
		if (checkValueIsExist(data.getDictType(), data.getValue(), data.getId())) {
			result.setReasons(new HashMap<>(Map.of("value", "该字典下已存在相同值！")));
			return result;
		}
		data.setUpdateTime(new Date());
		int updateCount = sysDictDataMapper.updateById(data);
		result.setMessage("成功更新" + updateCount + "记录");
		result.setSuccess(true);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult deleteDictData(Integer id) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		SysDictData data = sysDictDataMapper.selectById(id);
		if (data == null) {
			result.setMessage("字典数据不存在！");
			return result;
		}
		int deleteCount = sysDictDataMapper.deleteById(id);
		result.setMessage("成功删除" + deleteCount + "记录");
		result.setSuccess(true);
		return result;
	}

	/** 字典数据公共校验：类型/值/标签必填，所属字典类型必须存在。 */
	private ShopTxResult checkDictData(SysDictData data) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		if (data.getDictType() == null || data.getDictType().isBlank()) {
			result.setReasons(new HashMap<>(Map.of("dictType", "所属字典类型不能为空！")));
			return result;
		}
		if (data.getValue() == null || data.getValue().isBlank()) {
			result.setReasons(new HashMap<>(Map.of("value", "字典值不能为空！")));
			return result;
		}
		if (data.getLabel() == null || data.getLabel().isBlank()) {
			result.setReasons(new HashMap<>(Map.of("label", "字典标签不能为空！")));
			return result;
		}
		LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<SysDict>()
				.eq(SysDict::getDictType, data.getDictType());
		if (sysDictMapper.selectOne(wrapper) == null) {
			result.setReasons(new HashMap<>(Map.of("dictType", "所属字典类型不存在！")));
			return result;
		}
		return null;
	}

	private boolean checkTypeIsExist(String dictType, Integer excludeId) {
		LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<SysDict>()
				.eq(SysDict::getDictType, dictType);
		if (excludeId != null) {
			wrapper.ne(SysDict::getId, excludeId);
		}
		return sysDictMapper.selectCount(wrapper) > 0;
	}

	private boolean checkValueIsExist(String dictType, String value, Integer excludeId) {
		LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<SysDictData>()
				.eq(SysDictData::getDictType, dictType)
				.eq(SysDictData::getValue, value);
		if (excludeId != null) {
			wrapper.ne(SysDictData::getId, excludeId);
		}
		return sysDictDataMapper.selectCount(wrapper) > 0;
	}
}
