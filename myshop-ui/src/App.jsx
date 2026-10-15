import React from 'react'
import { Routes, Route, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { Layout, Menu } from 'antd'
import {
  DashboardOutlined,
  ShoppingOutlined,
  PlusCircleOutlined,
  AppstoreOutlined,
  TagsOutlined,
  SettingOutlined,
} from '@ant-design/icons'
import Dashboard from './pages/Dashboard.jsx'
import ItemList from './pages/ItemList.jsx'
import ItemForm from './pages/ItemForm.jsx'
import ItemCategory from './pages/ItemCategory.jsx'
import ItemBrand from './pages/ItemBrand.jsx'
import Placeholder from './pages/Placeholder.jsx'

const { Header, Sider, Content } = Layout

const MENU_ITEMS = [
  { key: '/', icon: <DashboardOutlined />, label: '个人工作台' },
  { key: '/items', icon: <ShoppingOutlined />, label: '商品列表' },
  { key: '/items/add', icon: <PlusCircleOutlined />, label: '添加商品' },
  { key: '/categories', icon: <AppstoreOutlined />, label: '商品类别列表' },
  { key: '/brands', icon: <TagsOutlined />, label: '商品品牌管理' },
  { key: '/specs', icon: <SettingOutlined />, label: '商品规格设置' },
]

export default function App() {
  const location = useLocation()
  const navigate = useNavigate()
  // /items/edit/3 高亮"商品列表"；/items/add 有独立菜单项
  const selectedKey = location.pathname.startsWith('/items/edit')
    ? '/items'
    : location.pathname

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="dark" width={200}>
        <div
          style={{
            color: '#fff',
            fontSize: 18,
            fontWeight: 600,
            padding: '16px',
            textAlign: 'center',
          }}
        >
          myshop 后台管理
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={MENU_ITEMS}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            background: '#fff',
            padding: '0 24px',
            fontSize: 16,
            borderBottom: '1px solid #f0f0f0',
          }}
        >
          {MENU_ITEMS.find((m) => m.key === selectedKey)?.label ?? 'myshop'}
        </Header>
        <Content style={{ margin: 24 }}>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/items" element={<ItemList />} />
            <Route path="/items/add" element={<ItemForm />} />
            <Route path="/items/edit/:id" element={<ItemForm />} />
            <Route path="/categories" element={<ItemCategory />} />
            <Route path="/brands" element={<ItemBrand />} />
            <Route path="/specs" element={<Placeholder title="商品规格设置" />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Content>
      </Layout>
    </Layout>
  )
}
