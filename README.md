# myshop

Java 电商单体应用：**Summer 框架 4.1.0**（`cn.jiebaba.summer`，自研类 Spring Boot 框架）后端 + **两个 React 前端**（后台管理 + 商城），前后端一体部署。完整项目文档见 [docs/](docs/README.md)。

> 2026-10 由 2016 年的 Spring4 XML + MyBatis + Dubbo SOA 架构整体迁移而来。开发约定见 [AGENTS.md](AGENTS.md)。

## 技术栈

- 后端：Summer 4.1.0（IoC/嵌入式 HTTP/BaseMapper ORM/校验/事务），Java 25，MySQL
- 前端：React 18 + Vite。后台管理用 Ant Design 5，商城不用组件库（手写电商 CSS），均 HashRouter
- 无 Spring、无 MyBatis、无 JSP、无 Dubbo/Zookeeper

## 工作区结构（monorepo，一个后端两个前端）

```
myshop-backend/        后端（Maven 多模块，部署一体化 jar）
myshop-manager-ui/     后台管理前端 -> static/admin/
myshop-mall-ui/        商城前端 -> static/mall/
docs/                  项目文档（架构/快速开始/接口清单/数据库设计/验收报告）
start.bat              一键启动
```

## 快速开始

```bash
# 0. 前置：JDK 25；Summer 4.1.0 已 install 到本地仓库（源码在 E:\summer_workspace）
#    数据库：MySQL 建库 myshop 并执行 myshop-backend/项目文档/myshop-manager.sql

# 1. 构建后端（含 10 个端到端冒烟测试）
cd myshop-backend && mvn clean install

# 2. 构建两个前端并同步到后端静态资源
cd ../myshop-manager-ui && npm install && npm run sync && cd ..
cd ../myshop-mall-ui && npm install && npm run sync && cd ..

# 3. 打包一体化 jar（门户 + 后台 + 商城 + API）
cd myshop-backend && mvn -pl myshop-manager/myshop-manager-web package -DskipTests

# 4. 运行
java -jar myshop-manager/myshop-manager-web/target/myshop-manager-boot.jar
# 打开 http://localhost:8080            门户
#      http://localhost:8080/mall/     商城（注册登录→购物车→下单→模拟支付）
#      http://localhost:8080/admin/    后台管理（商品/类别/品牌）

# 前端开发模式（热更新，代理到后端 8080）
cd myshop-manager-ui && npm run dev    # http://localhost:5173/admin/
cd myshop-mall-ui && npm run dev       # http://localhost:5174/mall/
```

本地无 MySQL 时用 H2 联调，命令见 [docs/快速开始.md](docs/快速开始.md)；商城种子数据：`myshop-backend/dev/mall-seed.sql`（22 个图文匹配的演示商品）。

## 功能

- **后台管理**（/admin/）：工作台数据概览、商品列表（增删改查/分页）、商品类别（树形懒展开/增删改）、品牌管理（增删改查）、商品规格设置（建设中）。
- **商城**（/mall/）：商品首页与列表（关键词/类别/品牌筛选、分页）、商品详情、会员注册登录（SHA-256 加盐、内存 token）、购物车（合并加购/改量/删除、库存校验）、下单（快照明细、扣库存、清购物车）、模拟支付、取消订单（回补库存）。
- REST API 命名规则：管理接口 `/items/** /users/**`，商城接口 `/mall/**`。

## 一键启动

双击 `start.bat`（或命令行执行）：自动设置 JDK 25 并启动 jar，访问 http://localhost:8080。
