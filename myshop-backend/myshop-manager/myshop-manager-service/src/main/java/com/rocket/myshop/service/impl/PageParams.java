package com.rocket.myshop.service.impl;

import java.util.Map;

/**
* @FullClassName com.rocket.myshop.service.impl.PageParams
* @Description: 列表查询的分页参数处理
* @version V1.0.0
 */
final class PageParams {

	private PageParams() {
	}

	/**
	 * start 缺省为0；length 缺省或为负数时表示不分页。
	 */
	static void normalize(Map<String, Object> params) {
		Object start = params.get("start");
		params.put("start", start instanceof Number ? ((Number) start).intValue() : 0);
		Object length = params.get("length");
		int size = length instanceof Number ? ((Number) length).intValue() : -1;
		params.put("length", size < 0 ? Integer.MAX_VALUE : size);
	}
}
