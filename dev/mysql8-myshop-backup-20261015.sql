-- MySQL dump 10.13  Distrib 8.0.46, for Linux (x86_64)
--
-- Host: localhost    Database: myshop
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `myshop`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `myshop` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `myshop`;

--
-- Table structure for table `demo`
--

DROP TABLE IF EXISTS `demo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `demo` (
  `id_` int NOT NULL AUTO_INCREMENT,
  `name_` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='示例表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `demo`
--

LOCK TABLES `demo` WRITE;
/*!40000 ALTER TABLE `demo` DISABLE KEYS */;
/*!40000 ALTER TABLE `demo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_item`
--

DROP TABLE IF EXISTS `shop_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_item` (
  `id_` int NOT NULL AUTO_INCREMENT,
  `title_` varchar(100) NOT NULL COMMENT '商品标题',
  `sell_point_` varchar(500) DEFAULT NULL COMMENT '商品卖点',
  `price_` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品价格',
  `num_` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `barcode_` varchar(40) DEFAULT NULL COMMENT '商品条形码',
  `img_logo_` varchar(500) DEFAULT NULL COMMENT '商品主图地址',
  `status_` char(1) NOT NULL DEFAULT '1' COMMENT '商品状态，1正常 0下架',
  `create_time_` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `seller_id_` varchar(40) DEFAULT NULL COMMENT '商家ID',
  `item_category_name_` varchar(40) DEFAULT NULL COMMENT '商品类别名称',
  `item_brand_name_` varchar(40) DEFAULT NULL COMMENT '商品品牌名称',
  `sale_clients_` varchar(100) DEFAULT NULL COMMENT '销售渠道',
  PRIMARY KEY (`id_`),
  KEY `idx_shop_item_category_name` (`item_category_name_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='商品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_item`
--

LOCK TABLES `shop_item` WRITE;
/*!40000 ALTER TABLE `shop_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_item_brand`
--

DROP TABLE IF EXISTS `shop_item_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_item_brand` (
  `id_` int NOT NULL AUTO_INCREMENT,
  `name_` varchar(40) NOT NULL COMMENT '品牌名称',
  `logo_` varchar(500) DEFAULT NULL COMMENT '品牌LOGO地址',
  `status_` tinyint(1) NOT NULL DEFAULT '1' COMMENT '品牌状态，1正常 0删除',
  `create_time_` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='商品品牌';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_item_brand`
--

LOCK TABLES `shop_item_brand` WRITE;
/*!40000 ALTER TABLE `shop_item_brand` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_item_brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_item_category`
--

DROP TABLE IF EXISTS `shop_item_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_item_category` (
  `id_` int NOT NULL AUTO_INCREMENT,
  `name_` varchar(40) NOT NULL,
  `status_` tinyint(1) NOT NULL DEFAULT '1' COMMENT '类别状态， 1正常 0删除',
  `create_time_` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `parent_id_` int DEFAULT NULL COMMENT '父类目ID=null时，代表的是一级的类目',
  `items_num_` int DEFAULT NULL COMMENT '该类别下面的商品数目',
  `rank_` smallint DEFAULT NULL COMMENT '排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数',
  `level_` tinyint DEFAULT '1' COMMENT '商品类别等级，默认为1级类别',
  `sub_category_num_` smallint DEFAULT '0' COMMENT '子类别数目',
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='商品类目';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_item_category`
--

LOCK TABLES `shop_item_category` WRITE;
/*!40000 ALTER TABLE `shop_item_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_item_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_menu`
--

DROP TABLE IF EXISTS `shop_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_menu` (
  `id_` int NOT NULL AUTO_INCREMENT,
  `name_` varchar(40) NOT NULL COMMENT '菜单名称',
  `code_` varchar(40) NOT NULL COMMENT '菜单编码',
  `parent_id_` int DEFAULT NULL COMMENT '父菜单ID，为null时代表一级菜单',
  `url_` varchar(200) DEFAULT NULL COMMENT '菜单链接',
  `status_` tinyint(1) NOT NULL DEFAULT '1' COMMENT '菜单状态，1正常 0删除',
  `create_time_` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time_` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='后台菜单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_menu`
--

LOCK TABLES `shop_menu` WRITE;
/*!40000 ALTER TABLE `shop_menu` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_user`
--

DROP TABLE IF EXISTS `shop_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_user` (
  `userid_` varchar(40) NOT NULL COMMENT '用户ID',
  `username_` varchar(40) NOT NULL COMMENT '用户名',
  `password_` varchar(100) NOT NULL COMMENT '密码（密文）',
  `op_date_` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`userid_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='后台用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_user`
--

LOCK TABLES `shop_user` WRITE;
/*!40000 ALTER TABLE `shop_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-15 22:44:23
