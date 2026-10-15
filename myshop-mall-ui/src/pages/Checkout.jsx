import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getCart, createOrder } from '../api/mall.js'
import { formatPrice } from '../App.jsx'

const ADDRESS_KEY = 'mall_consignee'

export default function Checkout() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [form, setForm] = useState({ consigneeName: '', consigneePhone: '', consigneeAddress: '' })
  const navigate = useNavigate()

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(ADDRESS_KEY))
      if (saved) setForm(saved)
    } catch {
      // 忽略损坏的本地缓存
    }
    getCart()
      .then((resp) => setItems(resp.list ?? []))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  const total = items.reduce(
    (sum, it) => sum + Number(it.price ?? 0) * Number(it.num ?? 0),
    0,
  )

  const submit = async () => {
    setMessage('')
    if (!form.consigneeName.trim() || !form.consigneePhone.trim() || !form.consigneeAddress.trim()) {
      setMessage('请完整填写收货人、电话与地址')
      return
    }
    if (items.length === 0) {
      setMessage('购物车为空')
      return
    }
    setSubmitting(true)
    try {
      localStorage.setItem(ADDRESS_KEY, JSON.stringify(form))
      const data = await createOrder(form)
      navigate(`/order/${data.orderId}`)
    } catch (e) {
      setMessage(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) return <p className="empty-tip">加载中…</p>
  if (error) return <p className="error-tip">{error}</p>
  if (items.length === 0 && !message) {
    return <p className="empty-tip">购物车为空，<a href="#/items">先去挑点东西</a></p>
  }

  return (
    <div>
      <h2 className="pagetitle">确认订单</h2>
      <div className="checkout-grid">
        <div className="checkout-form">
          <h3>收货信息</h3>
          <label>
            收货人
            <input
              value={form.consigneeName}
              onChange={(e) => setForm({ ...form, consigneeName: e.target.value })}
              maxLength={40}
              placeholder="姓名"
            />
          </label>
          <label>
            手机号码
            <input
              value={form.consigneePhone}
              onChange={(e) => setForm({ ...form, consigneePhone: e.target.value })}
              maxLength={20}
              placeholder="用于联系收货"
            />
          </label>
          <label>
            收货地址
            <textarea
              rows={3}
              value={form.consigneeAddress}
              onChange={(e) => setForm({ ...form, consigneeAddress: e.target.value })}
              maxLength={200}
              placeholder="省市区街道门牌"
            />
          </label>
        </div>
        <div className="checkout-summary">
          <h3>商品清单</h3>
          <ul>
            {items.map((it) => (
              <li key={it.id}>
                <span className="sumtitle">{it.title} × {it.num}</span>
                <span>{formatPrice(Number(it.price) * Number(it.num))}</span>
              </li>
            ))}
          </ul>
          <div className="sumtotal">
            应付总额 <b className="price">{formatPrice(total)}</b>
          </div>
          <button className="btn primary big" onClick={submit} disabled={submitting}>
            {submitting ? '提交中…' : '提交订单'}
          </button>
          <p className="muted">提交后为「待付款」状态，可在订单页模拟支付；不接真实支付。</p>
        </div>
      </div>
      {message && <p className="error-tip">{message}</p>}
    </div>
  )
}
