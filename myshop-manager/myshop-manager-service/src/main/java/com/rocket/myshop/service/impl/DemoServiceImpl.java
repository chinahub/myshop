package com.rocket.myshop.service.impl;

import com.rocket.myshop.domain.Demo;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.mapper.DemoMapper;
import com.rocket.myshop.service.DemoService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.core.annotation.Service;
import cn.jiebaba.summer.data.conditions.QueryWrapper;
import cn.jiebaba.summer.data.transaction.Transactional;

@Service("DemoService")
@Transactional(readOnly = true)
public class DemoServiceImpl implements DemoService {

	@Autowired
	DemoMapper demoMapper;

	@Override
	public ShopQueryResult listDemo() {
		long count = demoMapper.selectCount(QueryWrapper.of());
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal((int) count);
		if (count > 0) {
			result.setList(demoMapper.selectList());
		}
		return result;
	}
}
