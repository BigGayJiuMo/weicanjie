-- MySQL dump 10.13  Distrib 26.7.0, for Win64 (x86_64)
--
-- Host: localhost    Database: weicanjie_db
-- ------------------------------------------------------
-- Server version	26.7.0

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'ade5a445-a052-11f1-ad18-80fa5b722740:1-79';

--
-- Current Database: `weicanjie_db`
--

/*!40000 DROP DATABASE IF EXISTS `weicanjie_db`*/;

-- (database created by docker-entrypoint)



--
-- Table structure for table `admin_user`
--

DROP TABLE IF EXISTS `admin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理员账号',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'BCrypt 密码',
  `status` tinyint DEFAULT '1' COMMENT '账号状态 1正常 0禁用',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'admin' COMMENT 'super / merchant / kitchen',
  `restaurant_id` bigint DEFAULT NULL COMMENT '商家绑定的餐厅ID',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号，用于接收验证码',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user`
--

LOCK TABLES `admin_user` WRITE;
/*!40000 ALTER TABLE `admin_user` DISABLE KEYS */;
INSERT INTO `admin_user` VALUES (1,'admin','$2a$10$MklfGHDikBYUdMSnicKGmuMY91a1GzRsskLJDUqF4zb/Vs2qs0nhK',1,'super',NULL,NULL,'2026-08-26 11:32:39',NULL);
/*!40000 ALTER TABLE `admin_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `restaurant_id` bigint NOT NULL,
  `dish_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `price` decimal(10,2) NOT NULL,
  `notes` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_restaurant_dish` (`user_id`,`restaurant_id`,`dish_id`),
  KEY `restaurant_id` (`restaurant_id`),
  KEY `dish_id` (`dish_id`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`),
  CONSTRAINT `cart_ibfk_3` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `restaurant_id` bigint NOT NULL COMMENT '餐厅ID',
  `sort_order` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `restaurant_id` (`restaurant_id`),
  CONSTRAINT `category_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'热销推荐',1,1,1,'2026-08-26 11:32:39'),(2,'主食',1,2,1,'2026-08-26 11:32:39'),(3,'小吃',1,3,1,'2026-08-26 11:32:39'),(4,'饮品',1,4,1,'2026-08-26 11:32:39'),(5,'招牌菜',2,1,1,'2026-08-26 11:32:39'),(6,'素食',2,2,1,'2026-08-26 11:32:39'),(7,'汤品',2,3,1,'2026-08-26 11:32:39'),(8,'经典川菜',3,1,1,'2026-08-26 11:32:39'),(9,'特色湘菜',3,2,1,'2026-08-26 11:32:39'),(10,'麻辣香锅',3,3,1,'2026-08-26 11:32:39'),(11,'经典披萨',4,1,1,'2026-08-26 11:32:39'),(12,'意面',4,2,1,'2026-08-26 11:32:39'),(13,'沙拉',4,3,1,'2026-08-26 11:32:39'),(14,'皇堡系列',5,1,1,'2026-08-26 11:32:39'),(15,'鸡肉汉堡',5,2,1,'2026-08-26 11:32:39'),(16,'小吃配餐',5,3,1,'2026-08-26 11:32:39');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dish`
--

DROP TABLE IF EXISTS `dish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ingredients` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `taste` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `weight` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `monthly_sales` int DEFAULT '0',
  `category_id` bigint NOT NULL,
  `restaurant_id` bigint NOT NULL,
  `status` tinyint DEFAULT '1',
  `stock` int DEFAULT '999',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `restaurant_id` (`restaurant_id`),
  CONSTRAINT `dish_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `dish_ibfk_2` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dish`
--

LOCK TABLES `dish` WRITE;
/*!40000 ALTER TABLE `dish` DISABLE KEYS */;
INSERT INTO `dish` VALUES (1,'红烧肉',38.00,'经典红烧肉，肥而不腻','https://example.com/dish1.jpg',NULL,NULL,NULL,0,1,1,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(2,'宫保鸡丁',32.00,'麻辣鲜香，口感丰富','https://example.com/dish2.jpg',NULL,NULL,NULL,0,1,1,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(3,'米饭',2.00,'优质大米，香软可口','https://example.com/dish3.jpg',NULL,NULL,NULL,0,2,1,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(4,'可乐',5.00,'冰镇可乐，畅快淋漓','https://example.com/dish4.jpg',NULL,NULL,NULL,0,4,1,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(5,'清蒸鲈鱼',68.00,'新鲜鲈鱼，清蒸锁鲜','https://example.com/dish5.jpg',NULL,NULL,NULL,0,5,2,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(6,'凉拌黄瓜',12.00,'清爽解腻，开胃小菜','https://example.com/dish6.jpg',NULL,NULL,NULL,0,6,2,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(7,'菌菇汤',28.00,'多种菌菇，营养丰富','https://example.com/dish7.jpg',NULL,NULL,NULL,0,7,2,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(8,'水煮鱼',88.00,'麻辣鲜香，鱼肉嫩滑','https://example.com/dish8.jpg',NULL,NULL,NULL,0,8,3,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(9,'剁椒鱼头',68.00,'鲜辣入味，下饭神器','https://example.com/dish9.jpg',NULL,NULL,NULL,0,9,3,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(10,'麻辣香锅',58.00,'自选食材，麻辣过瘾','https://example.com/dish10.jpg',NULL,NULL,NULL,0,10,3,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(11,'超级至尊披萨',98.00,'多种肉类，芝士浓郁','https://example.com/dish11.jpg',NULL,NULL,NULL,0,11,4,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(12,'意大利肉酱面',42.00,'经典口味，肉酱丰富','https://example.com/dish12.jpg',NULL,NULL,NULL,0,12,4,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(13,'凯撒沙拉',32.00,'新鲜蔬菜，特制沙拉酱','https://example.com/dish13.jpg',NULL,NULL,NULL,0,13,4,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(14,'经典皇堡',49.00,'火烤牛肉，香浓多汁','https://example.com/dish14.jpg',NULL,NULL,NULL,0,14,5,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(15,'香脆鸡堡',39.00,'酥脆鸡肉，口感绝佳','https://example.com/dish15.jpg',NULL,NULL,NULL,0,15,5,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(16,'王道嫩香鸡',25.00,'外酥里嫩，秘制香料','https://example.com/dish16.jpg',NULL,NULL,NULL,0,16,5,1,999,'2026-08-26 11:32:39','2026-08-26 11:32:39');
/*!40000 ALTER TABLE `dish` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `dish_id` bigint NOT NULL,
  `dish_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dish_price` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `dish_id` (`dish_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `restaurant_id` bigint NOT NULL,
  `order_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `packing_fee` decimal(10,2) DEFAULT '0.00',
  `eat_type` tinyint DEFAULT '2' COMMENT '用餐方式：1=堂食 2=外带',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单备注',
  `status` tinyint DEFAULT '1',
  `pay_status` tinyint DEFAULT '0',
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pay_time` datetime DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `user_id` (`user_id`),
  KEY `restaurant_id` (`restaurant_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refund_record`
--

DROP TABLE IF EXISTS `refund_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refund_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `restaurant_id` bigint NOT NULL COMMENT '餐厅ID',
  `refund_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户选择的退款原因',
  `refund_remark` text COLLATE utf8mb4_unicode_ci COMMENT '用户填写的详细说明',
  `status` tinyint DEFAULT '1' COMMENT '退款状态：1=申请中 2=商家同意 3=商家拒绝',
  `previous_status` int DEFAULT NULL COMMENT '退款前订单状态',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `approve_time` datetime DEFAULT NULL COMMENT '商家审核时间',
  `refund_amount` decimal(10,2) DEFAULT '0.00' COMMENT '退款金额（可选）',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `user_id` (`user_id`),
  KEY `restaurant_id` (`restaurant_id`),
  CONSTRAINT `refund_record_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `refund_record_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `refund_record_ibfk_3` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refund_record`
--

LOCK TABLES `refund_record` WRITE;
/*!40000 ALTER TABLE `refund_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `refund_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restaurant`
--

DROP TABLE IF EXISTS `restaurant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '餐厅名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '餐厅描述',
  `contact_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `logo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '餐厅logo图片URL',
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-营业，0-歇业',
  `category_type` tinyint NOT NULL COMMENT '餐厅大分类',
  `avg_rating` decimal(3,2) DEFAULT NULL COMMENT '平均评分',
  `monthly_sales` int DEFAULT '0' COMMENT '月销量',
  `packing_fee` decimal(10,2) DEFAULT '0.00' COMMENT '打包费',
  `business_status` tinyint DEFAULT '1' COMMENT '营业状态：1-营业中，2-休息中，3-已打烊',
  `manual_business_status` tinyint DEFAULT '0' COMMENT '商家手动设置营业状态：0-自动，1-营业中，2-未营业',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='餐厅表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurant`
--

LOCK TABLES `restaurant` WRITE;
/*!40000 ALTER TABLE `restaurant` DISABLE KEYS */;
INSERT INTO `restaurant` VALUES (1,'美味餐厅','用心做好每一道菜，给您家的感觉','138-0013-8000','http://localhost:9000/weicanjie/restaurant/1/logo/1787716355034-logo (2).png','北京市朝阳区光华路1号',1,2,4.30,1560,2.00,1,0,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(2,'鲜味小馆','新鲜食材，健康烹饪','139-0013-9000','http://localhost:9000/weicanjie/restaurant/2/logo/1787716349562-logo (4).png','上海市浦东新区张江高科技园区',1,3,NULL,980,1.50,1,0,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(3,'川湘菜馆','正宗川湘风味，辣得过瘾','137-0013-7000','http://localhost:9000/weicanjie/restaurant/3/logo/1787716345188-logo (1).png','广州市天河区天河路385号',1,3,NULL,2100,3.00,1,0,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(4,'披萨小屋','手工薄饼披萨，现烤现卖','136-0013-6000','http://localhost:9000/weicanjie/restaurant/4/logo/1787716339976-logo (7).png','深圳市南山区科技园',1,4,NULL,1350,2.50,1,0,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(5,'汉堡王','经典汉堡，美味不等待','135-0013-5000','http://localhost:9000/weicanjie/restaurant/5/logo/1787716317598-logo (13).png','杭州市西湖区文三路',1,4,NULL,1890,1.00,1,0,'2026-08-26 11:32:39','2026-08-26 11:32:39');
/*!40000 ALTER TABLE `restaurant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restaurant_business_hours`
--

DROP TABLE IF EXISTS `restaurant_business_hours`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant_business_hours` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `day_of_week` tinyint NOT NULL,
  `open_time` time NOT NULL,
  `close_time` time NOT NULL,
  `is_open` tinyint DEFAULT '1',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_restaurant_day` (`restaurant_id`,`day_of_week`),
  CONSTRAINT `restaurant_business_hours_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='餐厅营业时间表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurant_business_hours`
--

LOCK TABLES `restaurant_business_hours` WRITE;
/*!40000 ALTER TABLE `restaurant_business_hours` DISABLE KEYS */;
INSERT INTO `restaurant_business_hours` VALUES (1,1,1,'08:00:00','22:00:00',1,'2026-08-26 11:32:39'),(2,1,2,'08:00:00','22:00:00',1,'2026-08-26 11:32:39'),(3,1,3,'08:00:00','22:00:00',1,'2026-08-26 11:32:39'),(4,1,4,'08:00:00','22:00:00',1,'2026-08-26 11:32:39'),(5,1,5,'08:00:00','22:00:00',1,'2026-08-26 11:32:39'),(6,1,6,'09:00:00','23:00:00',1,'2026-08-26 11:32:39'),(7,1,7,'09:00:00','23:00:00',1,'2026-08-26 11:32:39'),(8,2,1,'09:00:00','21:00:00',1,'2026-08-26 11:32:39'),(9,2,2,'09:00:00','21:00:00',1,'2026-08-26 11:32:39'),(10,2,3,'09:00:00','21:00:00',1,'2026-08-26 11:32:39'),(11,2,4,'09:00:00','21:00:00',1,'2026-08-26 11:32:39'),(12,2,5,'09:00:00','21:00:00',1,'2026-08-26 11:32:39'),(13,2,6,'10:00:00','22:00:00',1,'2026-08-26 11:32:39'),(14,2,7,'10:00:00','22:00:00',1,'2026-08-26 11:32:39'),(15,3,1,'10:00:00','23:00:00',1,'2026-08-26 11:32:39'),(16,3,2,'10:00:00','23:00:00',1,'2026-08-26 11:32:39'),(17,3,3,'10:00:00','23:00:00',1,'2026-08-26 11:32:39'),(18,3,4,'10:00:00','23:00:00',1,'2026-08-26 11:32:39'),(19,3,5,'10:00:00','23:00:00',1,'2026-08-26 11:32:39'),(20,3,6,'11:00:00','00:00:00',1,'2026-08-26 11:32:39'),(21,3,7,'11:00:00','00:00:00',1,'2026-08-26 11:32:39'),(22,4,1,'10:30:00','22:30:00',1,'2026-08-26 11:32:39'),(23,4,2,'10:30:00','22:30:00',1,'2026-08-26 11:32:39'),(24,4,3,'10:30:00','22:30:00',1,'2026-08-26 11:32:39'),(25,4,4,'10:30:00','22:30:00',1,'2026-08-26 11:32:39'),(26,4,5,'10:30:00','22:30:00',1,'2026-08-26 11:32:39'),(27,4,6,'11:00:00','23:00:00',1,'2026-08-26 11:32:39'),(28,4,7,'11:00:00','23:00:00',1,'2026-08-26 11:32:39'),(29,5,1,'08:00:00','22:00:00',1,'2026-08-26 11:32:39'),(30,5,2,'08:00:00','22:00:00',1,'2026-08-26 11:32:39'),(31,5,3,'08:00:00','22:00:00',1,'2026-08-26 11:32:39'),(32,5,4,'08:00:00','22:00:00',1,'2026-08-26 11:32:39'),(33,5,5,'08:00:00','22:00:00',1,'2026-08-26 11:32:39'),(34,5,6,'09:00:00','23:00:00',1,'2026-08-26 11:32:39'),(35,5,7,'09:00:00','23:00:00',1,'2026-08-26 11:32:39');
/*!40000 ALTER TABLE `restaurant_business_hours` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restaurant_category`
--

DROP TABLE IF EXISTS `restaurant_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant_category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序(数字越小越靠前)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='餐厅大分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurant_category`
--

LOCK TABLES `restaurant_category` WRITE;
/*!40000 ALTER TABLE `restaurant_category` DISABLE KEYS */;
INSERT INTO `restaurant_category` VALUES (1,'家常菜',1),(2,'正餐',2),(3,'快餐',3),(4,'早餐',4),(5,'小吃',5),(6,'烧烤',6),(7,'夜宵',7),(8,'饮品',8);
/*!40000 ALTER TABLE `restaurant_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restaurant_images`
--

DROP TABLE IF EXISTS `restaurant_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int DEFAULT '0',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `restaurant_id` (`restaurant_id`),
  CONSTRAINT `restaurant_images_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='餐厅图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurant_images`
--

LOCK TABLES `restaurant_images` WRITE;
/*!40000 ALTER TABLE `restaurant_images` DISABLE KEYS */;
INSERT INTO `restaurant_images` VALUES (1,1,'https://example.com/restaurant1_1.jpg',1,'2026-08-26 11:32:39'),(2,1,'https://example.com/restaurant1_2.jpg',2,'2026-08-26 11:32:39'),(3,1,'https://example.com/restaurant1_3.jpg',3,'2026-08-26 11:32:39'),(4,2,'https://example.com/restaurant2_1.jpg',1,'2026-08-26 11:32:39'),(5,3,'https://example.com/restaurant3_1.jpg',1,'2026-08-26 11:32:39');
/*!40000 ALTER TABLE `restaurant_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review_report`
--

DROP TABLE IF EXISTS `review_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `review_id` bigint NOT NULL COMMENT '被举报的评价ID',
  `reporter_id` bigint NOT NULL COMMENT '举报人（用户或商家）',
  `restaurant_id` bigint NOT NULL COMMENT '评价所属餐厅',
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '举报原因（选项）',
  `detail` text COLLATE utf8mb4_unicode_ci COMMENT '补充说明',
  `images` json DEFAULT NULL COMMENT '截图证据',
  `status` tinyint DEFAULT '0' COMMENT '0待审核 1通过 2驳回',
  `result_comment` text COLLATE utf8mb4_unicode_ci COMMENT '审核备注（平台给的说明）',
  `review_action` tinyint DEFAULT '0' COMMENT '审核后对评价的处理：0不处理 1隐藏评价',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `review_id` (`review_id`),
  KEY `reporter_id` (`reporter_id`),
  KEY `restaurant_id` (`restaurant_id`),
  CONSTRAINT `review_report_ibfk_1` FOREIGN KEY (`review_id`) REFERENCES `user_review` (`id`),
  CONSTRAINT `review_report_ibfk_2` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`),
  CONSTRAINT `review_report_ibfk_3` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价举报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_report`
--

LOCK TABLES `review_report` WRITE;
/*!40000 ALTER TABLE `review_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `review_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_favorite`
--

DROP TABLE IF EXISTS `user_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `restaurant_id` bigint NOT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_restaurant` (`user_id`,`restaurant_id`),
  KEY `restaurant_id` (`restaurant_id`),
  CONSTRAINT `user_favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `user_favorite_ibfk_2` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_favorite`
--

LOCK TABLES `user_favorite` WRITE;
/*!40000 ALTER TABLE `user_favorite` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_history`
--

DROP TABLE IF EXISTS `user_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `restaurant_id` bigint NOT NULL,
  `viewed_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_restaurant` (`user_id`,`restaurant_id`),
  KEY `restaurant_id` (`restaurant_id`),
  CONSTRAINT `user_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `user_history_ibfk_2` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户浏览历史记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_history`
--

LOCK TABLES `user_history` WRITE;
/*!40000 ALTER TABLE `user_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_review`
--

DROP TABLE IF EXISTS `user_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `order_id` bigint DEFAULT NULL,
  `restaurant_id` bigint NOT NULL,
  `rating` tinyint NOT NULL,
  `taste` tinyint DEFAULT '5',
  `pack` tinyint DEFAULT '5',
  `content` text COLLATE utf8mb4_unicode_ci,
  `image_urls` json DEFAULT NULL,
  `is_anonymous` tinyint DEFAULT '0' COMMENT '0否 1是',
  `reply_content` text COLLATE utf8mb4_unicode_ci COMMENT '商家回复内容',
  `reply_time` datetime DEFAULT NULL COMMENT '商家回复时间',
  `status` tinyint DEFAULT '1' COMMENT '1正常显示 0隐藏',
  `review_status` tinyint DEFAULT '1' COMMENT '审核状态：0待审核 1已通过 2已拒绝',
  `reject_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核拒绝原因',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `restaurant_id` (`restaurant_id`),
  CONSTRAINT `user_review_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `user_review_ibfk_2` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_review`
--

LOCK TABLES `user_review` WRITE;
/*!40000 ALTER TABLE `user_review` DISABLE KEYS */;
INSERT INTO `user_review` VALUES (1,1,NULL,1,3,5,5,'这家汉堡真的绝了！分量大味道好。','[\"/images/test/1.jpg\", \"/images/test/2.jpg\"]',1,NULL,NULL,1,1,NULL,NULL,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(2,2,NULL,1,5,5,5,'鸡腿很大，味道一级棒！','[]',0,NULL,NULL,1,1,NULL,NULL,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(3,2,NULL,2,5,5,5,'味道一级棒！','[]',0,NULL,NULL,1,1,NULL,NULL,'2026-08-26 11:32:39','2026-08-26 11:32:39'),(4,3,NULL,1,5,5,5,'两只鸡腿太满足了！','[\"/images/test/3.jpg\"]',0,NULL,NULL,1,1,NULL,NULL,'2026-08-26 11:32:39','2026-08-26 11:32:39');
/*!40000 ALTER TABLE `user_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_stats`
--

DROP TABLE IF EXISTS `user_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `favorite_count` int DEFAULT '0',
  `order_count` int DEFAULT '0',
  `review_count` int DEFAULT '0',
  `total_spent` decimal(10,2) DEFAULT '0.00',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  CONSTRAINT `user_stats_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_stats`
--

LOCK TABLES `user_stats` WRITE;
/*!40000 ALTER TABLE `user_stats` DISABLE KEYS */;
INSERT INTO `user_stats` VALUES (1,1,0,22,0,1936.00,'2026-08-26 19:47:07','2026-08-26 19:47:07');
/*!40000 ALTER TABLE `user_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `openid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信openid',
  `nickname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
  `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `openid` (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'test_openid_1','匿名用户','/images/default-avatar.png','18181111111','2026-08-26 11:32:38','2026-08-26 11:32:38'),(2,'test_openid_2','d***1','/images/default-avatar.png','18182222222','2026-08-26 11:32:38','2026-08-26 11:32:38'),(3,'test_openid_3','3***9','/images/default-avatar.png','18183333333','2026-08-26 11:32:38','2026-08-26 11:32:38');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'weicanjie_db'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-27 13:14:24


