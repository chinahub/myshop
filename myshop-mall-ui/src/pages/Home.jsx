import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listMallItems } from '../api/mall.js'
import { formatPrice } from '../App.jsx'

export const ItemCard = ({ item }) => (
  <Link className="itemcard" to={`/item/${item.id}`}>
    <div className="imgbox">
      {item.imgLogo ? (
        <img src={item.imgLogo} alt={item.title} loading="lazy" />
      ) : (
        <span className="noimg">暂无图片</span>
      )}
    </div>
    <div className="title">{item.title}</div>
    <div className="sellpoint">{item.sellPoint}</div>
    <div className="pricerow">
      <span className="price">{formatPrice(item.price)}</span>
      <span className="meta">{item.itemCategoryName || ''}</span>
    </div>
  </Link>
)

export default function Home() {
  const [data, setData] = useState({ total: 0, list: [] })
  const [error, setError] = useState('')

  useEffect(() => {
    listMallItems({ start: 0, length: 12 })
      .then((resp) => setData({ total: resp.total ?? 0, list: resp.list ?? [] }))
      .catch((e) => setError(e.message))
  }, [])

  return (
    <div>
      <section className="hero">
        <h1>本周精选 · 好物上新</h1>
        <p>共 {data.total} 件在售商品，来自后台运营实时上架</p>
        <Link className="hero-btn" to="/items">浏览全部商品</Link>
      </section>
      {error && <p className="error-tip">{error}</p>}
      <section className="grid">
        {data.list.length > 0 ? (
          data.list.map((item) => <ItemCard key={item.id} item={item} />)
        ) : (
          !error && <p className="empty-tip">暂无上架商品，请先在后台添加</p>
        )}
      </section>
    </div>
  )
}
