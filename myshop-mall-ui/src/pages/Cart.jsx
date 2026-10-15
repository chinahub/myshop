import React, { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getCart, updateCartNum, removeCartItem } from '../api/mall.js'
import { formatPrice } from '../App.jsx'

export default function Cart() {
  const [items, setItems] = useState([])
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  const refresh = useCallback(() => {
    getCart()
      .then((resp) => setItems(resp.list ?? []))
      .catch((e) => setMessage(e.message))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    refresh()
  }, [refresh])

  const total = items.reduce(
    (sum, it) => sum + Number(it.price ?? 0) * Number(it.num ?? 0),
    0,
  )

  const changeNum = async (it, delta) => {
    const next = it.num + delta
    setMessage('')
    try {
      const tx =
        next < 1
          ? await removeCartItem(it.id)
          : await updateCartNum(it.id, next)
      const reasonsText = Object.values(tx.reasons || {}).join('；')
      if (tx.success === false || reasonsText) {
        setMessage(reasonsText || tx.message || '操作失败')
      } else {
        refresh()
      }
    } catch (e) {
      setMessage(e.message)
    }
  }

  const remove = async (it) => {
    setMessage('')
    try {
      await removeCartItem(it.id)
      refresh()
    } catch (e) {
      setMessage(e.message)
    }
  }

  if (loading) return <p className="empty-tip">加载中…</p>

  return (
    <div>
      <h2 className="pagetitle">我的购物车</h2>
      {message && <p className="error-tip">{message}</p>}
      {items.length === 0 ? (
        <p className="empty-tip">
          购物车是空的，<a href="#/items">去逛逛</a>
        </p>
      ) : (
        <>
          <table className="carttable">
            <thead>
              <tr>
                <th className="colimg"></th>
                <th>商品</th>
                <th>单价</th>
                <th>数量</th>
                <th>小计</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {items.map((it) => (
                <tr key={it.id}>
                  <td className="colimg">
                    {it.imgLogo ? <img src={it.imgLogo} alt="" /> : <span className="noimg">无图</span>}
                  </td>
                  <td>
                    <a href={`#/item/${it.itemId}`}>{it.title}</a>
                    <div className="muted">库存 {it.stock} 件</div>
                  </td>
                  <td>{formatPrice(it.price)}</td>
                  <td>
                    <div className="numrow">
                      <button className="btn" onClick={() => changeNum(it, -1)}>-</button>
                      <input value={it.num} readOnly />
                      <button className="btn" onClick={() => changeNum(it, 1)} disabled={it.num >= it.stock}>+</button>
                    </div>
                  </td>
                  <td className="price">{formatPrice(Number(it.price) * Number(it.num))}</td>
                  <td>
                    <button className="btn-link" onClick={() => remove(it)}>删除</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="cartfooter">
            <span>
              共 {items.length} 种商品，合计 <b className="price">{formatPrice(total)}</b>
            </span>
            <button className="btn primary big" onClick={() => navigate('/checkout')}>去结算</button>
          </div>
        </>
      )}
    </div>
  )
}
