package com.rocket.myshop.controller.mall;

import java.util.List;

import com.rocket.myshop.domain.MallUser;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.dto.mall.CartAddRequest;
import com.rocket.myshop.dto.mall.CartItemView;
import com.rocket.myshop.dto.mall.CartUpdateRequest;
import com.rocket.myshop.service.MallAuthService;
import com.rocket.myshop.service.MallCartService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.web.annotation.GetMapping;
import cn.jiebaba.summer.web.annotation.PostMapping;
import cn.jiebaba.summer.web.annotation.RequestBody;
import cn.jiebaba.summer.web.annotation.RequestHeader;
import cn.jiebaba.summer.web.annotation.RequestMapping;
import cn.jiebaba.summer.web.annotation.RequestParam;
import cn.jiebaba.summer.web.annotation.RestController;
import cn.jiebaba.summer.web.validation.Valid;

/**
 * 商城购物车接口（需登录，token 从 X-Mall-Token 头解析）。
 */
@RestController
@RequestMapping("/mall/cart")
public class MallCartController {

	@Autowired
	MallAuthService mallAuthService;

	@Autowired
	MallCartService mallCartService;

	@GetMapping("/list.json")
	public ShopQueryResult list(@RequestHeader(value = "X-Mall-Token", required = false) String token) {
		MallUser user = mallAuthService.currentUser(token);
		List<CartItemView> views = mallCartService.listCart(user.getId());
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal(views.size());
		result.setList(views);
		result.setSuccess(true);
		result.setVersion("1.0.0");
		return result;
	}

	@PostMapping("/add")
	public ShopTxResult add(@RequestBody @Valid CartAddRequest request,
			@RequestHeader(value = "X-Mall-Token", required = false) String token) {
		MallUser user = mallAuthService.currentUser(token);
		return mallCartService.add(user.getId(), request);
	}

	@PostMapping("/updateNum")
	public ShopTxResult updateNum(@RequestBody @Valid CartUpdateRequest request,
			@RequestHeader(value = "X-Mall-Token", required = false) String token) {
		MallUser user = mallAuthService.currentUser(token);
		return mallCartService.updateNum(user.getId(), request.getCartId(), request.getNum());
	}

	@PostMapping("/delete")
	public ShopTxResult delete(@RequestParam("cartId") Integer cartId,
			@RequestHeader(value = "X-Mall-Token", required = false) String token) {
		MallUser user = mallAuthService.currentUser(token);
		return mallCartService.remove(user.getId(), cartId);
	}
}
