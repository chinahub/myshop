package com.rocket.myshop.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rocket.myshop.domain.Item;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.mapper.ItemMapper;
import com.rocket.myshop.service.ItemService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.core.annotation.Service;
import cn.jiebaba.summer.data.conditions.QueryWrapper;
import cn.jiebaba.summer.data.page.IPage;
import cn.jiebaba.summer.data.page.Page;
import cn.jiebaba.summer.data.transaction.Transactional;

@Service("ItemService")
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

	@Autowired
	ItemMapper itemMapper;

	@Override
	public ShopQueryResult listItem(Map<String, Object> params) {
		long count = itemMapper.selectCount(QueryWrapper.of());
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal((int) count);
		if (count > 0) {
			PageParams.normalize(params);
			int start = (Integer) params.get("start");
			int length = (Integer) params.get("length");
			IPage<Item> page = itemMapper.selectPage(new Page<>(length > 0 ? start / length + 1 : 1, length),
					QueryWrapper.of());
			result.setList(page.records());
		}
		return result;
	}

	@Override
	public Item getItem(Integer id) {
		return itemMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult addItem(Item item) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		if (item.getTitle() == null || item.getTitle().isBlank()) {
			result.setReasons(new HashMap<>(Map.of("title", "商品名称不能为空！")));
			return result;
		}
		Date now = new Date();
		if (item.getStatus() == null || item.getStatus().isBlank()) {
			item.setStatus("1");
		}
		item.setCreateTime(now);
		item.setUpdateTime(now);
		int insertCount = itemMapper.insert(item);
		result.setMessage("成功添加" + insertCount + "记录");
		result.setSuccess(true);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult updateItem(Item item) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		if (item.getId() == null) {
			result.setReasons(new HashMap<>(Map.of("id", "商品ID不能为空！")));
			return result;
		}
		if (item.getTitle() == null || item.getTitle().isBlank()) {
			result.setReasons(new HashMap<>(Map.of("title", "商品名称不能为空！")));
			return result;
		}
		Item exist = itemMapper.selectById(item.getId());
		if (exist == null) {
			result.setMessage("商品不存在！");
			return result;
		}
		item.setUpdateTime(new Date());
		int updateCount = itemMapper.updateById(item);
		result.setMessage("成功更新" + updateCount + "记录");
		result.setSuccess(true);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShopTxResult deleteItem(Integer id) {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		Item item = itemMapper.selectById(id);
		if (item == null) {
			result.setMessage("商品不存在！");
			return result;
		}
		// 与商品状态语义一致：1 正常，0 下架（软删除）
		item.setStatus("0");
		item.setUpdateTime(new Date());
		int updateCount = itemMapper.updateById(item);
		result.setMessage("成功删除" + updateCount + "记录");
		result.setSuccess(true);
		return result;
	}
}
