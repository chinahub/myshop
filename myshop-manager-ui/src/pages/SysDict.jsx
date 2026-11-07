import React, { useEffect, useState } from 'react'
import {
  Button,
  Card,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Select,
  Space,
  Table,
  Tag,
  message,
} from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import {
  listDicts,
  addDict,
  updateDict,
  deleteDict,
  listDictData,
  addDictData,
  updateDictData,
  deleteDictData,
} from '../api/dict.js'
import { clearDictCache } from '../hooks/useDict.js'
import { reasonsText } from '../api/client.js'

const PAGE_SIZE = 10

// 字典自身的启停是管理动作，不走字典表（避免自引用）
const STATUS_OPTIONS = [
  { value: 1, label: '启用' },
  { value: 0, label: '停用' },
]
const statusTag = (v) =>
  Number(v) === 1 ? <Tag color="green">启用</Tag> : <Tag color="red">停用</Tag>

/** 安全解析 extra JSON 用于预览 Tag 颜色 */
function extraColor(extra) {
  try {
    return extra ? JSON.parse(extra)?.color : undefined
  } catch {
    return undefined
  }
}

export default function SysDict() {
  // ---- 字典类型 ----
  const [typeForm] = Form.useForm()
  const [types, setTypes] = useState([])
  const [typeTotal, setTypeTotal] = useState(0)
  const [typePage, setTypePage] = useState(1)
  const [typeLoading, setTypeLoading] = useState(false)
  const [queryDictType, setQueryDictType] = useState('')
  const [typeModal, setTypeModal] = useState(null) // null | {mode:'add'} | {mode:'edit', record}
  const [typeSubmitting, setTypeSubmitting] = useState(false)

  // ---- 字典数据（选中类型后展示） ----
  const [dataForm] = Form.useForm()
  const [activeType, setActiveType] = useState(null) // { dictType, dictName }
  const [dictData, setDictData] = useState([])
  const [dataTotal, setDataTotal] = useState(0)
  const [dataPage, setDataPage] = useState(1)
  const [dataLoading, setDataLoading] = useState(false)
  const [dataModal, setDataModal] = useState(null) // null | {mode:'add'} | {mode:'edit', record}
  const [dataSubmitting, setDataSubmitting] = useState(false)

  const loadTypes = async (pageNum = 1) => {
    setTypeLoading(true)
    try {
      const params = { start: (pageNum - 1) * PAGE_SIZE, length: PAGE_SIZE }
      if (queryDictType) params.dictType = queryDictType
      const result = await listDicts(params)
      setTypes(result.list ?? [])
      setTypeTotal(result.total ?? 0)
      setTypePage(pageNum)
    } catch (e) {
      message.error(e.message)
    } finally {
      setTypeLoading(false)
    }
  }

  const loadData = async (dictType, pageNum = 1) => {
    setDataLoading(true)
    try {
      const result = await listDictData({
        dictType,
        start: (pageNum - 1) * PAGE_SIZE,
        length: PAGE_SIZE,
      })
      setDictData(result.list ?? [])
      setDataTotal(result.total ?? 0)
      setDataPage(pageNum)
    } catch (e) {
      message.error(e.message)
    } finally {
      setDataLoading(false)
    }
  }

  useEffect(() => {
    loadTypes(1)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const openTypeAdd = () => {
    setTypeModal({ mode: 'add' })
    typeForm.resetFields()
  }

  const openTypeEdit = (record) => {
    setTypeModal({ mode: 'edit', record })
    typeForm.setFieldsValue(record)
  }

  const submitType = async () => {
    const values = await typeForm.validateFields()
    setTypeSubmitting(true)
    try {
      const isAdd = typeModal.mode === 'add'
      const payload = isAdd ? values : { ...values, id: typeModal.record.id }
      const result = isAdd ? await addDict(payload) : await updateDict(payload)
      if (result.success) {
        message.success(result.message || '操作成功')
        setTypeModal(null)
        // 类型编码可能变更：字典数据挂靠关系变了，全量清缓存
        clearDictCache()
        loadTypes(typePage)
      } else {
        message.error(reasonsText(result))
      }
    } catch (e) {
      if (e.errorFields) return
      message.error(e.message)
    } finally {
      setTypeSubmitting(false)
    }
  }

  const onDeleteType = async (record) => {
    try {
      const result = await deleteDict(record.id)
      if (result.success) {
        message.success(result.message || '删除成功')
        clearDictCache(record.dictType)
        if (activeType?.dictType === record.dictType) {
          setActiveType(null)
          setDictData([])
          setDataTotal(0)
        }
        loadTypes(typePage)
      } else {
        message.error(result.message || '删除失败')
      }
    } catch (e) {
      message.error(e.message)
    }
  }

  const showData = (record) => {
    setActiveType({ dictType: record.dictType, dictName: record.dictName })
    loadData(record.dictType, 1)
  }

  const openDataAdd = () => {
    setDataModal({ mode: 'add' })
    dataForm.resetFields()
    dataForm.setFieldsValue({ status: 1, sort: 0 })
  }

  const openDataEdit = (record) => {
    setDataModal({ mode: 'edit', record })
    dataForm.setFieldsValue(record)
  }

  const submitData = async () => {
    const values = await dataForm.validateFields()
    setDataSubmitting(true)
    try {
      const isAdd = dataModal.mode === 'add'
      const payload = { ...values, dictType: activeType.dictType }
      const result = isAdd
        ? await addDictData(payload)
        : await updateDictData({ ...payload, id: dataModal.record.id })
      if (result.success) {
        message.success(result.message || '操作成功')
        setDataModal(null)
        clearDictCache(activeType.dictType)
        loadData(activeType.dictType, dataPage)
      } else {
        message.error(reasonsText(result))
      }
    } catch (e) {
      if (e.errorFields) return
      message.error(e.message)
    } finally {
      setDataSubmitting(false)
    }
  }

  const onDeleteData = async (record) => {
    try {
      const result = await deleteDictData(record.id)
      if (result.success) {
        message.success(result.message || '删除成功')
        clearDictCache(activeType.dictType)
        loadData(activeType.dictType, dataPage)
      } else {
        message.error(result.message || '删除失败')
      }
    } catch (e) {
      message.error(e.message)
    }
  }

  const typeColumns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '类型编码', dataIndex: 'dictType', width: 160 },
    { title: '字典名称', dataIndex: 'dictName', width: 140 },
    { title: '备注', dataIndex: 'remark', ellipsis: true, render: (v) => v || '-' },
    { title: '状态', dataIndex: 'status', width: 80, render: statusTag },
    {
      title: '操作',
      width: 220,
      render: (_, record) => (
        <Space size={0}>
          <Button type="link" size="small" onClick={() => showData(record)}>
            字典数据
          </Button>
          <Button type="link" size="small" onClick={() => openTypeEdit(record)}>
            修改
          </Button>
          <Popconfirm
            title="删除该字典类型会同时删除其全部字典数据，确认？"
            onConfirm={() => onDeleteType(record)}
          >
            <Button type="link" size="small" danger>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  const dataColumns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '值', dataIndex: 'value', width: 120 },
    {
      title: '标签',
      dataIndex: 'label',
      width: 140,
      render: (v, record) => <Tag color={extraColor(record.extra) || 'default'}>{v}</Tag>,
    },
    { title: '排序', dataIndex: 'sort', width: 80 },
    { title: '扩展 extra', dataIndex: 'extra', ellipsis: true, render: (v) => v || '-' },
    { title: '状态', dataIndex: 'status', width: 80, render: statusTag },
    {
      title: '操作',
      width: 130,
      render: (_, record) => (
        <Space size={0}>
          <Button type="link" size="small" onClick={() => openDataEdit(record)}>
            修改
          </Button>
          <Popconfirm title="确认删除该字典数据？" onConfirm={() => onDeleteData(record)}>
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
          placeholder="字典类型编码"
          style={{ width: 200 }}
          value={queryDictType}
          onChange={(e) => setQueryDictType(e.target.value)}
          onPressEnter={() => loadTypes(1)}
        />
        <Button type="primary" onClick={() => loadTypes(1)}>
          搜索
        </Button>
        <Button type="primary" ghost icon={<PlusOutlined />} onClick={openTypeAdd}>
          新增字典类型
        </Button>
      </Space>

      <Table
        size="small"
        rowKey="id"
        columns={typeColumns}
        dataSource={types}
        loading={typeLoading}
        pagination={{
          current: typePage,
          pageSize: PAGE_SIZE,
          total: typeTotal,
          showTotal: (t) => `共 ${t} 条`,
          onChange: (p) => loadTypes(p),
        }}
      />

      {activeType && (
        <Card
          size="small"
          title={`字典数据 — ${activeType.dictName}（${activeType.dictType}）`}
          style={{ marginTop: 24 }}
          extra={
            <Button type="primary" ghost icon={<PlusOutlined />} onClick={openDataAdd}>
              新增字典数据
            </Button>
          }
        >
          <Table
            size="small"
            rowKey="id"
            columns={dataColumns}
            dataSource={dictData}
            loading={dataLoading}
            pagination={{
              current: dataPage,
              pageSize: PAGE_SIZE,
              total: dataTotal,
              showTotal: (t) => `共 ${t} 条`,
              onChange: (p) => loadData(activeType.dictType, p),
            }}
          />
        </Card>
      )}

      <Modal
        title={typeModal?.mode === 'edit' ? '更改字典类型' : '添加字典类型'}
        open={typeModal !== null}
        confirmLoading={typeSubmitting}
        onOk={submitType}
        onCancel={() => setTypeModal(null)}
        destroyOnClose
      >
        <Form form={typeForm} layout="vertical" initialValues={{ status: 1 }}>
          <Form.Item
            label="类型编码"
            name="dictType"
            rules={[{ required: true, whitespace: true, message: '类型编码不能为空' }]}
            extra="如 item_status；编码变更会同步更新其下字典数据"
          >
            <Input maxLength={50} placeholder="字母/数字/下划线" />
          </Form.Item>
          <Form.Item
            label="字典名称"
            name="dictName"
            rules={[{ required: true, whitespace: true, message: '字典名称不能为空' }]}
          >
            <Input maxLength={50} placeholder="如：商品状态" />
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <Input maxLength={200} />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select options={STATUS_OPTIONS} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={dataModal?.mode === 'edit' ? '更改字典数据' : '添加字典数据'}
        open={dataModal !== null}
        confirmLoading={dataSubmitting}
        onOk={submitData}
        onCancel={() => setDataModal(null)}
        destroyOnClose
      >
        <Form form={dataForm} layout="vertical">
          <Form.Item
            label="字典值"
            name="value"
            rules={[{ required: true, whitespace: true, message: '字典值不能为空' }]}
            extra="统一按字符串存储；如 1、0"
          >
            <Input maxLength={50} />
          </Form.Item>
          <Form.Item
            label="字典标签"
            name="label"
            rules={[{ required: true, whitespace: true, message: '字典标签不能为空' }]}
          >
            <Input maxLength={100} placeholder="如：在售" />
          </Form.Item>
          <Form.Item label="排序" name="sort">
            <InputNumber min={0} precision={0} style={{ width: 120 }} />
          </Form.Item>
          <Form.Item
            label="扩展 extra"
            name="extra"
            extra="可选 JSON，如 {&quot;color&quot;:&quot;green&quot;} 控制 Tag 颜色"
          >
            <Input maxLength={200} placeholder='{"color":"green"}' />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select options={STATUS_OPTIONS} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
