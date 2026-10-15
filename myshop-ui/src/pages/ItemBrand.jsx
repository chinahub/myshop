import React, { useEffect, useState } from 'react'
import {
  Button,
  Form,
  Input,
  Modal,
  Popconfirm,
  Space,
  Table,
  Tag,
  message,
} from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import {
  listItemBrands,
  addItemBrand,
  updateItemBrand,
  deleteItemBrand,
} from '../api/shop.js'
import { reasonsText } from '../api/client.js'

const PAGE_SIZE = 10

export default function ItemBrand() {
  const [form] = Form.useForm()
  const [data, setData] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [loading, setLoading] = useState(false)
  const [queryName, setQueryName] = useState('')
  const [modal, setModal] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const load = async (pageNum = 1) => {
    setLoading(true)
    try {
      const params = { start: (pageNum - 1) * PAGE_SIZE, length: PAGE_SIZE }
      if (queryName) params.name = queryName
      const result = await listItemBrands(params)
      setData(result.list ?? [])
      setTotal(result.total ?? 0)
      setPage(pageNum)
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

  const openAdd = () => {
    setModal({ mode: 'add' })
    form.resetFields()
  }

  const openEdit = (record) => {
    setModal({ mode: 'edit', record })
    form.setFieldsValue({ name: record.name, logo: record.logo })
  }

  const submit = async () => {
    const values = await form.validateFields()
    setSubmitting(true)
    try {
      const isAdd = modal.mode === 'add'
      const payload = isAdd ? values : { ...values, id: modal.record.id }
      const result = isAdd ? await addItemBrand(payload) : await updateItemBrand(payload)
      if (result.success) {
        message.success(result.message || '操作成功')
        setModal(null)
        load(1)
      } else {
        message.error(reasonsText(result))
      }
    } catch (e) {
      if (e.errorFields) return
      message.error(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  const onDelete = async (id) => {
    try {
      const result = await deleteItemBrand(id)
      if (result.success) {
        message.success(result.message || '删除成功')
        load(page)
      } else {
        message.error(result.message || '删除失败')
      }
    } catch (e) {
      message.error(e.message)
    }
  }

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '品牌名称', dataIndex: 'name' },
    {
      title: 'LOGO',
      dataIndex: 'logo',
      ellipsis: true,
      render: (v) => v || '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (v) => (v ? <Tag color="green">正常</Tag> : <Tag color="red">删除</Tag>),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 170,
      render: (v) => (v ? new Date(v).toLocaleString('zh-CN') : '-'),
    },
    {
      title: '操作',
      width: 130,
      render: (_, record) => (
        <Space size={0}>
          <Button type="link" size="small" onClick={() => openEdit(record)}>
            修改
          </Button>
          <Popconfirm title="确认删除该品牌？" onConfirm={() => onDelete(record.id)}>
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
      <Space style={{ marginBottom: 16 }} wrap>
        <Input
          placeholder="品牌名称"
          style={{ width: 200 }}
          value={queryName}
          onChange={(e) => setQueryName(e.target.value)}
          onPressEnter={() => load(1)}
        />
        <Button type="primary" onClick={() => load(1)}>
          搜索
        </Button>
        <Button type="primary" ghost icon={<PlusOutlined />} onClick={openAdd}>
          新增品牌
        </Button>
      </Space>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={data}
        loading={loading}
        pagination={{
          current: page,
          pageSize: PAGE_SIZE,
          total,
          showTotal: (t) => `共 ${t} 条`,
          onChange: (p) => load(p),
        }}
      />
      <Modal
        title={modal?.mode === 'edit' ? '更改品牌' : '添加品牌'}
        open={modal !== null}
        confirmLoading={submitting}
        onOk={submit}
        onCancel={() => setModal(null)}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="品牌名称"
            name="name"
            rules={[{ required: true, whitespace: true, message: '品牌名称不能为空' }]}
          >
            <Input maxLength={40} />
          </Form.Item>
          <Form.Item label="品牌 LOGO 地址" name="logo">
            <Input placeholder="http://..." maxLength={500} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
