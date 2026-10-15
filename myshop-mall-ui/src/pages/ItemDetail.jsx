import React, { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getMallItem, addToCart } from '../api/mall.js'
import { getToken } from '../api/client.js'
import { useUser, formatPrice } from '../App.jsx'

export default function ItemDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useUser()
  const [item, setItem] = useState(null)
  const [num, setNum] = useState(1)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    getMallItem(id)
      .then((data) => setItem(data))
      .catch((e) => setError(e.message))
  }, [id])

  const ensureLogin = () => {
    if (user || getToken()) return true
    navigate('/login')
    return false
  }

  const doAddToCart = async () => {
    setMessage('')
    if (!ensureLogin()) return
    try {
      const tx = await addToCart(item.id, num)
      const reasonsText = Object.values(tx.reasons || {}).join('；')
      if (tx.success === false || reasonsText) {
        setMessage(reasonsText || tx.message || '操作失败')
      } else {
        setMessage('已加入购物车，去结算？')
      }
    } catch (e) {
      setMessage(e.message)
    }
  }

  const doBuyNow = async () => {
    if (!ensureLogin()) return
    try {
      const tx = await addToCart(item.id, num)
      const reasonsText = Object.values(tx.reasons || {}).join('；')
      if (tx.success === false || reasonsText) {
        setMessage(reasonsText || tx.message || '操作失败')
        return
      }
      navigate('/checkout')
    } catch (e) {
      setMessage(e.message)
    }
  }

  if (error) return <p className="error-tip">{error}</p>
  if (!item) return <p className="empty-tip">加载中…</p>

  return (
    <div className="detail">
      <div className="detail-img">
        {item.imgLogo ? (
          <img src={item.imgLogo} alt={item.title} />
        ) : (
          <span className="noimg">暂无图片</span>
        )}
      </div>
      <div className="detail-info">
        <h1>{item.title}</h1>
        <p className="sellpoint">{item.sellPoint}</p>
        <div className="price-big">{formatPrice(item.price)}</div>
        <dl className="kv">
          <div><dt>类别</dt><dd>{item.itemCategoryName || '-'}</dd></div>
          <div><dt>品牌</dt><dd>{item.itemBrandName || '-'}</dd></div>
          <div><dt>库存</dt><dd>{item.num} 件</dd></div>
        </dl>
        <div className="numrow">
          <span>数量</span>
          <button className="btn" onClick={() => setNum((n) => Math.max(1, n - 1))}>-</button>
          <input value={num} readOnly />
          <button className="btn" onClick={() => setNum((n) => Math.min(item.num || 99, n + 1))}>+</button>
          <span className="stockwarn">限 {item.num} 件</span>
        </div>
        <div className="actrow">
          <button className="btn primary big" onClick={doBuyNow}>立即购买</button>
          <button className="btn big" onClick={doAddToCart}>加入购物车</button>
        </div>
        {message && (
          <p className="msgline">
            {message}
            {message.includes('已加入购物车') && <Link to="/cart"> → 去购物车结算</Link>}
          </p>
        )}
      </div>
    </div>
  )
}
