package com.rocket.myshop;

import cn.jiebaba.summer.boot.SummerApplication;
import cn.jiebaba.summer.boot.annotation.SummerBootApplication;

/**
 * myshop 后台管理应用入口（单体、纯 REST）。
 * 组件扫描根：com.rocket.myshop，配置见 resources/application.yml。
 */
@SummerBootApplication
public class ManagerApplication {

	public static void main(String[] args) {
		SummerApplication.run(ManagerApplication.class, args);
	}
}
