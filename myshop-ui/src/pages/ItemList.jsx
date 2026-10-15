import React, { useEffect, useState } from 'react'
import { Button, Modal, Popconfirm, Space, Table, Tag, message } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { listItem, deleteItem } from '../api/shop.js'

const PAGE_SIZE = 10

export default function ItemList() {
  const navigate = useNavigate()
  const [data, setData] = useState([])
  const [total, setTotal] = useState(0)
  const [current, setCurrent] = useState(1)
  const [loading, setLoading] = useState(false)

  const load = async (page = current) => {
    setLoading(true)
    try {
      const result = await listItem({ start: (page - 1) * PAGE_SIZE, length: PAGE_SIZE })
      setData(result.list ?? [])
      setTotal(result.total ?? 0)
      setCurrent(page)
    } catch (e) {
      message.error(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load(1)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const onDelete = async (id) => {
    try {
      const result = await deleteItem(id)
      if (result.success) {
        message.success(result.message || '删除成功')
        load(current)
      } else {
        message.error(result.message || '删除失败')
      }
    } catch (e) {
      message.error(e.message)
    }
  }

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    {
      title: '商品名称',
      dataIndex: 'title',
      ellipsis: true,
    },
    { title: '类目', dataIndex: 'itemCategoryName', width: 110 },
    { title: '品牌', dataIndex: 'itemBrandName', width: 110 },
    { title: '销售价', dataIndex: 'price', width: 90 },
    { title: '库存', dataIndex: 'num', width: 70 },
    { title: '条形码', dataIndex: 'barcode', width: 110 },
    { title: '销售渠道', dataIndex: 'saleClients', width: 110, ellipsis: true },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (v) => (v === '1' ? <Tag color="green">在售</Tag> : <Tag color="red">下架</Tag>),
    },
    {
      title: '操作',
      width: 140,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => navigate(`/items/edit/${record.id}`)}>
            编辑
          </Button>
          <Popconfirm title="确认删除该商品？" onConfirm={() => onDelete(record.id)}>
            <Button type="link" size="small" danger>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/items/add')}>
          添加商品
        </Button>
      </Space>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={data}
        loading={loading}
        pagination={{
          current,
          pageSize: PAGE_SIZE,
          total,
          showTotal: (t) => `共 ${t} 条`,
          onChange: (page) => load(page),
        }}
      />
    </div>
  )
}
