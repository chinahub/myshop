/**
 * 统一请求封装：
 * - JSON POST 默认；json=null 发空体
 * - @RequestParam 接口（删除/子类目查询等）用查询串传参：直接把 ?id=1 拼在 path 里
 * - HTTP 错误（404/500 等）：解析 violations / RestError.message 拼出可读信息后抛错
 * - 业务错误（HTTP 200 但 success=false）：由调用方按返回值处理（展示 reasons）
 */
export async function request(path, { json, method = 'POST' } = {}) {
  const options = { method, headers: { 'Content-Type': 'application/json' } }
  options.body = json === undefined ? '' : JSON.stringify(json)
  const resp = await fetch(path, options)
  const text = await resp.text()
  let data = null
  try {
    data = text ? JSON.parse(text) : null
  } catch {
    data = { message: text }
  }
  if (!resp.ok) {
    const parts = []
    if (data?.violations?.length) parts.push(...data.violations)
    if (data?.message) parts.push(data.message)
    throw new Error(parts.join('；') || `请求失败（HTTP ${resp.status}）`)
  }
  return data
}

/** ShopResult 业务失败时，把 reasons 对象转成可读文案 */
export function reasonsText(txResult) {
  if (!txResult?.reasons) return txResult?.message || '操作失败'
  return Object.entries(txResult.reasons)
    .map(([field, msg]) => msg)
    .join('；')
}
