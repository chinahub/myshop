package com.rocket.myshop.controller;

import cn.jiebaba.summer.web.validation.NotNull;
import cn.jiebaba.summer.web.validation.Size;

/**
 * 用户演示表单 bean（Summer 校验注解）。
 */
public class User {

	@NotNull(message = "name 不能为空")
	private String name;

	@Size(max = 6, message = "用户名最长 6 个字符")
	private String username;

	public User() {
	}

	public User(String name, String username) {
		this.username = username;
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
