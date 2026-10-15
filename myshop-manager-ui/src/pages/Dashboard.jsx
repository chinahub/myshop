import React, { useEffect, useState } from 'react'
import { Card, Col, Row, Statistic, Alert, Spin } from 'antd'
import { ShoppingOutlined, TagsOutlined, AppstoreOutlined } from '@ant-design/icons'
import { listItem, listItemBrands, listItemCategory } from '../api/shop.js'

export default function Dashboard() {
  const [totals, setTotals] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    Promise.allSettled([
      listItem({ start: 0, length: 1 }),
      listItemBrands({ start: 0, length: 1 }),
      listItemCategory({ start: 0, length: 1 }),
    ]).then(([items, brands, categories]) => {
      const next = {}
      if (items.status === 'fulfilled') next.items = items.value.total
      if (brands.status === 'fulfilled') next.brands = brands.value.total
      if (categories.status === 'fulfilled') next.categories = categories.value.total
      setTotals(next)
      if (items.status === 'rejected' && brands.status === 'rejected') {
        setError('无法连接后端接口，请确认服务已启动且数据库可用')
      }
      setLoading(false)
    })
  }, [])

  return (
    <div>
      {error && (
        <Alert type="warning" showIcon message={error} style={{ marginBottom: 16 }} />
      )}
      <Row gutter={16}>
        <Col span={8}>
          <Card>
            <Spin spinning={loading}>
              <Statistic title="商品总数" value={totals.items ?? '-'} prefix={<ShoppingOutlined />} />
            </Spin>
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Spin spinning={loading}>
              <Statistic title="品牌总数" value={totals.brands ?? '-'} prefix={<TagsOutlined />} />
            </Spin>
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Spin spinning={loading}>
              <Statistic
                title="一级类别数"
                value={totals.categories ?? '-'}
                prefix={<AppstoreOutlined />}
              />
            </Spin>
          </Card>
        </Col>
      </Row>
    </div>
  )
}
