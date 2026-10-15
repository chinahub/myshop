#DROP TABLE shop_item_category;
CREATE TABLE `shop_item_category` (
  `id_` int(11) NOT NULL AUTO_INCREMENT,
  `name_` varchar(40) NOT NULL,
  `status_` tinyint(1) NOT NULL DEFAULT '1' COMMENT '类别状态， 1正常 0删除',
  `create_time_` datetime DEFAULT  CURRENT_TIMESTAMP() COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
  `parent_id_` int(11) DEFAULT NULL COMMENT '父类目ID=null时，代表的是一级的类目',
  `items_num_` int(11) DEFAULT NULL COMMENT '该类别下面的商品数目',
  `rank_` smallint(6) DEFAULT NULL COMMENT '排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数',
  `level_` tinyint(2) DEFAULT 1 COMMENT '商品类别等级，默认为1级类别',
  `sub_category_num_` smallint(6) DEFAULT 0 COMMENT '子类别数目',
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品类目';

#DROP TABLE shop_item_brand;
CREATE TABLE `shop_item_brand` (
  `id_` int(11) NOT NULL AUTO_INCREMENT,
  `name_` varchar(40) NOT NULL COMMENT '品牌名称',
  `logo_` varchar(500) DEFAULT NULL COMMENT '品牌LOGO地址',
  `status_` tinyint(1) NOT NULL DEFAULT '1' COMMENT '品牌状态，1正常 0删除',
  `create_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品品牌';

#DROP TABLE shop_item;
CREATE TABLE `shop_item` (
  `id_` int(11) NOT NULL AUTO_INCREMENT,
  `title_` varchar(100) NOT NULL COMMENT '商品标题',
  `sell_point_` varchar(500) DEFAULT NULL COMMENT '商品卖点',
  `price_` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品价格',
  `num_` int(11) NOT NULL DEFAULT '0' COMMENT '库存数量',
  `barcode_` varchar(40) DEFAULT NULL COMMENT '商品条形码',
  `img_logo_` varchar(500) DEFAULT NULL COMMENT '商品主图地址',
  `status_` char(1) NOT NULL DEFAULT '1' COMMENT '商品状态，1正常 0下架',
  `create_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
  `seller_id_` varchar(40) DEFAULT NULL COMMENT '商家ID',
  `item_category_name_` varchar(40) DEFAULT NULL COMMENT '商品类别名称',
  `item_brand_name_` varchar(40) DEFAULT NULL COMMENT '商品品牌名称',
  `sale_clients_` varchar(100) DEFAULT NULL COMMENT '销售渠道',
  PRIMARY KEY (`id_`),
  KEY `idx_shop_item_category_name` (`item_category_name_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品';

#DROP TABLE shop_menu;
CREATE TABLE `shop_menu` (
  `id_` int(11) NOT NULL AUTO_INCREMENT,
  `name_` varchar(40) NOT NULL COMMENT '菜单名称',
  `code_` varchar(40) NOT NULL COMMENT '菜单编码',
  `parent_id_` int(11) DEFAULT NULL COMMENT '父菜单ID，为null时代表一级菜单',
  `url_` varchar(200) DEFAULT NULL COMMENT '菜单链接',
  `status_` tinyint(1) NOT NULL DEFAULT '1' COMMENT '菜单状态，1正常 0删除',
  `create_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='后台菜单';

#DROP TABLE shop_user;
CREATE TABLE `shop_user` (
  `userid_` varchar(40) NOT NULL COMMENT '用户ID',
  `username_` varchar(40) NOT NULL COMMENT '用户名',
  `password_` varchar(100) NOT NULL COMMENT '密码（密文）',
  `op_date_` datetime DEFAULT CURRENT_TIMESTAMP() COMMENT '操作时间',
  PRIMARY KEY (`userid_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='后台用户';

#DROP TABLE demo;
CREATE TABLE `demo` (
  `id_` int(11) NOT NULL AUTO_INCREMENT,
  `name_` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='示例表';

#==============================================================================
# 商城（myshop-mall-ui 用户端）相关表，2026-10 新增
#==============================================================================

#DROP TABLE mall_user;
CREATE TABLE `mall_user` (
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

#DROP TABLE mall_cart;
CREATE TABLE `mall_cart` (
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

#DROP TABLE mall_order;
CREATE TABLE `mall_order` (
  `id_` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no_` varchar(40) NOT NULL COMMENT '订单号（MO+时间戳+随机4位）',
  `user_id_` int(11) NOT NULL COMMENT '会员ID',
  `total_amount_` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总额',
  `status_` tinyint NOT NULL DEFAULT '1' COMMENT '订单状态，1待付款 2已付款 3已取消',
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

#DROP TABLE mall_order_item;
CREATE TABLE `mall_order_item` (
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