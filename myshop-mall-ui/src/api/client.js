/**
 * 统一请求封装：
 * - 登录态 token 存 localStorage（key: mall_token），所有请求自动带 X-Mall-Token 头
 * - 401（未登录/登录过期）：清除本地 token 并跳转登录页（非登录页时）
 * - HTTP 错误：解析 violations / RestError.message 拼出可读信息后抛错
 * - ShopTxResult 业务失败（success=false）：不抛错，由调用方处理 message/reasons
 */
export const TOKEN_KEY = 'mall_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export async function request(path, { json, method = 'POST' } = {}) {
  const options = { method, headers: {} }
  // GET/HEAD 请求不能携带 body（fetch 规范会直接抛错），仅写请求序列化 JSON
  const upper = method.toUpperCase()
  if (json !== undefined && upper !== 'GET' && upper !== 'HEAD') {
    options.headers['Content-Type'] = 'application/json'
    options.body = JSON.stringify(json)
  }
  options.headers['X-Mall-Token'] = getToken() || ''
  const resp = await fetch(path, options)
  const text = await resp.text()
  let data = null
  try {
    data = text ? JSON.parse(text) : null
  } catch {
    data = { message: text }
  }
  if (resp.status === 401) {
    clearToken()
    if (!location.hash.startsWith('#/login')) {
      location.hash = '#/login'
    }
    throw new Error(data?.message || '请先登录')
  }
  if (!resp.ok) {
    const parts = []
    if (data?.violations?.length) parts.push(...data.violations)
    if (data?.message) parts.push(data.message)
    throw new Error(parts.join('；') || `请求失败（HTTP ${resp.status}）`)
  }
  return data
}

/** ShopTxResult 业务失败时，把 reasons 对象转成可读文案 */
export function reasonsText(txResult) {
  if (!txResult?.reasons || Object.keys(txResult.reasons).length === 0) {
    if (txResult?.success === false) return txResult?.message || '操作失败'
    return ''
  }
  return Object.values(txResult.reasons).join('；')
}
