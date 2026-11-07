import { request } from './client.js'

// ---------------- 前端消费 ----------------

// 按字典类型取启用数据（不分页，sort 升序），下拉框/状态 Tag 渲染统一走这里
export const listEnabledDictData = (dictType) =>
  request(`/sysDict/data/enabled.json?dictType=${encodeURIComponent(dictType)}`, { method: 'GET' })

// ---------------- 字典类型管理 ----------------

export const listDicts = (params) => request('/sysDict/list.json', { json: params })

export const addDict = (dict) => request('/sysDict/add.json', { json: dict })

export const updateDict = (dict) => request('/sysDict/update.json', { json: dict })

export const deleteDict = (id) =>
  request(`/sysDict/delete.json?id=${encodeURIComponent(id)}`, { json: null })

// ---------------- 字典数据管理 ----------------

export const listDictData = (params) => request('/sysDict/data/list.json', { json: params })

export const addDictData = (data) => request('/sysDict/data/add.json', { json: data })

export const updateDictData = (data) => request('/sysDict/data/update.json', { json: data })

export const deleteDictData = (id) =>
  request(`/sysDict/data/delete.json?id=${encodeURIComponent(id)}`, { json: null })
