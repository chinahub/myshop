import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { register } from '../api/mall.js'
import { setToken } from '../api/client.js'
import { useUser } from '../App.jsx'

export default function Register() {
  const [form, setForm] = useState({ username: '', password: '', confirm: '', nickname: '' })
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const navigate = useNavigate()
  const { setUser } = useUser()

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    if (form.password !== form.confirm) {
      setError('两次输入的密码不一致')
      return
    }
    setSubmitting(true)
    try {
      const result = await register({
        username: form.username,
        password: form.password,
        nickname: form.nickname || undefined,
      })
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
      <h2>注册 myshop 商城</h2>
      <form onSubmit={submit}>
        <label>
          用户名（3-40 位）
          <input
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            autoFocus
          />
        </label>
        <label>
          密码（6-40 位）
          <input
            type="password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />
        </label>
        <label>
          确认密码
          <input
            type="password"
            value={form.confirm}
            onChange={(e) => setForm({ ...form, confirm: e.target.value })}
          />
        </label>
        <label>
          昵称（可选，默认同用户名）
          <input
            value={form.nickname}
            onChange={(e) => setForm({ ...form, nickname: e.target.value })}
          />
        </label>
        {error && <p className="error-tip">{error}</p>}
        <button className="btn primary big wide" type="submit" disabled={submitting}>
          {submitting ? '注册中…' : '注册并登录'}
        </button>
      </form>
      <p className="authfoot">已有账号？<Link to="/login">直接登录</Link></p>
    </div>
  )
}
