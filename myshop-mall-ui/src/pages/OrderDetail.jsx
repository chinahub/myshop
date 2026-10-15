import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { getOrder, cancelOrder, payOrder } from '../api/mall.js'
import { formatPrice, orderStatus } from '../App.jsx'

export default function OrderDetail() {
  const { id } = useParams()
  const [order, setOrder] = useState(null)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const refresh = () => {
    getOrder(id)
      .then((data) => setOrder(data))
      .catch((e) => setError(e.message))
  }

  useEffect(refresh, [id])

  const act = async (fn) => {
    setMessage('')
    try {
      const tx = await fn(id)
      const reasonsText = Object.values(tx.reasons || {}).join('；')
      if (tx.success === false || reasonsText) {
        setMessage(reasonsText || tx.message || '操作失败')
      } else {
        setMessage(tx.message || '操作成功')
        refresh()
      }
    } catch (e) {
      setMessage(e.message)
    }
  }

  if (error) return <p className="error-tip">{error}</p>
  if (!order) return <p className="empty-tip">加载中…</p>

  const st = orderStatus(order.status)

  return (
    <div>
      <a href="#/orders">← 返回订单列表</a>
      <h2 className="pagetitle">订单详情 <span className={`st big ${st.cls}`}>{st.text}</span></h2>
      {message && <p className="msgline ok">{message}</p>}
      <dl className="kv orderkv">
        <div><dt>订单号</dt><dd className="mono">{order.orderNo}</dd></div>
        <div><dt>下单时间</dt><dd>{(order.createTime || '').replace('T', ' ').slice(0, 19)}</dd></div>
        {order.payTime && (
          <div><dt>付款时间</dt><dd>{order.payTime.replace('T', ' ').slice(0, 19)}</dd></div>
        )}
        <div><dt>收货人</dt><dd>{order.consigneeName} {order.consigneePhone}</dd></div>
        <div><dt>收货地址</dt><dd>{order.consigneeAddress}</dd></div>
      </dl>
      <table className="carttable">
        <thead>
          <tr>
            <th className="colimg"></th>
            <th>商品</th>
            <th>单价</th>
            <th>数量</th>
            <th>小计</th>
          </tr>
        </thead>
        <tbody>
          {(order.items || []).map((it) => (
            <tr key={it.id}>
              <td className="colimg">
                {it.itemImg ? <img src={it.itemImg} alt="" /> : <span className="noimg">无图</span>}
              </td>
              <td>{it.itemTitle}</td>
              <td>{formatPrice(it.itemPrice)}</td>
              <td>{it.num}</td>
              <td className="price">{formatPrice(it.subtotal)}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="cartfooter">
        <span>合计 <b className="price">{formatPrice(order.totalAmount)}</b></span>
        {order.status === 1 && (
          <span>
            <button className="btn primary big" onClick={() => act(payOrder)}>模拟支付</button>
            <button className="btn big" onClick={() => act(cancelOrder)}>取消订单</button>
          </span>
        )}
      </div>
    </div>
  )
}
