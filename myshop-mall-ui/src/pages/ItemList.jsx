import React, { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { listMallItems, getCategories, getBrands } from '../api/mall.js'
import { ItemCard } from './Home.jsx'

const PAGE_SIZE = 12

export default function ItemList() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [categories, setCategories] = useState([])
  const [brands, setBrands] = useState([])
  const [data, setData] = useState({ total: 0, list: [] })
  const [keyword, setKeyword] = useState(searchParams.get('keyword') || '')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const page = Math.max(1, Number(searchParams.get('page') || 1))
  const category = searchParams.get('category') || ''
  const brand = searchParams.get('brand') || ''

  useEffect(() => {
    getCategories().then((list) => setCategories(list || [])).catch(() => setCategories([]))
    getBrands().then((list) => setBrands(list || [])).catch(() => setBrands([]))
  }, [])

  useEffect(() => {
    setLoading(true)
    listMallItems({
      start: (page - 1) * PAGE_SIZE,
      length: PAGE_SIZE,
      keyword: searchParams.get('keyword') || '',
      itemCategoryName: category,
      itemBrandName: brand,
    })
      .then((resp) => setData({ total: resp.total ?? 0, list: resp.list ?? [] }))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [searchParams])

  const patchParams = (patch) => {
    const next = new URLSearchParams(searchParams)
    Object.entries(patch).forEach(([k, v]) => {
      if (v === '' || v === null || v === undefined) next.delete(k)
      else next.set(k, v)
    })
    if (!('page' in patch)) next.delete('page')
    setSearchParams(next)
  }

  const subCategories = categories
    .flatMap((c) => [{ name: c.name }, ...(c.children || [])])
  const totalPages = Math.max(1, Math.ceil(data.total / PAGE_SIZE))

  return (
    <div>
      <div className="filterbar">
        <input
          placeholder="搜索商品关键词"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && patchParams({ keyword })}
        />
        <button className="btn primary" onClick={() => patchParams({ keyword })}>搜索</button>
        <select value={category} onChange={(e) => patchParams({ category: e.target.value })}>
          <option value="">全部类别</option>
          {subCategories.map((c, i) => (
            <option key={i} value={c.name}>{c.name}</option>
          ))}
        </select>
        <select value={brand} onChange={(e) => patchParams({ brand: e.target.value })}>
          <option value="">全部品牌</option>
          {brands.map((b) => (
            <option key={b.id} value={b.name}>{b.name}</option>
          ))}
        </select>
      </div>
      {error && <p className="error-tip">{error}</p>}
      {loading ? (
        <p className="empty-tip">加载中…</p>
      ) : (
        <section className="grid">
          {data.list.length > 0 ? (
            data.list.map((item) => <ItemCard key={item.id} item={item} />)
          ) : (
            <p className="empty-tip">没有符合条件的商品</p>
          )}
        </section>
      )}
      {totalPages > 1 && (
        <div className="pager">
          <button disabled={page <= 1} onClick={() => patchParams({ page: page - 1 })}>上一页</button>
          <span>第 {page} / {totalPages} 页（共 {data.total} 件）</span>
          <button disabled={page >= totalPages} onClick={() => patchParams({ page: page + 1 })}>下一页</button>
        </div>
      )}
    </div>
  )
}
