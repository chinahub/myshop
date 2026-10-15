import React, { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { listMyOrders, cancelOrder, payOrder } from '../api/mall.js'
import { formatPrice, orderStatus } from '../App.jsx'

const PAGE_SIZE = 10

export default function Orders() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [data, setData] = useState({ total: 0, list: [] })
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  const page = Math.max(1, Number(searchParams.get('page') || 1))

  const refresh = () => {
    setLoading(true)
    listMyOrders({ start: (page - 1) * PAGE_SIZE, length: PAGE_SIZE })
      .then((resp) => setData({ total: resp.total ?? 0, list: resp.list ?? [] }))
      .catch((e) => setMessage(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(refresh, [searchParams])

  const act = async (fn, id) => {
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

  const totalPages = Math.max(1, Math.ceil(data.total / PAGE_SIZE))

  if (loading) return <p className="empty-tip">加载中…</p>

  return (
    <div>
      <h2 className="pagetitle">我的订单</h2>
      {message && (
        <p className="msgline ok">{message} <a href="#/orders">刷新</a></p>
      )}
      {data.list.length === 0 ? (
        <p className="empty-tip"><a href="#/items">还没有订单，先去下单</a></p>
      ) : (
        <table className="carttable">
          <thead>
            <tr>
              <th>订单号</th>
              <th>商品</th>
              <th>金额</th>
              <th>状态</th>
              <th>下单时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {data.list.map((order) => {
              const st = orderStatus(order.status)
              const first = order.items?.[0]
              return (
                <tr key={order.id}>
                  <td className="mono">{order.orderNo}</td>
                  <td>
                    <a href={`#/order/${order.id}`}>
                      {first ? first.itemTitle : '-'}
                      {order.items?.length > 1 ? ` 等 ${order.items.length} 件` : ''}
                    </a>
                  </td>
                  <td className="price">{formatPrice(order.totalAmount)}</td>
                  <td><span className={`st ${st.cls}`}>{st.text}</span></td>
                  <td>{(order.createTime || '').replace('T', ' ').slice(0, 19)}</td>
                  <td>
                    {order.status === 1 && (
                      <>
                        <button className="btn-link pay" onClick={() => act(payOrder, order.id)}>付款</button>
                        <button className="btn-link" onClick={() => act(cancelOrder, order.id)}>取消</button>
                      </>
                    )}
                    <button className="btn-link" onClick={() => navigate(`/order/${order.id}`)}>详情</button>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      )}
      {totalPages > 1 && (
        <div className="pager">
          <button disabled={page <= 1}
            onClick={() => setSearchParams(page > 1 ? { page: page - 1 } : {})}>上一页</button>
          <span>第 {page} / {totalPages} 页（共 {data.total} 单）</span>
          <button disabled={page >= totalPages}
            onClick={() => setSearchParams({ page: page + 1 })}>下一页</button>
        </div>
      )}
    </div>
  )
}
