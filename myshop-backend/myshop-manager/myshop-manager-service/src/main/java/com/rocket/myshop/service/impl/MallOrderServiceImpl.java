package com.rocket.myshop.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.rocket.myshop.domain.Item;
import com.rocket.myshop.domain.MallCart;
import com.rocket.myshop.domain.MallOrder;
import com.rocket.myshop.domain.MallOrderItem;
import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.dto.mall.OrderCreateRequest;
import com.rocket.myshop.dto.mall.OrderItemView;
import com.rocket.myshop.dto.mall.OrderView;
import com.rocket.myshop.exception.MallNotFoundException;
import com.rocket.myshop.mapper.ItemMapper;
import com.rocket.myshop.mapper.MallCartMapper;
import com.rocket.myshop.mapper.MallOrderItemMapper;
import com.rocket.myshop.mapper.MallOrderMapper;
import com.rocket.myshop.service.MallOrderService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.core.annotation.Service;
import cn.jiebaba.summer.data.conditions.LambdaQueryWrapper;
import cn.jiebaba.summer.data.page.IPage;
import cn.jiebaba.summer.data.page.Page;
import cn.jiebaba.summer.data.transaction.Transactional;

@Service("MallOrderService")
@Transactional(rollbackFor = Exception.class)
public class MallOrderServiceImpl implements MallOrderService {

	private static final DateTimeFormatter ORDER_NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	@Autowired
	MallOrderMapper mallOrderMapper;

	@Autowired
	MallOrderItemMapper mallOrderItemMapper;

	@Autowired
	MallCartMapper mallCartMapper;

	@Autowired
	ItemMapper itemMapper;

