package com.rocket.myshop.controller.mall;

import com.rocket.myshop.domain.MallUser;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.dto.mall.LoginRequest;
import com.rocket.myshop.dto.mall.LoginResult;
import com.rocket.myshop.dto.mall.RegisterRequest;
import com.rocket.myshop.service.MallAuthService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.web.annotation.GetMapping;
import cn.jiebaba.summer.web.annotation.PostMapping;
import cn.jiebaba.summer.web.annotation.RequestBody;
import cn.jiebaba.summer.web.annotation.RequestHeader;
import cn.jiebaba.summer.web.annotation.RequestMapping;
import cn.jiebaba.summer.web.annotation.RestController;
import cn.jiebaba.summer.web.validation.Valid;

/**
 * 商城会员认证接口：注册/登录/登出/当前用户。
 * 登录态通过请求头 X-Mall-Token 携带，token 由 MallAuthService 内存管理。
 */
@RestController
@RequestMapping("/mall/auth")
public class MallAuthController {

	@Autowired
	MallAuthService mallAuthService;

	@PostMapping("/register")
	public LoginResult register(@RequestBody @Valid RegisterRequest request) {
		return mallAuthService.register(request);
	}

	@PostMapping("/login")
	public LoginResult login(@RequestBody @Valid LoginRequest request) {
		return mallAuthService.login(request);
	}

	@PostMapping("/logout")
	public ShopTxResult logout(@RequestHeader(value = "X-Mall-Token", required = false) String token) {
		mallAuthService.logout(token);
		ShopTxResult result = new ShopTxResult("已退出登录", true);
		result.setVersion("1.0.0");
		return result;
	}

	@GetMapping("/me")
	public LoginResult me(@RequestHeader(value = "X-Mall-Token", required = false) String token) {
		MallUser user = mallAuthService.currentUser(token);
		LoginResult result = new LoginResult();
		result.setUserId(user.getId());
		result.setUsername(user.getUsername());
		result.setNickname(user.getNickname());
		return result;
	}
}
