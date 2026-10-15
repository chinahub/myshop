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
-- 连接池每个新连接都会执行一次本脚本（INIT=RUNSCRIPT），种子数据先删后插保证幂等。
DELETE FROM demo;
DELETE FROM shop_item;
DELETE FROM shop_item_brand;
DELETE FROM shop_item_category;
INSERT INTO demo (name_) VALUES ('演示数据一'), ('演示数据二');
INSERT INTO shop_item (title_, price_, num_, status_, item_category_name_, item_brand_name_) VALUES
  ('本地验证商品A', 99.00, 10, '1', '测试类别', '测试品牌'),
  ('本地验证商品B', 199.00, 5, '1', '测试类别', '测试品牌');
INSERT INTO shop_item_brand (name_, status_) VALUES ('测试品牌', 1), ('备用品牌', 1);
INSERT INTO shop_item_category (name_, status_, level_, sub_category_num_) VALUES ('测试类别', 1, 1, 0), ('测试子类别', 1, 2, 0);
UPDATE shop_item_category SET parent_id_ = (SELECT MIN(id_) FROM shop_item_category WHERE level_ = 1) WHERE name_ = '测试子类别';