	@Override
	public Map<String, Object> createOrder(Integer userId, OrderCreateRequest request) {
		List<MallCart> carts = mallCartMapper.selectList(new LambdaQueryWrapper<MallCart>()
				.eq(MallCart::getUserId, userId)
				.orderByDesc(MallCart::getId));
		if (carts.isEmpty()) {
			throw new MallNotFoundException("购物车为空，无法提交订单");
		}
		// 快照 + 扣库存：商品信息在下单时刻定格，历史订单不受商品后续改价/下架影响
		List<MallOrderItem> orderItems = new ArrayList<>();
		BigDecimal total = BigDecimal.ZERO;
		for (MallCart cart : carts) {
			Item item = itemMapper.selectById(cart.getItemId());
			if (item == null || !"1".equals(item.getStatus())) {
				throw new MallNotFoundException("商品「" + safeTitle(cart.getItemId()) + "」已下架，请处理后重新提交");
			}
			if (item.getNum() < cart.getNum()) {
				throw new MallNotFoundException("商品「" + item.getTitle() + "」库存不足（剩 " + item.getNum() + " 件），请调整数量");
			}
			BigDecimal price = item.getPrice() == null ? BigDecimal.ZERO : item.getPrice();
			BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cart.getNum()));
			MallOrderItem orderItem = new MallOrderItem();
			orderItem.setItemId(item.getId());
			orderItem.setItemTitle(item.getTitle());
			orderItem.setItemImg(item.getImgLogo());
			orderItem.setItemPrice(price);
			orderItem.setNum(cart.getNum());
			orderItem.setSubtotal(subtotal);
			orderItems.add(orderItem);
			total = total.add(subtotal);
		}
		Date now = new Date();
		MallOrder order = new MallOrder();
		order.setOrderNo(generateOrderNo());
		order.setUserId(userId);
		order.setTotalAmount(total);
		order.setStatus(MallOrder.STATUS_UNPAID);
		order.setConsigneeName(request.getConsigneeName());
		order.setConsigneePhone(request.getConsigneePhone());
		order.setConsigneeAddress(request.getConsigneeAddress());
		order.setCreateTime(now);
		order.setUpdateTime(now);
		mallOrderMapper.insert(order);
		for (MallOrderItem orderItem : orderItems) {
			orderItem.setOrderId(order.getId());
			mallOrderItemMapper.insert(orderItem);
			// 非原子 select-then-update，适合演示场景；生产应改为 UPDATE ... WHERE num_ >= ? 条件扣减
			Item item = itemMapper.selectById(orderItem.getItemId());
			item.setNum(item.getNum() - orderItem.getNum());
			item.setUpdateTime(now);
			itemMapper.updateById(item);
		}
		for (MallCart cart : carts) {
			mallCartMapper.deleteById(cart.getId());
		}
		Map<String, Object> data = new HashMap<>();
		data.put("orderId", order.getId());
		data.put("orderNo", order.getOrderNo());
		data.put("totalAmount", total);
		return data;
	}

	@Override
	@Transactional(readOnly = true)
	public ShopQueryResult listMyOrders(Integer userId, Map<String, Object> params) {
		LambdaQueryWrapper<MallOrder> wrapper = new LambdaQueryWrapper<MallOrder>()
				.eq(MallOrder::getUserId, userId)
				.orderByDesc(MallOrder::getId);
		PageParams.normalize(params);
		int start = (Integer) params.get("start");
		int length = (Integer) params.get("length");
		long count = mallOrderMapper.selectCount(new LambdaQueryWrapper<MallOrder>()
				.eq(MallOrder::getUserId, userId));
		ShopQueryResult result = new ShopQueryResult();
		result.setTotal((int) count);
		if (count > 0) {
			IPage<MallOrder> page = mallOrderMapper.selectPage(
					new Page<>(length > 0 ? start / length + 1 : 1, length), wrapper);
			List<OrderView> views = new ArrayList<>();
			for (MallOrder order : page.records()) {
				views.add(toOrderView(order));
			}
			result.setList(views);
		}
		result.setSuccess(true);
		result.setVersion("1.0.0");
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public OrderView getMyOrder(Integer userId, Integer orderId) {
		MallOrder order = ownedOrder(userId, orderId);
		return toOrderView(order);
	}

	@Override
	public ShopTxResult pay(Integer userId, Integer orderId) {
		ShopTxResult result = txResult();
		MallOrder order = ownedOrder(userId, orderId);
		if (order.getStatus() != MallOrder.STATUS_UNPAID) {
			result.setSuccess(false);
			result.setMessage("仅待付款订单可以支付");
			return result;
		}
		Date now = new Date();
		order.setStatus(MallOrder.STATUS_PAID);
		order.setPayTime(now);
		order.setUpdateTime(now);
		mallOrderMapper.updateById(order);
		result.setMessage("支付成功（模拟）");
		result.setSuccess(true);
		return result;
	}

	@Override
	public ShopTxResult cancel(Integer userId, Integer orderId) {
		ShopTxResult result = txResult();
		MallOrder order = ownedOrder(userId, orderId);
		if (order.getStatus() != MallOrder.STATUS_UNPAID) {
			result.setSuccess(false);
			result.setMessage("仅待付款订单可以取消");
			return result;
		}
		List<MallOrderItem> items = mallOrderItemMapper.selectList(new LambdaQueryWrapper<MallOrderItem>()
				.eq(MallOrderItem::getOrderId, orderId));
		Date now = new Date();
		for (MallOrderItem orderItem : items) {
			Item item = itemMapper.selectById(orderItem.getItemId());
			if (item == null) {
				continue;
			}
			item.setNum(item.getNum() + orderItem.getNum());
			item.setUpdateTime(now);
			itemMapper.updateById(item);
		}
		order.setStatus(MallOrder.STATUS_CANCELLED);
		order.setUpdateTime(now);
		mallOrderMapper.updateById(order);
		result.setMessage("订单已取消，库存已回补");
		result.setSuccess(true);
		return result;
	}

	private MallOrder ownedOrder(Integer userId, Integer orderId) {
		MallOrder order = mallOrderMapper.selectById(orderId);
		if (order == null || !order.getUserId().equals(userId)) {
			throw new MallNotFoundException("订单不存在");
		}
		return order;
	}

	private OrderView toOrderView(MallOrder order) {
		OrderView view = new OrderView();
		view.setId(order.getId());
		view.setOrderNo(order.getOrderNo());
		view.setUserId(order.getUserId());
		view.setTotalAmount(order.getTotalAmount());
		view.setStatus(order.getStatus());
		view.setConsigneeName(order.getConsigneeName());
		view.setConsigneePhone(order.getConsigneePhone());
		view.setConsigneeAddress(order.getConsigneeAddress());
		view.setCreateTime(order.getCreateTime());
		view.setPayTime(order.getPayTime());
		List<MallOrderItem> items = mallOrderItemMapper.selectList(new LambdaQueryWrapper<MallOrderItem>()
				.eq(MallOrderItem::getOrderId, order.getId())
				.orderByAsc(MallOrderItem::getId));
		List<OrderItemView> itemViews = new ArrayList<>();
		for (MallOrderItem orderItem : items) {
			OrderItemView itemView = new OrderItemView();
			itemView.setId(orderItem.getId());
			itemView.setItemId(orderItem.getItemId());
			itemView.setItemTitle(orderItem.getItemTitle());
			itemView.setItemImg(orderItem.getItemImg());
			itemView.setItemPrice(orderItem.getItemPrice());
			itemView.setNum(orderItem.getNum());
			itemView.setSubtotal(orderItem.getSubtotal());
			itemViews.add(itemView);
		}
		view.setItems(itemViews);
		return view;
	}

	private String generateOrderNo() {
		return "MO" + LocalDateTime.now().format(ORDER_NO_FORMAT)
				+ String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
	}

	private String safeTitle(Integer itemId) {
		Item item = itemMapper.selectById(itemId);
		return item == null ? String.valueOf(itemId) : item.getTitle();
	}

	private ShopTxResult txResult() {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		return result;
	}
}
