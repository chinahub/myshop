import React, { createContext, useContext, useEffect, useState } from 'react'
import { Routes, Route, Link, NavLink, useNavigate } from 'react-router-dom'
import { getMe, logout, getDictData } from './api/mall.js'
import { getToken, clearToken } from './api/client.js'
import Home from './pages/Home.jsx'
import ItemList from './pages/ItemList.jsx'
import ItemDetail from './pages/ItemDetail.jsx'
import Cart from './pages/Cart.jsx'
import Checkout from './pages/Checkout.jsx'
import Orders from './pages/Orders.jsx'
import OrderDetail from './pages/OrderDetail.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'

const UserContext = createContext({ user: null, setUser: () => {} })
export const useUser = () => useContext(UserContext)

// 订单状态映射：内置一份作保底（字典接口失败时兜底），App 启动后用 sys_dict_data 覆盖
let STATUS_META = {
  1: { text: '待付款', cls: 'st-unpaid' },
  2: { text: '已付款', cls: 'st-paid' },
  3: { text: '已取消', cls: 'st-cancelled' },
}
export const orderStatus = (status) => STATUS_META[String(status)] || { text: '未知', cls: '' }

/** 拉取 order_status 字典覆盖保底映射；label 取文案，cls 优先用 extra.cls，缺省沿用保底样式 */
async function applyOrderStatusDict() {
  const list = await getDictData('order_status')
  if (!Array.isArray(list) || list.length === 0) return
  const next = {}
  for (const item of list) {
    let cls = ''
    try {
      cls = item.extra ? JSON.parse(item.extra)?.cls || '' : ''
    } catch {
      cls = ''
    }
    next[String(item.value)] = {
      text: item.label,
      cls: cls || STATUS_META[String(item.value)]?.cls || '',
    }
  }
  STATUS_META = next
}

export function formatPrice(value) {
  const num = Number(value ?? 0)
  return `¥${num.toFixed(2)}`
}

export default function App() {
  const [user, setUser] = useState(null)
  const [, setDictVersion] = useState(0)
  const navigate = useNavigate()

  // 已存 token 时恢复登录态（token 失效时 client.js 已清 token，此处静默失败即可）
  useEffect(() => {
    if (!getToken()) return
    getMe()
      .then((me) => setUser({ userId: me.userId, username: me.username, nickname: me.nickname }))
      .catch(() => setUser(null))
  }, [])

  // 订单状态文案改由字典驱动；成功后 bump 一次 state 让已挂载的订单页重渲染
  useEffect(() => {
    applyOrderStatusDict()
      .then(() => setDictVersion((v) => v + 1))
      .catch(() => {
        // 字典不可用时沿用内置保底映射
      })
  }, [])

  const onLogout = async () => {
    try {
      await logout()
    } catch {
      // 忽略登出失败
    }
    clearToken()
    setUser(null)
    navigate('/')
  }

  return (
    <UserContext.Provider value={{ user, setUser }}>
      <header className="topbar">
        <div className="topbar-inner">
          <Link to="/" className="brand">
            myshop<span>商城</span>
          </Link>
          <nav className="mainnav">
            <NavLink to="/" end>首页</NavLink>
            <NavLink to="/items">全部商品</NavLink>
            <NavLink to="/orders">我的订单</NavLink>
          </nav>
          <div className="userbox">
            <Link to="/cart" className="cartlink">🛒 购物车</Link>
            {user ? (
              <>
                <span className="hello">{user.nickname}</span>
                <button className="btn-link" onClick={onLogout}>退出</button>
              </>
            ) : (
              <>
                <Link to="/login">登录</Link>
                <Link to="/register" className="reg">注册</Link>
              </>
            )}
          </div>
        </div>
      </header>

      <main className="container">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/items" element={<ItemList />} />
          <Route path="/item/:id" element={<ItemDetail />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/orders" element={<Orders />} />
          <Route path="/order/:id" element={<OrderDetail />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="*" element={<Home />} />
        </Routes>
      </main>

      <footer className="footer">
        myshop 商城 · Summer 单体应用演示 · 商品与库存数据来自后台管理端
      </footer>
    </UserContext.Provider>
  )
}
