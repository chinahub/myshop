package com.rocket.myshop.service;

import java.util.Map;

import com.rocket.myshop.dto.common.ShopQueryResult;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.dto.mall.OrderCreateRequest;
import com.rocket.myshop.dto.mall.OrderView;

/**
 * 订单服务：从购物车整单提交（扣库存、写快照、清购物车）、查询、模拟支付、取消（回补库存）。
 */
public interface MallOrderService {

    /** 提交订单，返回 {orderId, orderNo}；购物车为空/库存不足时失败。 */
    Map<String, Object> createOrder(Integer userId, OrderCreateRequest request);

    /** 当前会员订单分页（带明细），params: start/length。 */
    ShopQueryResult listMyOrders(Integer userId, Map<String, Object> params);

    /** 订单详情（校验归属），不存在或非本人抛 MallNotFoundException。 */
    OrderView getMyOrder(Integer userId, Integer orderId);

    /** 模拟支付：仅待付款订单可支付。 */
    ShopTxResult pay(Integer userId, Integer orderId);

    /** 取消订单：仅待付款订单可取消，取消回补库存。 */
    ShopTxResult cancel(Integer userId, Integer orderId);
}
