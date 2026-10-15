package com.rocket.myshop.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rocket.myshop.domain.ItemCategory;
import com.rocket.myshop.dto.ItemCategoryDto;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.mapper.ItemCategoryMapper;
import com.rocket.myshop.service.ItemCategoryService;

import org.apache.commons.beanutils.BeanUtils;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.core.annotation.Service;
import cn.jiebaba.summer.data.conditions.LambdaQueryWrapper;
import cn.jiebaba.summer.data.page.IPage;
import cn.jiebaba.summer.data.page.Page;
import cn.jiebaba.summer.data.transaction.Transactional;

@Service("ItemCategoryService")
@Transactional(rollbackFor = Exception.class)
public class ItemCategoryServiceImpl implements ItemCategoryService {

	@Autowired
	ItemCategoryMapper itemCategoryMapper;

	@Override
	public ShopQueryResult listItemCategory(Map<String, Object> params) {
		LambdaQueryWrapper<ItemCategory> wrapper = new LambdaQueryWrapper<ItemCategory>()
				.eq(ItemCategory::getStatus, Boolean.TRUE);
		Object name = params.get("name");
		if (name instanceof String s && !s.isEmpty()) {
			wrapper.eq(ItemCategory::getName, s);
		}
		Object level = params.get("level");
		if (level instanceof Number n) {
			wrapper.eq(ItemCategory::getLevel, n.byteValue());
		}
		Object createTimeBegin = params.get("createTimeBegin");
		if (createTimeBegin instanceof String s && !s.isEmpty()) {
			wrapper.gt(ItemCategory::getCreateTime, s);
		}
		long count = itemCategoryMapper.selectCount(wrapper);
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal((int) count);
		if (count > 0) {
			PageParams.normalize(params);
			int start = (Integer) params.get("start");
			int length = (Integer) params.get("length");
			IPage<ItemCategory> page = itemCategoryMapper.selectPage(
					new Page<>(length > 0 ? start / length + 1 : 1, length), wrapper);
			result.setList(page.records());
		}
		result.setSuccess(true);
		result.setVersion("1.0.0");
		return result;
	}

	@Override
	public ShopQueryResult getSubItemsCategory(Integer parentId) {
		ShopQueryResult result = new ShopQueryResult();
		List<ItemCategory> itemCategorys = itemCategoryMapper.selectList(
				new LambdaQueryWrapper<ItemCategory>()
						.eq(ItemCategory::getParentId, parentId)
						.eq(ItemCategory::getStatus, Boolean.TRUE));
		result.setList(itemCategorys);
		result.setSuccess(true);
		result.setVersion("1.0.0");
		return result;
	}

	@Override
	public ShopTxResult addItemsCategory(ItemCategoryDto itemCategoryDto) throws Exception {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		boolean existFlag = checkCategoryNameIsExist(itemCategoryDto.getName(), null);
		if (existFlag) {
			result.setSuccess(false);
			Map<String, Object> reasons = new HashMap<>();
			reasons.put("name", "商品类别名称已存在！");
			result.setReasons(reasons);
		} else {
			ItemCategory itemCategory = new ItemCategory();
			BeanUtils.copyProperties(itemCategory, itemCategoryDto);
			Integer parentId = itemCategory.getParentId();
			byte level = 1;
			ItemCategory itemCategoryParent = parentId == null ? null : itemCategoryMapper.selectById(parentId);
			if (parentId != null && itemCategoryParent == null) {
				result.setSuccess(false);
				Map<String, Object> reasons = new HashMap<>();
				reasons.put("parentId", "父商品类别不存在！");
				result.setReasons(reasons);
				return result;
			}
			if (parentId != null) {
				// 更新父类别中的子类别数目，子类别更新时间
				short subCategoryNum = itemCategoryParent.getSubCategoryNum().shortValue();
				subCategoryNum += level;
				itemCategoryParent.setSubCategoryNum(subCategoryNum);
				Byte levelParent = itemCategoryParent.getLevel();
				level += levelParent.byteValue();
				itemCategoryParent.setUpdateTime(new Date());
				itemCategoryMapper.updateById(itemCategoryParent);
			}
			itemCategory.setLevel(level);
			int insertCount = itemCategoryMapper.insert(itemCategory);
			result.setMessage("成功添加" + insertCount + "记录");
			result.setSuccess(true);
		}
		return result;
	}

	private boolean checkCategoryNameIsExist(String name, Integer categoryId) {
		LambdaQueryWrapper<ItemCategory> wrapper = new LambdaQueryWrapper<ItemCategory>()
				.eq(ItemCategory::getName, name);
		if (categoryId != null) {
			wrapper.ne(ItemCategory::getId, categoryId);
		}
		return itemCategoryMapper.selectCount(wrapper) > 0;
	}

	@Override
	public ShopTxResult deleteItemsCategory(Integer categoryId) {
		ItemCategory itemCategory = itemCategoryMapper.selectById(categoryId);
		if (itemCategory == null) {
			ShopTxResult result = new ShopTxResult();
			result.setSuccess(false);
			result.setMessage("商品类别不存在！");
			result.setVersion("1.0.0");
			return result;
		}
		Integer parentId = itemCategory.getParentId();
		if (parentId != null) {
			ItemCategory itemCategoryParent = itemCategoryMapper.selectById(parentId);
			if (itemCategoryParent != null) {
				itemCategoryParent.setUpdateTime(new Date());
				Short subCategoryNum = itemCategoryParent.getSubCategoryNum();
				subCategoryNum--;
				itemCategoryParent.setSubCategoryNum(subCategoryNum);
				itemCategoryMapper.updateById(itemCategoryParent);
			}
		}
		itemCategory.setStatus(false);
		itemCategory.setUpdateTime(new Date());
		int updateCount = itemCategoryMapper.updateById(itemCategory);
		ShopTxResult result = new ShopTxResult();
		result.setMessage("成功删除" + updateCount + "记录");
		result.setSuccess(true);
		result.setVersion("1.0.0");
		return result;
	}

	@Override
	public ShopTxResult updateItemsCategory(ItemCategoryDto itemCategoryDto) throws Exception {
		ShopTxResult result = new ShopTxResult();
		boolean existFlag = checkCategoryNameIsExist(itemCategoryDto.getName(), itemCategoryDto.getId());
		result.setVersion("1.0.0");
		if (existFlag) {
			result.setSuccess(false);
			Map<String, Object> reasons = new HashMap<>();
			reasons.put("name", "商品类别名称已存在！");
			result.setReasons(reasons);
		} else {
			ItemCategory itemCategory = new ItemCategory();
			BeanUtils.copyProperties(itemCategory, itemCategoryDto);
			itemCategory.setUpdateTime(new Date());
			int updateCount = itemCategoryMapper.updateById(itemCategory);
			result.setMessage("成功更新" + updateCount + "记录");
			result.setSuccess(true);
		}
		return result;
	}
}
