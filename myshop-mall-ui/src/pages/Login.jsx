import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { login } from '../api/mall.js'
import { setToken } from '../api/client.js'
import { useUser } from '../App.jsx'

export default function Login() {
  const [form, setForm] = useState({ username: '', password: '' })
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const navigate = useNavigate()
  const { setUser } = useUser()

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const result = await login(form)
      setToken(result.token)
      setUser({ userId: result.userId, username: result.username, nickname: result.nickname })
      navigate('/')
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="authbox">
      <h2>登录 myshop 商城</h2>
      <form onSubmit={submit}>
        <label>
          用户名
          <input
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            autoFocus
          />
        </label>
        <label>
          密码
          <input
            type="password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />
        </label>
        {error && <p className="error-tip">{error}</p>}
        <button className="btn primary big wide" type="submit" disabled={submitting}>
          {submitting ? '登录中…' : '登录'}
        </button>
      </form>
      <p className="authfoot">还没有账号？<Link to="/register">立即注册</Link></p>
    </div>
  )
}
