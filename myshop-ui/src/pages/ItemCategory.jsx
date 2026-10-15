import React, { useEffect, useState } from 'react'
import {
  Button,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Space,
  Table,
  message,
} from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import {
  listItemCategory,
  getSubItemsCategory,
  addItemsCategory,
  updateItemsCategory,
  deleteItemsCategory,
} from '../api/shop.js'
import { reasonsText } from '../api/client.js'

const PAGE_SIZE = 10

export default function ItemCategory() {
  const [form] = Form.useForm()
  const [data, setData] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [loading, setLoading] = useState(false)
  const [queryName, setQueryName] = useState('')
  // 弹窗状态：null 关闭；{mode:'add', parentId} / {mode:'edit', record}
  const [modal, setModal] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const load = async (pageNum = 1) => {
    setLoading(true)
    try {
      const params = { start: (pageNum - 1) * PAGE_SIZE, length: PAGE_SIZE, level: 1 }
      if (queryName) params.name = queryName
      const result = await listItemCategory(params)
      // antd Table 需要行上有 children 字段才进入树模式；isLeaf:false 让所有行显示展开钮
      setData((result.list ?? []).map((c) => ({ ...c, isLeaf: false, children: [] })))
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

  // 树形懒展开：首次展开某行时拉取其子类别
  const onExpand = async (expanded, record) => {
    if (!expanded || record.children?.length || record._loaded) return
    try {
      const result = await getSubItemsCategory(record.id)
      const children = (result.list ?? []).map((c) => ({ ...c, isLeaf: false, children: [] }))
      setData((prev) =>
        updateNode(prev, record.id, (node) => ({
          ...node,
          children,
          _loaded: true,
        })),
      )
    } catch (e) {
      message.error(e.message)
    }
  }

  const updateNode = (nodes, id, updater) =>
    nodes.map((n) => {
      if (n.id === id) return updater(n)
      if (n.children?.length) return { ...n, children: updateNode(n.children, id, updater) }
      return n
    })

  const openAdd = (record) => {
    setModal({ mode: 'add', parentId: record?.id ?? null })
    form.resetFields()
  }

  const openEdit = (record) => {
    setModal({ mode: 'edit', record })
    form.setFieldsValue({ name: record.name, rank: record.rank })
  }

  const submit = async () => {
    const values = await form.validateFields()
    setSubmitting(true)
    try {
      const isAdd = modal.mode === 'add'
      const payload = isAdd
        ? { ...values, parentId: modal.parentId }
        : { id: modal.record.id, ...values }
      const result = isAdd
        ? await addItemsCategory(payload)
        : await updateItemsCategory(payload)
      if (result.success) {
        message.success(result.message || '操作成功')
        setModal(null)
        load(1)
      } else {
        message.error(reasonsText(result))
      }
    } catch (e) {
      if (e.errorFields) return // 表单校验错误已在字段上展示
      message.error(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  const onDelete = async (id) => {
    try {
      const result = await deleteItemsCategory(id)
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

  const fmtTime = (v) => (v ? new Date(v).toLocaleString('zh-CN') : '-')

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '类别名称', dataIndex: 'name' },
    { title: '子类别数', dataIndex: 'subCategoryNum', width: 90 },
    { title: '商品数量', dataIndex: 'itemsNum', width: 90 },
    { title: '排序值', dataIndex: 'rank', width: 80 },
    { title: '层级', dataIndex: 'level', width: 70 },
    { title: '创建时间', dataIndex: 'createTime', width: 170, render: fmtTime },
    {
      title: '操作',
      width: 230,
      render: (_, record) => (
        <Space size={0} wrap>
          <Button type="link" size="small" onClick={() => openAdd(record)}>
            添加子类别
          </Button>
          <Button type="link" size="small" onClick={() => openEdit(record)}>
            修改
          </Button>
          <Popconfirm title="确认删除该商品类别？" onConfirm={() => onDelete(record.id)}>
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
          placeholder="类别名称"
          style={{ width: 200 }}
          value={queryName}
          onChange={(e) => setQueryName(e.target.value)}
          onPressEnter={() => load(1)}
        />
        <Button type="primary" onClick={() => load(1)}>
          搜索
        </Button>
        <Button type="primary" ghost icon={<PlusOutlined />} onClick={() => openAdd(null)}>
          新增顶层类别
        </Button>
      </Space>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={data}
        loading={loading}
        onExpand={onExpand}
        pagination={{
          current: page,
          pageSize: PAGE_SIZE,
          total,
          showTotal: (t) => `共 ${t} 条（展开子类别不计入分页）`,
          onChange: (p) => load(p),
        }}
      />
      <Modal
        title={
          modal?.mode === 'edit'
            ? '更改商品类别'
            : modal?.parentId
              ? '添加子类别'
              : '添加顶层类别'
        }
        open={modal !== null}
        confirmLoading={submitting}
        onOk={submit}
        onCancel={() => setModal(null)}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="类别名称"
            name="name"
            rules={[{ required: true, whitespace: true, message: '类别名称不能为空' }]}
          >
            <Input maxLength={40} />
          </Form.Item>
          <Form.Item label="排序值" name="rank">
            <InputNumber min={0} precision={0} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
