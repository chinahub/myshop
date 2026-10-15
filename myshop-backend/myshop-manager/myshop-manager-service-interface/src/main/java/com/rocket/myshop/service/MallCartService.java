package com.rocket.myshop.service;

import java.util.List;

import com.rocket.myshop.dto.mall.CartAddRequest;
import com.rocket.myshop.dto.mall.CartItemView;
import com.rocket.myshop.dto.common.ShopTxResult;

/**
 * 购物车服务：数量合并加购、改数量、删除、清空（下单后调用）。
 */
public interface MallCartService {

    List<CartItemView> listCart(Integer userId);

    ShopTxResult add(Integer userId, CartAddRequest request);

    ShopTxResult updateNum(Integer userId, Integer cartId, Integer num);

    ShopTxResult remove(Integer userId, Integer cartId);

    ShopTxResult clear(Integer userId);
}
