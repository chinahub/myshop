package com.rocket.myshop.controller;

import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.service.DemoService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.web.annotation.GetMapping;
import cn.jiebaba.summer.web.annotation.RestController;

@RestController
public class DemoController {

	@Autowired
	DemoService demoService;

	@GetMapping("/listDemo")
	public ShopQueryResult viewDemo() {
		return demoService.listDemo();
	}

	@GetMapping("/showData")
	public ShopQueryResult getDemo() {
		return demoService.listDemo();
	}

	@GetMapping("/error")
	public Object error() {
		// 演示全局异常处理：messageCode 以 Error. 开头时由消息包解析文案
		throw new RuntimeException("Error.1");
	}

}
