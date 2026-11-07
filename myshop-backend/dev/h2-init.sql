-- 本地无 MySQL 时的 H2 验证脚本（java -jar 时通过 JDBC URL 的 INIT=RUNSCRIPT 加载）。
-- 列名/类型与 项目文档/myshop-manager.sql 保持一致；仅用于本地联调，生产请使用 MySQL。
CREATE TABLE IF NOT EXISTS shop_item_category (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  name_ VARCHAR(40) NOT NULL,
  status_ TINYINT(1) NOT NULL DEFAULT 1,
  create_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  parent_id_ INT DEFAULT NULL,
  items_num_ INT DEFAULT NULL,
  rank_ SMALLINT DEFAULT NULL,
  level_ TINYINT(2) DEFAULT 1,
  sub_category_num_ SMALLINT DEFAULT 0
);
CREATE TABLE IF NOT EXISTS shop_item_brand (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  name_ VARCHAR(40) NOT NULL,
  logo_ VARCHAR(500) DEFAULT NULL,
  status_ TINYINT(1) NOT NULL DEFAULT 1,
  create_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time_ DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS shop_item (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  title_ VARCHAR(100) NOT NULL,
  sell_point_ VARCHAR(500) DEFAULT NULL,
  price_ DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  num_ INT NOT NULL DEFAULT 0,
  barcode_ VARCHAR(40) DEFAULT NULL,
  img_logo_ VARCHAR(500) DEFAULT NULL,
  status_ CHAR(1) NOT NULL DEFAULT '1',
  create_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  seller_id_ VARCHAR(40) DEFAULT NULL,
  item_category_name_ VARCHAR(40) DEFAULT NULL,
  item_brand_name_ VARCHAR(40) DEFAULT NULL,
  sale_clients_ VARCHAR(100) DEFAULT NULL
);
CREATE TABLE IF NOT EXISTS shop_menu (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  name_ VARCHAR(40) NOT NULL,
  code_ VARCHAR(40) NOT NULL,
  parent_id_ INT DEFAULT NULL,
  url_ VARCHAR(200) DEFAULT NULL,
  status_ TINYINT(1) NOT NULL DEFAULT 1,
  create_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time_ DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS shop_user (
  userid_ VARCHAR(40) NOT NULL PRIMARY KEY,
  username_ VARCHAR(40) NOT NULL,
  password_ VARCHAR(100) NOT NULL,
  op_date_ DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS demo (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  name_ VARCHAR(40) DEFAULT NULL
);
CREATE TABLE IF NOT EXISTS mall_user (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  username_ VARCHAR(40) NOT NULL,
  password_ VARCHAR(64) NOT NULL,
  salt_ VARCHAR(32) NOT NULL,
  nickname_ VARCHAR(40) DEFAULT NULL,
  status_ TINYINT(1) NOT NULL DEFAULT 1,
  create_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time_ DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS mall_cart (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  user_id_ INT NOT NULL,
  item_id_ INT NOT NULL,
  num_ INT NOT NULL DEFAULT 1,
  create_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time_ DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS mall_order (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  order_no_ VARCHAR(40) NOT NULL,
  user_id_ INT NOT NULL,
  total_amount_ DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  status_ TINYINT(1) NOT NULL DEFAULT 1,
  consignee_name_ VARCHAR(40) DEFAULT NULL,
  consignee_phone_ VARCHAR(20) DEFAULT NULL,
  consignee_address_ VARCHAR(200) DEFAULT NULL,
  create_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  pay_time_ DATETIME DEFAULT NULL
);
CREATE TABLE IF NOT EXISTS mall_order_item (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  order_id_ INT NOT NULL,
  item_id_ INT NOT NULL,
  item_title_ VARCHAR(100) NOT NULL,
  item_img_ VARCHAR(500) DEFAULT NULL,
  item_price_ DECIMAL(10,2) NOT NULL,
  num_ INT NOT NULL,
  subtotal_ DECIMAL(10,2) NOT NULL
);
CREATE TABLE IF NOT EXISTS sys_dict (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  dict_type_ VARCHAR(50) NOT NULL,
  dict_name_ VARCHAR(50) NOT NULL,
  remark_ VARCHAR(200) DEFAULT NULL,
  status_ TINYINT NOT NULL DEFAULT 1,
  create_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (dict_type_)
);
CREATE TABLE IF NOT EXISTS sys_dict_data (
  id_ INT AUTO_INCREMENT PRIMARY KEY,
  dict_type_ VARCHAR(50) NOT NULL,
  value_ VARCHAR(50) NOT NULL,
  label_ VARCHAR(100) NOT NULL,
  sort_ INT NOT NULL DEFAULT 0,
  extra_ VARCHAR(200) DEFAULT NULL,
  status_ TINYINT NOT NULL DEFAULT 1,
  create_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time_ DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (dict_type_, value_)
);
-- mall_* 是商城运行时数据（会员/购物车/订单），不是种子表：
-- 本脚本在每个新连接上重跑，故只建表不删数，避免会话中途数据被清空。
-- sys_dict* 与 demo/shop_* 同为种子数据（字典是配置非用户数据），先删后插保证幂等，
-- 本地 H2 模式下通过字典管理页新增的条目也会在换连接后重置，联调请知悉。
-- 连接池每个新连接都会执行一次本脚本（INIT=RUNSCRIPT），种子数据先删后插保证幂等。
DELETE FROM demo;
DELETE FROM shop_item;
DELETE FROM shop_item_brand;
DELETE FROM shop_item_category;
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
INSERT INTO demo (name_) VALUES ('演示数据一'), ('演示数据二');
INSERT INTO shop_item (title_, price_, num_, status_, item_category_name_, item_brand_name_) VALUES
  ('本地验证商品A', 99.00, 10, '1', '测试类别', '测试品牌'),
  ('本地验证商品B', 199.00, 5, '1', '测试类别', '测试品牌');
INSERT INTO shop_item_brand (name_, status_) VALUES ('测试品牌', 1), ('备用品牌', 1);
INSERT INTO shop_item_category (name_, status_, level_, sub_category_num_) VALUES ('测试类别', 1, 1, 0), ('测试子类别', 1, 2, 0);
UPDATE shop_item_category SET parent_id_ = (SELECT MIN(id_) FROM shop_item_category WHERE level_ = 1) WHERE name_ = '测试子类别';
