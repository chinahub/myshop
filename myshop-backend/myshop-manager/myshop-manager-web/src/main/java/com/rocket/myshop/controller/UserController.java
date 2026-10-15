package com.rocket.myshop.controller;

import com.rocket.myshop.controller.User;

import cn.jiebaba.summer.web.annotation.GetMapping;
import cn.jiebaba.summer.web.annotation.PostMapping;
import cn.jiebaba.summer.web.annotation.PathVariable;
import cn.jiebaba.summer.web.annotation.RequestMapping;
import cn.jiebaba.summer.web.annotation.ResponseBody;
import cn.jiebaba.summer.web.annotation.RestController;
import cn.jiebaba.summer.web.server.ResponseStatusException;
import cn.jiebaba.summer.web.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	@GetMapping("/{username}")
	public User getUser(@PathVariable String username) {
		return findUser(username);
	}

	/**
	 * 模拟用户查询：仅支持 jsmith / djones。
	 */
	private User findUser(String username) {
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Username is required.");
		}
		if ("jsmith".equals(username)) {
			return new User("Jane Smith", username);
		}
		if ("djones".equals(username)) {
			return new User("Don Jones", username);
		}
		throw new ResponseStatusException(404, "Unable to find user with username '" + username + "'");
	}

	@PostMapping("/addUser")
	public User addUser(@Valid User user) {
		return user;
	}

}
