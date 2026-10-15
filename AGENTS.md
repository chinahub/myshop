# AGENTS.md

面向 AI 编码助手的项目指导文档。修改本仓库前请先读完本文件。

## 项目简介

myshop 是一个 Java 电商单体应用（Apache 2.0 协议）。**2026-10 已完成架构改造**：由 2016 年的 Spring 4 XML + MyBatis + Dubbo SOA 体系，整体迁移到自研 **Summer 框架 4.1.0**（`cn.jiebaba.summer`，源码在 `E:\summer_workspace`，同一作者 chinahub）之上，形成**单体、纯 REST、零 Servlet** 的应用。**2026-10-15 新增商城链路**：会员认证/购物车/订单后端 + myshop-mall-ui 前端。

## 工作区结构（monorepo）

git 仓库根在 `E:\myshop_workspace`（远端 chinahub/myshop），三个工程：

```
myshop-backend/        后端（本文件所述 Maven 多模块均在 myshop-backend/ 下）
myshop-manager-ui/     后台管理前端（React18+Vite+Antd，原 myshop-ui）
myshop-mall-ui/        商城前端（React18+Vite，无组件库）
docs/                  项目文档（架构/快速开始/接口清单/数据库设计/GUI验收报告）
```

## 技术栈（当前）

- **Summer 4.1.0**（`cn.jiebaba.summer:summer-boot`）：IoC/AOP/嵌入式 HTTP(虚拟线程)/BaseMapper ORM/校验/事务/JUL 日志，零 Servlet、零第三方依赖
- **Java 25**（`maven.compiler.release=25`，编译必须带 `-parameters`——已在根 POM 配置，Summer 靠参数名做 @PathVariable/@RequestParam 绑定）
- MySQL（mysql-connector-j 9.x，运行时）+ H2 2.3.232（仅测试）
- JUnit 5（端到端冒烟测试）
- 无 lombok、无 Jackson、无 Spring、无 MyBatis——JSON/校验/DI 全部使用 Summer 自带实现

## 构建与运行

> 本机默认 JAVA_HOME 是 JDK 17，**构建前必须切 JDK 25**：
> `export JAVA_HOME="D:\jdk\jdk-25.0.4" && export PATH="/d/jdk/jdk-25.0.4/bin:$PATH"`
> Summer 4.1.0 需先在 `E:\summer_workspace` 执行 `mvn -s settings.xml clean install -DskipTests` 装入本地仓库（本机已完成，含 3 个本地 bug 修复）。

```bash
# 全量构建（含 H2 冒烟测试，10 个用例）
mvn clean install

# 只跑冒烟测试
mvn test -pl myshop-manager/myshop-manager-web

# 本地运行（一体化 jar：门户/ /admin/ /mall/ + API）
java -jar myshop-manager/myshop-manager-web/target/myshop-manager-boot.jar
# 端口 8080；数据源配置见 src/main/resources/application.yml，
# 可用环境变量覆盖：SUMMER_DATASOURCE_URL / _USERNAME / _PASSWORD（松弛映射）
```

## 模块结构

```
myshop-backend (根 POM：版本管理 + dependencyManagement + pluginManagement)
├── myshop-common            纯 JDBC 工具类 SqlScriptRunner（无框架依赖）
├── myshop-manager           ★ 后端主体
│   ├── pojo                 实体：@TableName/@TableId/@TableField 映射（列名以下划线结尾，如 id_）
│   ├── dto                  ShopResult/ShopQueryResult/ShopTxResult + ItemCategoryDto + mall/*（Summer 校验注解）
│   ├── mapper               Mapper 接口，均 extends BaseMapper<T>（无 XML，复杂条件在 service 用 Wrapper）
│   ├── service-interface    服务接口（含 mall 4 个：MallAuth/Catalog/Cart/Order；商城业务异常 MallAuthException(401)/MallNotFoundException(404) 在此模块 com.rocket.myshop.exception）
│   ├── service              实现：@Service + @Autowired + @Transactional（Summer 注解）+ LambdaQueryWrapper（含 mall/auth/catalog/cart/order 4 个实现）
│   └── web (jar)            ManagerApplication 启动类 + 控制器（controller/mall/* 为商城）+ GlobalExceptionHandler + application.yml
│                            打包用 summer-pack-maven-plugin → myshop-manager-boot.jar（内嵌 static/ 门户+admin+mall）
└── myshop-user              占位骨架（7 个空 jar 模块，用户明确保留，待二期实现独立用户端服务）
```

依赖链：web → service → mapper/service-interface → pojo/dto。

## 关键 API 约定（Summer 特有，勿用 Spring 习惯）

