-- 商城演示种子数据（幂等：先删后插）。
-- 适用本地 MySQL：docker exec -i mysql8 mysql -uroot -proot123456 --default-character-set=utf8 myshop < dev/mall-seed.sql
-- 商品图沿用 ui-front 静态模板素材（经联络表逐张核对归类，见 docs/GUI验收报告），放入 myshop-mall-ui/public/images。
-- mall_user 保留不清，避免演示账号反复注册。

CREATE TABLE IF NOT EXISTS `mall_user` (
  `id_` int(11) NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `username_` varchar(40) NOT NULL COMMENT '用户名',
  `password_` varchar(64) NOT NULL COMMENT '密码哈希（SHA-256(salt+password) 十六进制）',
  `salt_` varchar(32) NOT NULL COMMENT '盐（32位随机 hex）',
  `nickname_` varchar(40) DEFAULT NULL COMMENT '昵称，默认同用户名',
  `status_` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态，1正常 0禁用',
  `create_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
  PRIMARY KEY (`id_`),
  UNIQUE KEY `uk_mall_user_username` (`username_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商城会员';

CREATE TABLE IF NOT EXISTS `mall_cart` (
  `id_` int(11) NOT NULL AUTO_INCREMENT COMMENT '购物车条目ID',
  `user_id_` int(11) NOT NULL COMMENT '会员ID',
  `item_id_` int(11) NOT NULL COMMENT '商品ID',
  `num_` int(11) NOT NULL DEFAULT '1' COMMENT '数量',
  `create_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
  PRIMARY KEY (`id_`),
  UNIQUE KEY `uk_mall_cart_user_item` (`user_id_`,`item_id_`),
  KEY `idx_mall_cart_user` (`user_id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='购物车';

CREATE TABLE IF NOT EXISTS `mall_order` (
  `id_` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no_` varchar(40) NOT NULL COMMENT '订单号（MO+时间戳+随机4位）',
  `user_id_` int(11) NOT NULL COMMENT '会员ID',
  `total_amount_` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总额',
  `status_` tinyint NOT NULL DEFAULT '1' COMMENT '订单状态，1待付款 2已付款 3已取消（tinyInt1isBit 陷阱：Integer 字段禁用 tinyint(1)）',
  `consignee_name_` varchar(40) DEFAULT NULL COMMENT '收货人姓名',
  `consignee_phone_` varchar(20) DEFAULT NULL COMMENT '收货人电话',
  `consignee_address_` varchar(200) DEFAULT NULL COMMENT '收货地址',
  `create_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
  `pay_time_` datetime DEFAULT NULL COMMENT '付款时间（模拟支付）',
  PRIMARY KEY (`id_`),
  UNIQUE KEY `uk_mall_order_no` (`order_no_`),
  KEY `idx_mall_order_user` (`user_id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单';

CREATE TABLE IF NOT EXISTS `mall_order_item` (
  `id_` int(11) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id_` int(11) NOT NULL COMMENT '订单ID',
  `item_id_` int(11) NOT NULL COMMENT '商品ID',
  `item_title_` varchar(100) NOT NULL COMMENT '商品标题快照',
  `item_img_` varchar(500) DEFAULT NULL COMMENT '商品主图快照',
  `item_price_` decimal(10,2) NOT NULL COMMENT '成交单价快照',
  `num_` int(11) NOT NULL COMMENT '数量',
  `subtotal_` decimal(10,2) NOT NULL COMMENT '小计',
  PRIMARY KEY (`id_`),
  KEY `idx_mall_order_item_order` (`order_id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单明细';

DELETE FROM mall_order_item;
DELETE FROM mall_order;
DELETE FROM mall_cart;
DELETE FROM shop_item;
DELETE FROM shop_item_brand;
DELETE FROM shop_item_category;

-- 类别树（一级 level=1，二级 level=2；与下方商品的 item_category_name_ 对应）
INSERT INTO shop_item_category (id_, name_, status_, parent_id_, level_, sub_category_num_) VALUES
  (1, '腕表精品', 1, NULL, 1, 2),
  (2, '经典机械腕表', 1, 1, 2, 0),
  (3, '经典石英腕表', 1, 1, 2, 0),
  (4, '手机数码', 1, NULL, 1, 2),
  (5, '智能手机', 1, 4, 2, 0),
  (6, '影音器材', 1, 4, 2, 0),
  (7, '电脑平板', 1, NULL, 1, 2),
  (8, '平板电脑', 1, 7, 2, 0),
  (9, '摄影配件', 1, 7, 2, 0);

-- 品牌
INSERT INTO shop_item_brand (id_, name_, status_) VALUES
  (1, '时莱', 1),
  (2, '云驰', 1),
  (3, '星影', 1),
  (4, '极光', 1),
  (5, '悦响', 1);

-- 22 个上架商品：图片 -> /mall/images/<文件名>（前端 public/images 同名文件，图文已经联络表核对）
INSERT INTO shop_item (title_, sell_point_, price_, num_, status_, item_category_name_, item_brand_name_, img_logo_) VALUES
  ('时莱 T-Classic 白盘黑带机械腕表', '罗马字白盘，黑牛皮带，复古通勤', 1980.00, 12, '1', '经典机械腕表', '时莱', '/mall/images/ITEM_13470_1451386627677_1.jpg'),
  ('时莱 T-Sport 钢带三眼运动表', '精钢表带大表盘，夜光计时指针', 2450.00, 9, '1', '经典机械腕表', '时莱', '/mall/images/ITEM_13772_1451386576157_1.jpg'),
  ('时莱 Chrono 三眼计时 棕皮带', '白盘三眼计时，复古棕色皮带', 1680.00, 15, '1', '经典石英腕表', '时莱', '/mall/images/ITEM_13781_1442909134561_1.jpg'),
  ('时莱 T-Lady 玫瑰金白盘腕表', '玫瑰金表壳，小盘日历，女士通勤', 2880.00, 7, '1', '经典机械腕表', '时莱', '/mall/images/ITEM_14356_1451386616146_1.jpg'),
  ('时莱 Chrono 商务三眼 深棕带', '玫瑰金壳三眼计时，商务仪式感', 2180.00, 8, '1', '经典石英腕表', '时莱', '/mall/images/ITEM_14357_1443506727760_1.jpg'),
  ('时莱 Nova 格纹金壳 红织带女表', '格纹表盘，英伦红织带', 1230.00, 11, '1', '经典石英腕表', '时莱', '/mall/images/ITEM_14693_1444290329929_1.jpg'),
  ('时莱 Nova 格纹 米皮带女表', '格纹表盘，米色皮带，温柔气质', 1160.00, 13, '1', '经典石英腕表', '时莱', '/mall/images/ITEM_14695_1451548265213_1.jpg'),
  ('时莱 Classic 白盘三色织带中性表', '极简三针，NATO 织带', 980.00, 20, '1', '经典石英腕表', '时莱', '/mall/images/ITEM_14764_1451543730289_1.jpg'),
  ('云驰 C6 旗舰手机 32G 深空灰', '4.7英寸双色温闪光灯，金属机身', 2199.00, 25, '1', '智能手机', '云驰', '/mall/images/ITEM_14222_1443182409766_1.jpg'),
  ('云驰 C6 旗舰手机 32G 金色', '4.7英寸，Live Photo 动态拍摄', 2199.00, 22, '1', '智能手机', '云驰', '/mall/images/ITEM_14223_1456998242916_1.jpg'),
  ('云驰 C6 旗舰手机 64G 玫瑰金', '64G 大存储，摄像头光学防抖', 2499.00, 18, '1', '智能手机', '云驰', '/mall/images/ITEM_14225_1456998320712_1.jpg'),
  ('云驰 C6 旗舰手机 32G 银色', '3D Touch 压感屏，银色经典', 2199.00, 16, '1', '智能手机', '云驰', '/mall/images/ITEM_14230_1443182251848_1.jpg'),
  ('云驰 C6 套装版 玫瑰金 64G', '全套含保护壳与钢化膜', 2699.00, 10, '1', '智能手机', '云驰', '/mall/images/ITEM_14231_1456998346293_1.jpg'),
  ('云驰 R7 轻薄手机 玫瑰金', '5.5英寸双曲面，闪充充电5分钟通话2小时', 1799.00, 30, '1', '智能手机', '云驰', '/mall/images/ITEM_11807_1448616221122_1.jpg'),
  ('云驰 X5 大屏手机 玉白', '6.0英寸巨屏，双卡长续航', 999.00, 40, '1', '智能手机', '云驰', '/mall/images/ITEM_12042_1441174812822_1.jpg'),
  ('星影 Touch 播放器 6代 深空灰', '4英寸触控屏，随身音乐游戏', 1080.00, 24, '1', '影音器材', '星影', '/mall/images/ITEM_11336_1440405084922_1.jpg'),
  ('悦响 urBass 入耳式耳机 金', '三键线控，低音饱满', 388.00, 200, '1', '影音器材', '悦响', '/mall/images/ITEM_13966_1443061224611_1.jpg'),
  ('悦响 SoloB 头戴式耳机 黑', '封闭式声场，轻量头梁', 599.00, 60, '1', '影音器材', '悦响', '/mall/images/ITEM_13999_1443067063493_1.jpg'),
  ('星影 Pad Air 7.9英寸平板 银色', '7.9英寸视网膜屏，指纹解锁', 1588.00, 18, '1', '平板电脑', '星影', '/mall/images/ITEM_666_1425957459826_1.jpg'),
  ('星影 Pad mini 平板 白色 64G', 'A8 芯片，同屏办公', 1299.00, 14, '1', '平板电脑', '星影', '/mall/images/ITEM_13812_1442915504319_1.jpg'),
  ('极光 FR10 微型自拍相机 白色', '指尖可分离微单，WiFi直传', 1699.00, 12, '1', '摄影配件', '极光', '/mall/images/ITEM_13063_1442375522030_1.jpg'),
  ('极光 FR10 微型自拍相机 金色', '指尖可分离微单，时尚金色', 1799.00, 9, '1', '摄影配件', '极光', '/mall/images/ITEM_13064_1442375445404_1.jpg');
