import React, { createContext, useContext, useEffect, useState } from 'react'
import { Routes, Route, Link, NavLink, useNavigate } from 'react-router-dom'
import { getMe, logout } from './api/mall.js'
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

const STATUS_META = {
  1: { text: '待付款', cls: 'st-unpaid' },
  2: { text: '已付款', cls: 'st-paid' },
  3: { text: '已取消', cls: 'st-cancelled' },
}
export const orderStatus = (status) => STATUS_META[status] || { text: '未知', cls: '' }

export function formatPrice(value) {
  const num = Number(value ?? 0)
  return `¥${num.toFixed(2)}`
}

export default function App() {
  const [user, setUser] = useState(null)
  const navigate = useNavigate()

  // 已存 token 时恢复登录态（token 失效时 client.js 已清 token，此处静默失败即可）
  useEffect(() => {
    if (!getToken()) return
    getMe()
      .then((me) => setUser({ userId: me.userId, username: me.username, nickname: me.nickname }))
      .catch(() => setUser(null))
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
        myshop 商城 · Summer 4.1.0 单体应用演示 · 商品与库存数据来自后台管理端
      </footer>
    </UserContext.Provider>
  )
}