- 注解包：IoC 用 `cn.jiebaba.summer.core.annotation.*`（@Service/@Autowired/@Configuration/@Bean）；Web 用 `cn.jiebaba.summer.web.annotation.*`（@RestController/@GetMapping/@PostMapping/@RequestBody/@RequestParam/@RequestHeader/@PathVariable）；校验用 `cn.jiebaba.summer.web.validation.*`（@NotNull/@NotBlank/@Size/@Min/@Max + @Valid 触发）；事务用 `cn.jiebaba.summer.data.transaction.@Transactional`。
- `@PostConstruct/@PreDestroy` 必须用 Summer 版（jakarta 版不会回调）。
- **ORM 列名陷阱**：MetadataParser 列名只读 `@TableField.value()`，`@TableId.value()` 不参与列名解析——主键字段必须同时标 `@TableId(value="id_", type=IdType.AUTO)` 和 `@TableField("id_")`，否则生成 `SELECT id`（缺尾下划线）导致查询失败。
- **MySQL tinyInt1isBit 陷阱**：JDBC 对 `TINYINT(1)` 列返回 Boolean。对应 Boolean 字段没问题（阿里云数据源转数字走 `!= 0`，本地 Summer 已修复），**但 Integer 字段（如订单状态 1/2/3）的列必须用 `TINYINT` 而非 `TINYINT(1)`**，否则运行时报 `Can not set Integer field ... to Boolean`（H2 不受影响，测试易漏）。
- **路由只支持整段变量**（`{var}` 必须独占一段）；`/{page}.json` 后缀模式和裸 `/**` catch-all 均不支持——因此两个前端 SPA 都用 HashRouter。
- 原生请求/响应：`WebRequest`/`WebResponse`（cn.jiebaba.summer.web.http），无 HttpServletRequest；请求头注入用 `@RequestHeader(value="X-Mall-Token", required=false)`。
- 分页：`selectPage(new Page<>(current, size), wrapper)`，current 从 1 起；服务层沿用 start/length 换算（PageParams.normalize）。
- Bean 校验失败 → 400 + `violations` 数组（框架默认格式，前端契约）。
- 有 @Transactional/@Aspect 的类不能是 final（代理要求）。
- **浏览器 fetch：GET/HEAD 请求不能带 body**——前端统一请求封装按 method 过滤，别给 GET 加 json body。

## 异常处理体系

`web/exception/GlobalExceptionHandler`（@RestControllerAdvice）+ 复用的 `RestError` POJO：
- Bean 校验失败：400 + violations
- messageCode 以 `Error.` 开头：状态码查 `resources/exception/exceptionStatusMapping.properties`（按异常类继承深度匹配），文案查 `resources/messages/exception(_zh_CN).properties`
- 商城业务异常按**类名**映射状态码：`com.rocket.myshop.exception.MallAuthException=401`、`MallNotFoundException=404`（message 原样透传给前端），无需改消息包
- 其余：500 兜底
新增业务异常映射时改 exceptionStatusMapping.properties，无需动代码。

## 前端工程（两个，均独立 npm 工程，不参与 Maven 构建）

### myshop-manager-ui（后台管理，React 18 + Vite 5 + Ant Design 5）

```bash
cd myshop-manager-ui
npm install          # 首次
npm run dev          # 端口 5173，base=/admin/，/items /users /listDemo 代理到后端 8080
npm run build        # 仅构建到 dist/
npm run sync         # 构建 + 把 dist 同步到 myshop-manager-web/src/main/resources/static/admin
```

- 路由 HashRouter（`/#/items`）。API 走绝对路径（fetch(`/items/...`)），子路径部署不受影响。
- 页面：工作台 /、商品列表 /items（增改删）、添加商品 /items/add、商品类别 /categories（树形懒展开）、品牌 /brands、规格设置 /specs（占位）。

### myshop-mall-ui（商城，React 18 + Vite 5，无组件库）

```bash
cd myshop-mall-ui
npm install && npm run dev    # 端口 5174，base=/mall/，/mall 代理到后端 8080
npm run sync                  # 构建 + dist 同步到 static/mall
```

- 页面：首页 /、全部商品 /items（搜索/类别/品牌过滤+分页）、详情 /item/:id、购物车 /cart、结算 /checkout、订单 /orders、订单详情 /order/:id、登录 /login、注册 /register。
- 登录态：token 存 localStorage（key `mall_token`），请求统一带 `X-Mall-Token` 头；401 自动清 token 跳登录。
- 商品图在 `public/images/`（源自 ui-front 模板素材，已按联络表鉴定），构建后同步到 `static/mall/images/`；DB `img_logo_` 存 `/mall/images/<文件名>`。

