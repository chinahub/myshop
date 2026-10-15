# myshop

Java 电商后台：**Summer 框架 4.1.0**（`cn.jiebaba.summer`，自研类 Spring Boot 微服务框架）+ **React + Vite + Ant Design** 前端，前后端一体部署的单体纯 REST 应用。

> 2026-10 由 2016 年的 Spring4 XML + MyBatis + Dubbo SOA 架构整体迁移而来，详细开发约定见 [AGENTS.md](AGENTS.md)。

## 技术栈

- 后端：Summer 4.1.0（IoC/嵌入式 HTTP/BaseMapper ORM/校验/事务），Java 25，MySQL
- 前端：React 18 + Vite 5 + Ant Design 5，构建产物由 Summer 静态资源托管
- 无 Spring、无 MyBatis、无 JSP、无 Dubbo/Zookeeper

## 快速开始

```bash
# 0. 前置：JDK 25；Summer 4.1.0 已 install 到本地仓库（源码在 E:\summer_workspace）
#    数据库：MySQL 建库 myshop 并执行 项目文档/myshop-manager.sql（或用下文的 H2 联调方式）

# 1. 构建后端（含 9 个端到端冒烟测试）
mvn clean install

# 2. 构建前端并同步到后端静态资源
cd myshop-ui && npm install && npm run sync && cd ..

# 3. 打包一体化 jar（页面 + API）
mvn -pl myshop-manager/myshop-manager-web -am package -DskipTests

# 4. 运行
java -jar myshop-manager/myshop-manager-web/target/myshop-manager-boot.jar
# 打开 http://localhost:8080

# 前端开发模式（热更新，代理到后端 8080）
cd myshop-ui && npm run dev   # http://localhost:5173
```

## 模块结构

```
myshop-ui                前端工程（React+Vite+Antd）
myshop-common            工具类
myshop-manager           后端主体：pojo / dto / mapper / service-interface / service / web
myshop-user              用户端占位骨架（待实现）
项目文档                 建表 SQL + 需求文档
dev/h2-init.sql          本地无 MySQL 时的 H2 联调脚本
```

## 功能

后台管理：工作台数据概览、商品列表（增删改查/分页）、商品类别（树形懒展开/增删改）、品牌管理（增删改查）、商品规格设置（建设中）。REST API 同名 `.json` 接口对外开放。
