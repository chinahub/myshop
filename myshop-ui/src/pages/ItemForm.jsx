import React, { useEffect, useState } from 'react'
import { Button, Card, Form, Input, InputNumber, Radio, message } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { addItem, updateItem, getItem } from '../api/shop.js'
import { reasonsText } from '../api/client.js'

export default function ItemForm() {
  const navigate = useNavigate()
  const { id } = useParams()
  const isEdit = Boolean(id)
  const [form] = Form.useForm()
  const [submitting, setSubmitting] = useState(false)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!isEdit) return
    setLoading(true)
    getItem(id)
      .then((item) => {
        form.setFieldsValue(item ?? {})
      })
      .catch((e) => message.error(e.message))
      .finally(() => setLoading(false))
  }, [id, isEdit, form])

  const onFinish = async (values) => {
    setSubmitting(true)
    try {
      const payload = isEdit ? { ...values, id: Number(id) } : values
      const result = isEdit ? await updateItem(payload) : await addItem(payload)
      if (result.success) {
        message.success(result.message || (isEdit ? '更新成功' : '添加成功'))
        navigate('/items')
      } else {
        message.error(reasonsText(result))
        // 把后端 reasons 映射回表单字段错误
        if (result.reasons) {
          form.setFields(
            Object.entries(result.reasons).map(([name, msg]) => ({ name, errors: [msg] })),
          )
        }
      }
    } catch (e) {
      message.error(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card loading={loading} style={{ maxWidth: 720 }}>
      <Form form={form} layout="vertical" onFinish={onFinish} initialValues={{ status: '1' }}>
        <Form.Item
          label="商品名称"
          name="title"
          rules={[{ required: true, message: '商品名称不能为空' }]}
        >
          <Input placeholder="商品标题" maxLength={100} />
        </Form.Item>
        <Form.Item label="商品卖点" name="sellPoint">
          <Input.TextArea placeholder="卖点描述" maxLength={500} rows={2} />
        </Form.Item>
        <Form.Item label="销售价" name="price" rules={[{ required: true, message: '销售价不能为空' }]}>
          <InputNumber min={0} precision={2} style={{ width: 200 }} addonAfter="元" />
        </Form.Item>
        <Form.Item label="库存数量" name="num" rules={[{ required: true, message: '库存不能为空' }]}>
          <InputNumber min={0} precision={0} style={{ width: 200 }} />
        </Form.Item>
        <Form.Item label="条形码" name="barcode">
          <Input placeholder="商品条形码" maxLength={40} />
        </Form.Item>
        <Form.Item label="商品主图地址" name="imgLogo">
          <Input placeholder="http://..." maxLength={500} />
        </Form.Item>
        <Form.Item label="商品类目名称" name="itemCategoryName">
          <Input placeholder="类目名称" maxLength={40} />
        </Form.Item>
        <Form.Item label="品牌名称" name="itemBrandName">
          <Input placeholder="品牌名称" maxLength={40} />
        </Form.Item>
        <Form.Item label="销售渠道" name="saleClients">
          <Input placeholder="如：PC，APP" maxLength={100} />
        </Form.Item>
        <Form.Item label="商家ID" name="sellerId">
          <Input maxLength={40} />
        </Form.Item>
        <Form.Item label="状态" name="status">
          <Radio.Group>
            <Radio value="1">在售</Radio>
            <Radio value="0">下架</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={submitting}>
            {isEdit ? '保存' : '添加'}
          </Button>
          <Button style={{ marginLeft: 12 }} onClick={() => navigate('/items')}>
            返回
          </Button>
        </Form.Item>
      </Form>
    </Card>
  )
}