### 一体化部署

- `npm run sync` 后重新 `mvn -pl myshop-manager/myshop-manager-web package -DskipTests`（**sync 只改源目录，必须重新打包才进 jar**）。
- 三个入口：`/`（手写门户 `static/index.html`，gitignore 例外保留）、`/admin/`（后台）、`/mall/`（商城）。
- **两个 sync 脚本只清各自子目录**，绝不清空 static 根（会互删/删门户页）。

## 数据库

- 建表脚本：`项目文档/myshop-manager.sql`（MySQL，InnoDB/utf8，列名尾下划线；含 mall 4 表）
- 后台表：shop_item / shop_item_brand / shop_item_category / shop_menu / shop_user / demo
- 商城表：**mall_user**（会员，SHA-256 加盐）/ **mall_cart**（user+item 唯一）/ **mall_order**（状态 1待付款 2已付款 3已取消，收货快照）/ **mall_order_item**（标题/图/单价/数量下单快照）
- 冒烟测试用 H2 Regular 模式内存库（测试内联 `initH2Schema`；**改表结构三处同步**：myshop-manager.sql、dev/h2-init.sql、测试 initH2Schema）
- 本地无 MySQL 联调：`dev/h2-init.sql`（H2 MySQL 模式；mall 表只建表不删数据——每新连接都重跑脚本）
- 种子：`dev/mall-seed.sql`（幂等建表+图文匹配的 22 商品/9 类别/5 品牌，WSL 导入命令见 docs）

## 本地一键验证（无 MySQL）

```bash
cd myshop-manager/myshop-manager-web && mvn exec:java -Dexec.mainClass=com.rocket.myshop.ManagerApplication \
  -Dexec.classpathScope=test \
  "-Dsummer.datasource.url=jdbc:h2:mem:myshop;MODE=MySQL;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'file:E:/myshop_workspace/myshop-backend/dev/h2-init.sql'" \
  -Dsummer.datasource.driver-class-name=org.h2.Driver -Dsummer.datasource.username=sa "-Dsummer.datasource.password="
```

## 本机 MySQL（默认数据源）

- WSL Docker 部署：容器 `mysql8`（8.0.46），`localhost:3306`，root/root123456，库 `myshop`。备份快照在 `dev/mysql8-*-backup-20261015.sql`。
- `application.yml` 默认即此连接；覆盖用松弛映射环境变量。
- Git Bash 注意：`wsl docker exec -i mysql8 mysql ... < x.sql` 导入；`wsl docker run -v` 路径转换要加 `MSYS_NO_PATHCONV=1`。

## 已知问题与历史遗留

1. `myshop-user` 是空壳占位（用户明确要求保留），无任何 Java 代码；商城后端代码放 myshop-manager-web 的 mall 包（用户已确认）。
2. 原 JSP/webapp 目录已删除；`ui-front`（静态商城模板、图源）、`ui-bak`、`android-client`、`weixin-client` 为历史遗留保留。
3. `myshop-parent` 目录为历史遗留，未清理。
4. ItemController 等管理接口的 `@RequestParam` 一律查询串传参（Summer 不读表单体）。
5. git remote 平时指向 gh-proxy.com 镜像（本机直连 GitHub 超时），push 需临时改回 `https://github.com/chinahub/myshop.git`。
6. 历史分支 myshop-1.0.0 ~ 2.5.0 是旧架构演进记录；旧架构代码在 master 的 git 历史（迁移前）。
7. **Summer 框架已在本机源码（E:\summer_workspace）修了 3 个 bug 并重装 4.1.0**（未推上游）：
   - `Json.isNumeric` 不含 BigDecimal/BigInteger → DECIMAL 绑定失败（summer-core Json.java）
   - 数值列转 Boolean 走 `Boolean.valueOf("1")`=false → TINYINT 状态字段映射错误（summer-boot SqlExecutor.java）
   - 自增主键 generated key 是 BigInteger → 回填 Integer 不匹配（summer-boot TableFieldInfo.setValue 增加数字窄化）
   升级 Summer 版本时若远端未包含这三个修复，需要重新打补丁。
8. 商城 token 会话存内存（重启失效），密码 SHA-256 加盐；均为有意保留的玩具级实现（二期可持久化/升级 BCrypt）。

## 项目文档

`docs/`：README（索引）、架构与结构、快速开始、接口清单、数据库设计、GUI验收报告。`项目文档/`：myshop-manager.sql（建表脚本）+ myshop需求说明.doc（原始需求）。
