import { useEffect, useState } from 'react'
import { listEnabledDictData } from '../api/dict.js'

// 模块级缓存：dictType -> Promise<options[]>，页面切换不重复请求。
// 字典管理页增删改后须调 clearDictCache 让下一次挂载重新拉取。
const cache = new Map()

/** 把字典数据行规整为下拉/Tag 可用结构：extra 为 JSON 字符串（存 color/cls 等） */
function normalize(item) {
  let extra = {}
  try {
    extra = item.extra ? JSON.parse(item.extra) : {}
  } catch {
    extra = {}
  }
  return {
    value: String(item.value),
    label: item.label,
    sort: item.sort ?? 0,
    status: item.status,
    color: extra.color,
    cls: extra.cls,
    raw: item,
  }
}

/** 取字典并缓存（失败不缓存，下次重试） */
export function fetchDict(dictType) {
  if (!cache.has(dictType)) {
    cache.set(
      dictType,
      listEnabledDictData(dictType)
        .then((list) => (list ?? []).map(normalize))
        .catch((err) => {
          cache.delete(dictType)
          throw err
        }),
    )
  }
  return cache.get(dictType)
}

/** 清缓存：传 dictType 清单项，不传清全部 */
export function clearDictCache(dictType) {
  if (dictType) cache.delete(dictType)
  else cache.clear()
}

/** 状态值 → 字典 key 的统一转换：Boolean true/false 转 '1'/'0'（品牌状态），其余转字符串 */
export function dictKey(v) {
  if (v === true) return '1'
  if (v === false) return '0'
  return String(v ?? '')
}

/** 下拉/Radio 选项 hook：返回 [{ value, label, color, ... }]，加载失败保持空数组 */
export default function useDict(dictType) {
  const [options, setOptions] = useState([])
  useEffect(() => {
    let alive = true
    fetchDict(dictType)
      .then((list) => {
        if (alive) setOptions(list)
      })
      .catch(() => {
        // 字典接口失败：保持空选项，由调用方决定降级展示
      })
    return () => {
      alive = false
    }
  }, [dictType])
  return options
}

/** 表格状态列渲染用：返回 Map<字典key, option> */
export function useDictMap(dictType) {
  const options = useDict(dictType)
  return new Map(options.map((o) => [o.value, o]))
}
