package com.rocket.myshop.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rocket.myshop.domain.Item;
import com.rocket.myshop.domain.MallCart;
import com.rocket.myshop.dto.common.ShopTxResult;
import com.rocket.myshop.dto.mall.CartAddRequest;
import com.rocket.myshop.dto.mall.CartItemView;
import com.rocket.myshop.exception.MallNotFoundException;
import com.rocket.myshop.mapper.ItemMapper;
import com.rocket.myshop.mapper.MallCartMapper;
import com.rocket.myshop.service.MallCartService;
import com.rocket.myshop.service.MallCatalogService;

import cn.jiebaba.summer.core.annotation.Autowired;
import cn.jiebaba.summer.core.annotation.Service;
import cn.jiebaba.summer.data.conditions.LambdaQueryWrapper;
import cn.jiebaba.summer.data.transaction.Transactional;

@Service("MallCartService")
@Transactional(rollbackFor = Exception.class)
public class MallCartServiceImpl implements MallCartService {

	@Autowired
	MallCartMapper mallCartMapper;

	@Autowired
	ItemMapper itemMapper;

	@Autowired
	MallCatalogService mallCatalogService;

	@Override
	@Transactional(readOnly = true)
	public List<CartItemView> listCart(Integer userId) {
		List<MallCart> carts = mallCartMapper.selectList(new LambdaQueryWrapper<MallCart>()
				.eq(MallCart::getUserId, userId)
				.orderByDesc(MallCart::getId));
		List<CartItemView> views = new ArrayList<>();
		for (MallCart cart : carts) {
			Item item = itemMapper.selectById(cart.getItemId());
			if (item == null || !"1".equals(item.getStatus())) {
				// 商品已下架/删除：购物车行保留但不出视图，下单时也会被跳过
				continue;
			}
			CartItemView view = new CartItemView();
			view.setId(cart.getId());
			view.setItemId(item.getId());
			view.setNum(cart.getNum());
			view.setTitle(item.getTitle());
			view.setSellPoint(item.getSellPoint());
			view.setPrice(item.getPrice());
			view.setImgLogo(item.getImgLogo());
			view.setStock(item.getNum());
			views.add(view);
		}
		return views;
	}

	@Override
	public ShopTxResult add(Integer userId, CartAddRequest request) {
		ShopTxResult result = txResult();
		Item item = mallCatalogService.requireItemOnSale(request.getItemId());
		MallCart cart = mallCartMapper.selectOne(new LambdaQueryWrapper<MallCart>()
				.eq(MallCart::getUserId, userId)
				.eq(MallCart::getItemId, request.getItemId()));
		int targetNum = cart == null ? request.getNum() : cart.getNum() + request.getNum();
		if (targetNum > item.getNum()) {
			result.setSuccess(false);
			result.getReasons().put("num", "商品库存不足，最多可购 " + item.getNum() + " 件");
			return result;
		}
		Date now = new Date();
		if (cart == null) {
			cart = new MallCart();
			cart.setUserId(userId);
			cart.setItemId(item.getId());
			cart.setNum(targetNum);
			cart.setCreateTime(now);
			cart.setUpdateTime(now);
			mallCartMapper.insert(cart);
		} else {
			cart.setNum(targetNum);
			cart.setUpdateTime(now);
			mallCartMapper.updateById(cart);
		}
		result.setMessage("已加入购物车");
		result.setSuccess(true);
		return result;
	}

	@Override
	public ShopTxResult updateNum(Integer userId, Integer cartId, Integer num) {
		ShopTxResult result = txResult();
		MallCart cart = ownedCart(userId, cartId);
		Item item = mallCatalogService.requireItemOnSale(cart.getItemId());
		if (num > item.getNum()) {
			result.setSuccess(false);
			result.getReasons().put("num", "商品库存不足，最多可购 " + item.getNum() + " 件");
			return result;
		}
		cart.setNum(num);
		cart.setUpdateTime(new Date());
		mallCartMapper.updateById(cart);
		result.setMessage("数量已更新");
		result.setSuccess(true);
		return result;
	}

	@Override
	public ShopTxResult remove(Integer userId, Integer cartId) {
		ShopTxResult result = txResult();
		MallCart cart = ownedCart(userId, cartId);
		mallCartMapper.deleteById(cart.getId());
		result.setMessage("已从购物车移除");
		result.setSuccess(true);
		return result;
	}

	@Override
	public ShopTxResult clear(Integer userId) {
		List<MallCart> carts = mallCartMapper.selectList(new LambdaQueryWrapper<MallCart>()
				.eq(MallCart::getUserId, userId));
		for (MallCart cart : carts) {
			mallCartMapper.deleteById(cart.getId());
		}
		ShopTxResult result = txResult();
		result.setMessage("购物车已清空");
		result.setSuccess(true);
		return result;
	}

	private MallCart ownedCart(Integer userId, Integer cartId) {
		MallCart cart = mallCartMapper.selectById(cartId);
		if (cart == null || !cart.getUserId().equals(userId)) {
			throw new MallNotFoundException("购物车条目不存在");
		}
		return cart;
	}

	private ShopTxResult txResult() {
		ShopTxResult result = new ShopTxResult();
		result.setVersion("1.0.0");
		return result;
	}
}
