-- 系统字典种子数据（幂等：先删后插；字典属配置数据，重跑即恢复出厂枚举）。
-- 适用本地 MySQL：wsl docker exec -i mysql8 mysql -uroot -proot123456 --default-character-set=utf8 myshop < dev/sys-dict-seed.sql
-- 建表语句在 项目文档/myshop-manager.sql；H2 对应 dev/h2-init.sql 与冒烟测试 initH2Schema（改表结构三处同步）。
-- extra_ 为 JSON 扩展字段：color 供后台 antd Tag 用；cls 供商城 mall-ui 状态样式类用。

DELETE FROM sys_dict_data;
DELETE FROM sys_dict;

INSERT INTO sys_dict (dict_type_, dict_name_, remark_, status_) VALUES
  ('item_status', '商品状态', 'shop_item.status_：1在售 0下架', 1),
  ('brand_status', '品牌状态', 'shop_item_brand.status_：1正常 0删除', 1),
  ('order_status', '订单状态', 'mall_order.status_：1待付款 2已付款 3已取消', 1);

INSERT INTO sys_dict_data (dict_type_, value_, label_, sort_, extra_, status_) VALUES
  ('item_status',  '1', '在售',   1, '{"color":"green"}',  1),
  ('item_status',  '0', '下架',   2, '{"color":"red"}',    1),
  ('brand_status', '1', '正常',   1, '{"color":"green"}',  1),
  ('brand_status', '0', '删除',   2, '{"color":"red"}',    1),
  ('order_status', '1', '待付款', 1, '{"color":"orange","cls":"st-unpaid"}',    1),
  ('order_status', '2', '已付款', 2, '{"color":"green","cls":"st-paid"}',       1),
  ('order_status', '3', '已取消', 3, '{"color":"red","cls":"st-cancelled"}',    1);
