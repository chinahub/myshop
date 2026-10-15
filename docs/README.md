# myshop 项目文档

2026-10 由 2016 年的 Spring 4 XML + MyBatis + Dubbo SOA 体系整体迁移至 **Summer 4.1.0** 单体纯 REST 架构。本目录是项目的完整说明文档，AI 协作开发约定见根目录 [AGENTS.md](../AGENTS.md)。

## 文档索引

| 文档 | 内容 |
| --- | --- |
| [架构与结构.md](架构与结构.md) | 工作区三工程布局、后端模块依赖、前端工程、部署形态 |
| [快速开始.md](快速开始.md) | 构建、运行、前后端开发模式、测试、种子数据、本地一键验证 |
| [接口清单.md](接口清单.md) | 后台管理 API 与商城 API 全量清单、统一返回格式、错误处理 |
| [数据库设计.md](数据库设计.md) | 全部数据表设计（管理表 + 商城表）与建表脚本位置 |
| [GUI验收报告.md](GUI验收报告.md) | 2026-10-15 商城上线前浏览器黑盒验收记录（含修复的 2 个 bug） |

## 项目速览

- **后端** `myshop-backend/`：Summer 4.1.0（自研 IoC/HTTP/BaseMapper ORM）+ Java 25 + MySQL，单体应用，一个 jar 同时提供 REST API、后台管理页面（`/admin/`）和商城页面（`/mall/`），根路径 `/` 为门户页。
- **后台管理前端** `myshop-manager-ui/`：React 18 + Vite + Ant Design 5，HashRouter，构建产物同步到后端 `static/admin/`。
- **商城前端** `myshop-mall-ui/`：React 18 + Vite（不依赖组件库，手写电商 CSS），HashRouter，构建产物同步到后端 `static/mall/`。
- **功能**：后台（商品/类别/品牌管理、工作台）；商城（商品浏览、搜索筛选、会员注册登录、购物车、下单、模拟支付、取消回补库存）。

## 版本历史

- 2016 原始架构：Spring4 XML + MyBatis + Dubbo/Zookeeper + JSP（git 历史 master 分支旧提交可查）。
- 2026-10 上旬：迁移 Summer 4.1.0 单体，删除 JSP，后台管理改 React。
- 2026-10-15：新增商城前端 myshop-mall-ui 与商城后端（认证/购物车/订单），工作区重组为三工程 monorepo。
