import React from 'react'
import { Card, Empty } from 'antd'

/** 功能占位页：原系统即未实现的功能（如商品规格设置） */
export default function Placeholder({ title }) {
  return (
    <Card>
      <Empty description={`${title}：功能建设中（原系统未实现该页面）`} />
    </Card>
  )
}
