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