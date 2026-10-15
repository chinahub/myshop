import { request } from './client.js'

// ---------------- 商品 ----------------

export const listItem = (params) => request('/items/listItem.json', { json: params })

export const getItem = (id) =>
  request(`/items/detail.json?id=${encodeURIComponent(id)}`, { method: 'GET' })

export const addItem = (item) => request('/items/addItem', { json: item })

export const updateItem = (item) => request('/items/updateItem', { json: item })

export const deleteItem = (id) =>
  request(`/items/deleteItem?id=${encodeURIComponent(id)}`, { json: null })

// ---------------- 品牌 ----------------

export const listItemBrands = (params) => request('/items/listItemsBrand.json', { json: params })

export const addItemBrand = (brand) => request('/items/addItemsBrand.json', { json: brand })

export const updateItemBrand = (brand) => request('/items/updateItemsBrand.json', { json: brand })

export const deleteItemBrand = (id) =>
  request(`/items/deleteItemsBrand.json?id=${encodeURIComponent(id)}`, { json: null })

// ---------------- 类别 ----------------

export const listItemCategory = (params) => request('/items/listItemsCategory.json', { json: params })

export const getSubItemsCategory = (parentId) =>
  request(`/items/getSubItemsCategory.json?parentId=${encodeURIComponent(parentId)}`, { json: null })

export const addItemsCategory = (category) => request('/items/addItemsCategory.json', { json: category })

export const updateItemsCategory = (category) =>
  request('/items/updateItemsCategory.json', { json: category })

export const deleteItemsCategory = (categoryId) =>
  request(`/items/deleteItemsCategory.json?categoryId=${encodeURIComponent(categoryId)}`, { json: null })
