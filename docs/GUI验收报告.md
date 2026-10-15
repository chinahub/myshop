# GUI 验收报告（2026-10-16）

商城（myshop-mall-ui）与一体部署上线前的浏览器黑盒验收。方法：真实浏览器模拟用户操作（点击/输入/截图），不注入脚本、不构造 URL 绕过交互。每步均有 DOM 结构 + 截图双证据。

## 结论：验收通过（8 个测试点，发现并修复 2 个 bug 后全绿）

## 环境准备与测试账号

- 启动 `myshop-manager-boot.jar`（8080），导入 `dev/mall-seed.sql` 种子数据（22 商品）。
- 测试用户新注册 `guiuser`（本次会话通过 GUI 创建），未预先注入任何登录态或订单。

## 测试点明细

| # | 测试点 | 结果 | 证据 |
| --- | --- | --- | --- |
| T1 | 门户页 `/`：两个入口卡片、视觉 | ✅ | screenshot portal |
| T2 | 商城首页 `/mall/`：hero + 22 商品网格（图片/价格/类别） | ✅（修复 bug#1 后） | screenshot home |
| T3 | 未登录点「加入购物车」→ 自动跳登录页 `#/login` | ✅ | screenshot login |
| T4 | 注册（guiuser/123456）→ 自动登录、顶栏显示昵称 | ✅ | DOM 证据 |
| T5 | 加购 2 件 + 购物车改数量 3 → 小计 ¥897 合计 ¥897 | ✅ | screenshot cart |
| T6 | 结算（收货人/电话/地址）→ 提交订单 → 待付款 → 模拟支付 → 已付款 + 付款时间落库 + 操作按钮消失 | ✅（修复 bug#2 后） | screenshot order |
| T7 | 后台 `/admin/` 工作台（22/6/9 统计）与商品列表加载 | ✅ | screenshot admin |
| T8 | 订单列表：订单号/商品快照/金额/状态徽标/详情 | ✅ | screenshot orders |
| T9 | 搜索「深空灰」过滤 + 类别/品牌下拉展示 | ✅（一刀切整串 like 行为见备注） | DOM 证据 |

## 发现的问题（均已修复）

### bug#1（前端）GET 请求携带 body 导致商城页面接口全挂
- 首页所有商品接口报 `Failed to execute 'fetch': Request with GET/HEAD method cannot have body`。
- 原因：`src/api/client.js` 对 GET 也执行 `JSON.stringify(json)` 充当 body；curl 可以复现 API 层但浏览器 fetch 规范直接拒绝，因此 curl 冒烟未暴露。
- 修复：client.js 仅对非 GET/HEAD 请求序列化 body 并设置 Content-Type。

### bug#2（数据库）mall_order.status_ 用 TINYINT(1) 导致订单读取失败
- 订单创建成功但详情报 `Can not set Integer field MallOrder.status to Boolean`。
- 原因：MySQL Connector/J 对 `TINYINT(1)` 列默认返回 Boolean（tinyInt1isBit=true），Integer 字段赋值失败；H2 测试库无此转换，冒烟测试未暴露。
- 修复：`mall_order.status_` 全处改为 `TINYINT`（建表脚本、种子脚本、线上 ALTER）；`mall_user.status_` 为 Boolean 字段保留 tinyint(1) 不动。
- 教训：Integer 字段的 tinyint 列禁用 tinyint(1)，已写入 docs/快速开始.md 与 AGENTS.md。

### 内容质量：种子商品图文不匹配
- 初版种子按「数码/家电」起名，但素材池实为腕表/手机/相机/耳机（联络表逐张核对：5 张手机、3 张相机、2 张平板、2 张耳机、9 张腕表）。
- 修复：重写 `dev/mall-seed.sql` 为「精品腕表 + 手机数码」体系（22 商品图文一一对应，9 类别 5 品牌），重导 MySQL 并复测首页。

## 备注

- 搜索为标题整串 `LIKE %kw%`，多词空格即视为一个短语（演示可接受）。
- 测试过程中重导种子会清空 mall_order/mall_cart（脚本设计如此，演示环境保持初始态）；「guiuser」账号保留。
- 该轮 GUI 验收无 console 错误收集能力，以页面可见错误为准；未见空白/占位错误。
