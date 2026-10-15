import { request } from './client.js'

// ---------------- 商品浏览（公开） ----------------

export const listMallItems = (params = {}) => {
  const qs = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') qs.set(k, v)
  })
  return request(`/mall/items/list.json?${qs}`, { method: 'GET', json: null })
}

export const getMallItem = (id) =>
  request(`/mall/items/detail.json?id=${encodeURIComponent(id)}`, { method: 'GET', json: null })

export const getCategories = () => request('/mall/items/categories.json', { method: 'GET', json: null })

export const getBrands = () => request('/mall/items/brands.json', { method: 'GET', json: null })

// ---------------- 会员认证 ----------------

export const register = ({ username, password, nickname }) =>
  request('/mall/auth/register', { json: { username, password, nickname } })

export const login = ({ username, password }) =>
  request('/mall/auth/login', { json: { username, password } })

export const logout = () => request('/mall/auth/logout', { json: null })

export const getMe = () => request('/mall/auth/me', { method: 'GET', json: null })

// ---------------- 购物车 ----------------

export const getCart = () => request('/mall/cart/list.json', { method: 'GET', json: null })

export const addToCart = (itemId, num) => request('/mall/cart/add', { json: { itemId, num } })

export const updateCartNum = (cartId, num) => request('/mall/cart/updateNum', { json: { cartId, num } })

export const removeCartItem = (cartId) =>
  request(`/mall/cart/delete?cartId=${encodeURIComponent(cartId)}`, { json: null })

// ---------------- 订单 ----------------

export const createOrder = (consignee) => request('/mall/orders/create', { json: consignee })

export const listMyOrders = (params = {}) => {
  const qs = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') qs.set(k, v)
  })
  return request(`/mall/orders/list.json?${qs}`, { method: 'GET', json: null })
}

export const getOrder = (id) =>
  request(`/mall/orders/detail.json?id=${encodeURIComponent(id)}`, { method: 'GET', json: null })

export const payOrder = (id) =>
  request(`/mall/orders/pay?id=${encodeURIComponent(id)}`, { json: null })

export const cancelOrder = (id) =>
  request(`/mall/orders/cancel?id=${encodeURIComponent(id)}`, { json: null })
