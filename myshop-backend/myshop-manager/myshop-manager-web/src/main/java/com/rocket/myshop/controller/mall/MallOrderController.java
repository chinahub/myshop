package com.rocket.myshop.controller.mall;

import java.util.HashMap;
import java.util.Map;

import com.rocket.myshop.domain.MallUser;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.dto.mall.OrderCreateRequest;
import com.rocket.myshop.dto.mall.OrderView;
import com.rocket.myshop.service.MallAuthService;
import com.rocket.myshop.service.MallOrderService;

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
 * 商城订单接口（需登录）：从购物车提交订单、查询、模拟支付、取消。
 */
@RestController
@RequestMapping("/mall/orders")
public class MallOrderController {

	@Autowired
	MallAuthService mallAuthService;

	@Autowired
	MallOrderService mallOrderService;

	@PostMapping("/create")
	public Map<String, Object> create(@RequestBody @Valid OrderCreateRequest request,
			@RequestHeader(value = "X-Mall-Token", required = false) String token) {
		MallUser user = mallAuthService.currentUser(token);
		return mallOrderService.createOrder(user.getId(), request);
	}

	@GetMapping("/list.json")
	public ShopQueryResult list(@RequestHeader(value = "X-Mall-Token", required = false) String token,
			@RequestParam(required = false) Integer start,
			@RequestParam(required = false) Integer length) {
		MallUser user = mallAuthService.currentUser(token);
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("length", length);
		return mallOrderService.listMyOrders(user.getId(), params);
	}

	@GetMapping("/detail.json")
	public OrderView detail(@RequestParam("id") Integer id,
			@RequestHeader(value = "X-Mall-Token", required = false) String token) {
		MallUser user = mallAuthService.currentUser(token);
		return mallOrderService.getMyOrder(user.getId(), id);
	}

	@PostMapping("/pay")
	public ShopTxResult pay(@RequestParam("id") Integer id,
			@RequestHeader(value = "X-Mall-Token", required = false) String token) {
		MallUser user = mallAuthService.currentUser(token);
		return mallOrderService.pay(user.getId(), id);
	}

	@PostMapping("/cancel")
	public ShopTxResult cancel(@RequestParam("id") Integer id,
			@RequestHeader(value = "X-Mall-Token", required = false) String token) {
		MallUser user = mallAuthService.currentUser(token);
		return mallOrderService.cancel(user.getId(), id);
	}
}
