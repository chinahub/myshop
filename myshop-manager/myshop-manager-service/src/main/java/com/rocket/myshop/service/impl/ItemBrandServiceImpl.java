package com.rocket.myshop.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rocket.myshop.domain.ItemBrand;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.mapper.ItemBrandMapper;
import com.rocket.myshop.service.ItemBrandService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.core.annotation.Service;
import cn.jiebaba.summer.data.conditions.LambdaQueryWrapper;
import cn.jiebaba.summer.data.page.IPage;
import cn.jiebaba.summer.data.page.Page;
import cn.jiebaba.summer.data.transaction.Transactional;

@Service("ItemBrandService")
@Transactional(readOnly = true)
public class ItemBrandServiceImpl implements ItemBrandService {

	@Autowired
	ItemBrandMapper itemBrandMapper;

	@Override
	public ShopQueryResult listItemBrands(Map<String, Object> params) {
		LambdaQueryWrapper<ItemBrand> wrapper = new LambdaQueryWrapper<>();
		Object status = params.get("status");
		if (status != null) {
			wrapper.eq(ItemBrand::getStatus, status);
		}
		Object name = params.get("name");
		if (name instanceof String s && !s.isEmpty()) {
			wrapper.eq(ItemBrand::getName, s);
		}
		Object createTimeBegin = params.get("createTimeBegin");
		if (createTimeBegin instanceof String s && !s.isEmpty()) {
			wrapper.gt(ItemBrand::getCreateTime, s);
		}
		long count = itemBrandMapper.selectCount(wrapper);
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal((int) count);
		if (count > 0) {
			PageParams.normalize(params);
			int start = (Integer) params.get("start");
			int length = (Integer) params.get("length");
			IPage<ItemBrand> page = itemBrandMapper.selectPage(
					new Page<>(length > 0 ? start / length + 1 : 1, length), wrapper);
			result.setList(page.records());
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult addItemBrand(ItemBrand brand) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		if (brand.getName() == null || brand.getName().isBlank()) {
			result.setReasons(new HashMap<>(Map.of("name", "品牌名称不能为空！")));
			return result;
		}
		if (checkNameIsExist(brand.getName(), null)) {
			result.setReasons(new HashMap<>(Map.of("name", "品牌名称已存在！")));
			return result;
		}
		Date now = new Date();
		if (brand.getStatus() == null) {
			brand.setStatus(Boolean.TRUE);
		}
		brand.setCreateTime(now);
		brand.setUpdateTime(now);
		int insertCount = itemBrandMapper.insert(brand);
		result.setMessage("成功添加" + insertCount + "记录");
		result.setSuccess(true);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult updateItemBrand(ItemBrand brand) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		if (brand.getId() == null) {
			result.setReasons(new HashMap<>(Map.of("id", "品牌ID不能为空！")));
			return result;
		}
		if (brand.getName() == null || brand.getName().isBlank()) {
			result.setReasons(new HashMap<>(Map.of("name", "品牌名称不能为空！")));
			return result;
		}
		if (checkNameIsExist(brand.getName(), brand.getId())) {
			result.setReasons(new HashMap<>(Map.of("name", "品牌名称已存在！")));
			return result;
		}
		ItemBrand exist = itemBrandMapper.selectById(brand.getId());
		if (exist == null) {
			result.setMessage("品牌不存在！");
			return result;
		}
		brand.setUpdateTime(new Date());
		int updateCount = itemBrandMapper.updateById(brand);
		result.setMessage("成功更新" + updateCount + "记录");
		result.setSuccess(true);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult deleteItemBrand(Integer id) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		ItemBrand brand = itemBrandMapper.selectById(id);
		if (brand == null) {
			result.setMessage("品牌不存在！");
			return result;
		}
		brand.setStatus(Boolean.FALSE);
		brand.setUpdateTime(new Date());
		int updateCount = itemBrandMapper.updateById(brand);
		result.setMessage("成功删除" + updateCount + "记录");
		result.setSuccess(true);
		return result;
	}

	private boolean checkNameIsExist(String name, Integer excludeId) {
		LambdaQueryWrapper<ItemBrand> wrapper = new LambdaQueryWrapper<ItemBrand>()
				.eq(ItemBrand::getName, name);
		if (excludeId != null) {
			wrapper.ne(ItemBrand::getId, excludeId);
		}
		return itemBrandMapper.selectCount(wrapper) > 0;
	}
}
