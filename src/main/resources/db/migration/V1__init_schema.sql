CREATE DATABASE  IF NOT EXISTS `book_haven` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `book_haven`;
-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: book_haven
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `address_id` int NOT NULL AUTO_INCREMENT,
  `district` varchar(255) NOT NULL,
  `province` varchar(255) NOT NULL,
  `road` varchar(255) DEFAULT NULL,
  `town` varchar(255) NOT NULL,
  `type` enum('Company','Home') NOT NULL,
  `customer_id` int DEFAULT NULL,
  `manager_id` int DEFAULT NULL,
  PRIMARY KEY (`address_id`),
  KEY `FKpkaif5e5vy013h0spva59jpfd` (`customer_id`),
  KEY `FKsuo0clijqht6stnpbk9ip8yug` (`manager_id`),
  CONSTRAINT `FKpkaif5e5vy013h0spva59jpfd` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `FKsuo0clijqht6stnpbk9ip8yug` FOREIGN KEY (`manager_id`) REFERENCES `managers` (`manager_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'Cầu Kè','Trà Vinh','An Trại','An Phú Tân','Home',NULL,2);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authors`
--

DROP TABLE IF EXISTS `authors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `authors` (
  `author_id` int NOT NULL AUTO_INCREMENT,
  `birth` date DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `nationality` varchar(255) NOT NULL,
  PRIMARY KEY (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authors`
--

LOCK TABLES `authors` WRITE;
/*!40000 ALTER TABLE `authors` DISABLE KEYS */;
/*!40000 ALTER TABLE `authors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `cart_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_id` int NOT NULL,
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `UK867x3yysb1f3jk41cv3vsoejj` (`customer_id`),
  CONSTRAINT `FKioh3c0mo0al2kswtnk5r09y7f` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
  `cart_item_id` int NOT NULL AUTO_INCREMENT,
  `added_price` decimal(38,2) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `quantity` int NOT NULL,
  `selected` bit(1) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `cart_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`cart_item_id`),
  UNIQUE KEY `UK2fypguoq8qc1uigt9i6em0t07` (`cart_id`,`product_id`),
  KEY `FKqkqmvkmbtiaqn2nfqf25ymfs2` (`product_id`),
  CONSTRAINT `FK1uobyhgl1wvgt1jpccia8xxs3` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`cart_id`),
  CONSTRAINT `FKqkqmvkmbtiaqn2nfqf25ymfs2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Phiêu lưu muôn nơi','Tiểu thuyết'),(2,'Không thở được','Kinh dị'),(3,'Không khép được mồm','Hài hước');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_messages`
--

DROP TABLE IF EXISTS `chat_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(255) NOT NULL,
  `sender_type` varchar(255) NOT NULL,
  `timestamp` datetime(6) NOT NULL,
  `customer_id` int NOT NULL,
  `manager_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKf8h7t26bisb22sijjo6fap0nh` (`customer_id`),
  KEY `FKtn2wxjtrsftaehyrykjfpg1dh` (`manager_id`),
  CONSTRAINT `FKf8h7t26bisb22sijjo6fap0nh` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `FKtn2wxjtrsftaehyrykjfpg1dh` FOREIGN KEY (`manager_id`) REFERENCES `managers` (`manager_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_messages`
--

LOCK TABLES `chat_messages` WRITE;
/*!40000 ALTER TABLE `chat_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `avatar` mediumblob,
  `birth` date DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified` bit(1) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `gender` tinyint DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) NOT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (2,NULL,'2000-01-01','testverityemail@gmail.com',_binary '\0','test1',0,'test','$2a$10$YY9l70sP/Ou1tT31QeOcb.Slt6atxdTdBOidd.5B7BMP1BtdqYTm2','0123456789'),(3,NULL,'2000-01-01','testlock@gmail.com',_binary '','testlock',0,'testlock','$2a$10$YY9l70sP/Ou1tT31QeOcb.Slt6atxdTdBOidd.5B7BMP1BtdqYTm2','0123456789'),(4,NULL,'2000-01-01','testlogin@gmail.com',_binary '','testlogin',0,'testlogin','$2a$10$YY9l70sP/Ou1tT31QeOcb.Slt6atxdTdBOidd.5B7BMP1BtdqYTm2','0123456789');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback_reply`
--

DROP TABLE IF EXISTS `feedback_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback_reply` (
  `reply_id` int NOT NULL AUTO_INCREMENT,
  `replied_at` datetime(6) NOT NULL,
  `replied_by` varchar(255) NOT NULL,
  `reply_text` text NOT NULL,
  `manager_feedback_id` int DEFAULT NULL,
  `product_feedback_id` int DEFAULT NULL,
  PRIMARY KEY (`reply_id`),
  KEY `FK7akyrwp6dsno1bwfqko025624` (`manager_feedback_id`),
  KEY `FK5yte6ookd5fo60bdfwko9gvtf` (`product_feedback_id`),
  CONSTRAINT `FK5yte6ookd5fo60bdfwko9gvtf` FOREIGN KEY (`product_feedback_id`) REFERENCES `product_feedbacks` (`feedback_id`),
  CONSTRAINT `FK7akyrwp6dsno1bwfqko025624` FOREIGN KEY (`manager_feedback_id`) REFERENCES `manager_feedbacks` (`feedback_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback_reply`
--

LOCK TABLES `feedback_reply` WRITE;
/*!40000 ALTER TABLE `feedback_reply` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedback_reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback_report`
--

DROP TABLE IF EXISTS `feedback_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback_report` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `report_reason` text NOT NULL,
  `report_status` enum('PENDING','REVIEWED','RESOLVED') NOT NULL DEFAULT 'PENDING',
  `reported_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `product_feedback_id` int DEFAULT NULL,
  `manager_feedback_id` int DEFAULT NULL,
  `reported_by_customer_id` int DEFAULT NULL,
  `reported_by_manager_id` int DEFAULT NULL,
  PRIMARY KEY (`report_id`),
  KEY `product_feedback_id` (`product_feedback_id`),
  KEY `manager_feedback_id` (`manager_feedback_id`),
  KEY `reported_by_customer_id` (`reported_by_customer_id`),
  KEY `reported_by_manager_id` (`reported_by_manager_id`),
  CONSTRAINT `feedback_report_ibfk_1` FOREIGN KEY (`product_feedback_id`) REFERENCES `product_feedbacks` (`feedback_id`) ON DELETE SET NULL,
  CONSTRAINT `feedback_report_ibfk_2` FOREIGN KEY (`manager_feedback_id`) REFERENCES `manager_feedbacks` (`feedback_id`) ON DELETE SET NULL,
  CONSTRAINT `feedback_report_ibfk_3` FOREIGN KEY (`reported_by_customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE SET NULL,
  CONSTRAINT `feedback_report_ibfk_4` FOREIGN KEY (`reported_by_manager_id`) REFERENCES `managers` (`manager_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback_report`
--

LOCK TABLES `feedback_report` WRITE;
/*!40000 ALTER TABLE `feedback_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedback_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_history`
--

DROP TABLE IF EXISTS `login_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_history` (
  `login_id` int NOT NULL AUTO_INCREMENT,
  `device_info` text,
  `ip_address` varchar(45) NOT NULL,
  `login_time` datetime(6) NOT NULL,
  `customer_id` int DEFAULT NULL,
  `manager_id` int DEFAULT NULL,
  PRIMARY KEY (`login_id`),
  KEY `FKbylxk796yfe3tdx2oveeke417` (`customer_id`),
  KEY `FKgb7oaex9tj1weuitunnb0njtg` (`manager_id`),
  CONSTRAINT `FKbylxk796yfe3tdx2oveeke417` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `FKgb7oaex9tj1weuitunnb0njtg` FOREIGN KEY (`manager_id`) REFERENCES `managers` (`manager_id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_history`
--

LOCK TABLES `login_history` WRITE;
/*!40000 ALTER TABLE `login_history` DISABLE KEYS */;
INSERT INTO `login_history` VALUES (25,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0.0','0:0:0:0:0:0:0:1','2025-04-05 12:26:32.884634',4,NULL),(40,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36 Edg/135.0.0.0','0:0:0:0:0:0:0:1','2025-04-08 11:48:14.492495',NULL,2),(41,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36 Edg/135.0.0.0','0:0:0:0:0:0:0:1','2025-04-08 12:06:02.647685',NULL,2);
/*!40000 ALTER TABLE `login_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manager_account`
--

DROP TABLE IF EXISTS `manager_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manager_account` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `create_at` date NOT NULL,
  `recent_activity` datetime(6) DEFAULT NULL,
  `status` enum('Wait','Confirm','Warning','Locked') DEFAULT NULL,
  `update_at` datetime(6) DEFAULT NULL,
  `customer_id` int DEFAULT NULL,
  `manager_id` int DEFAULT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `UKqpaxrk4udyrxrya7te1lpiw5k` (`customer_id`),
  UNIQUE KEY `UKoua2miy4h5ppf447wrwxnmllc` (`manager_id`),
  CONSTRAINT `FKaunfbqdbo7r3ujyo7f8hke6ie` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `FKlb07uurnrhxxcwppcng7rt1ms` FOREIGN KEY (`manager_id`) REFERENCES `managers` (`manager_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manager_account`
--

LOCK TABLES `manager_account` WRITE;
/*!40000 ALTER TABLE `manager_account` DISABLE KEYS */;
INSERT INTO `manager_account` VALUES (2,'2025-04-03','2025-04-03 21:30:12.622254','Wait','2025-04-03 21:30:12.622254',2,NULL),(3,'2025-04-03','2025-04-05 19:15:30.919700','Confirm','2025-04-05 19:15:30.919700',3,NULL),(4,'2025-04-03','2025-04-03 21:30:12.622254','Confirm','2025-04-03 21:30:12.622254',4,NULL),(6,'2025-04-03','2025-04-03 22:34:37.405357','Confirm','2025-04-03 22:34:37.405357',NULL,1),(7,'2025-04-08','2025-04-08 11:24:40.996034','Confirm','2025-04-08 11:24:40.996034',NULL,2),(8,'2025-04-08','2025-04-08 11:39:18.518381','Confirm','2025-04-08 11:39:18.518381',NULL,3);
/*!40000 ALTER TABLE `manager_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manager_feedbacks`
--

DROP TABLE IF EXISTS `manager_feedbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manager_feedbacks` (
  `feedback_id` int NOT NULL AUTO_INCREMENT,
  `comment` text,
  `created_at` datetime(6) NOT NULL,
  `rating` int NOT NULL,
  `customer_id` int NOT NULL,
  `manager_id` int NOT NULL,
  `order_id` int NOT NULL,
  PRIMARY KEY (`feedback_id`),
  KEY `FKjf5ptea47fbaqflg0v9x6mvi0` (`customer_id`),
  KEY `FK21a6a8o2ce0yty3vhhdx35yeg` (`manager_id`),
  KEY `FK1gehgyi2wd9kfg66sxc5psili` (`order_id`),
  CONSTRAINT `FK1gehgyi2wd9kfg66sxc5psili` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FK21a6a8o2ce0yty3vhhdx35yeg` FOREIGN KEY (`manager_id`) REFERENCES `managers` (`manager_id`),
  CONSTRAINT `FKjf5ptea47fbaqflg0v9x6mvi0` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manager_feedbacks`
--

LOCK TABLES `manager_feedbacks` WRITE;
/*!40000 ALTER TABLE `manager_feedbacks` DISABLE KEYS */;
/*!40000 ALTER TABLE `manager_feedbacks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `managers`
--

DROP TABLE IF EXISTS `managers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `managers` (
  `manager_id` int NOT NULL AUTO_INCREMENT,
  `avatar` mediumblob,
  `email` varchar(255) NOT NULL,
  `manager_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`manager_id`),
  UNIQUE KEY `UK9t1pttj95csyjl4it8lxgl7jj` (`email`),
  KEY `FKp120yp1v9255xi0050d9gqq2p` (`role_id`),
  CONSTRAINT `FKp120yp1v9255xi0050d9gqq2p` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `managers`
--

LOCK TABLES `managers` WRITE;
/*!40000 ALTER TABLE `managers` DISABLE KEYS */;
INSERT INTO `managers` VALUES (1,NULL,'admin@gmail.com','admin','$2a$10$NSgpT.iB6pasiMNEbx2sy..nGPnNfYX/RvkRWG51U.qddnaNCd4Du',1),(2,NULL,'truongbaock@gmail.com','string','$2a$10$NSgpT.iB6pasiMNEbx2sy..nGPnNfYX/RvkRWG51U.qddnaNCd4Du',2),(3,NULL,'test@gmail.com','test','$2a$10$NSgpT.iB6pasiMNEbx2sy..nGPnNfYX/RvkRWG51U.qddnaNCd4Du',2);
/*!40000 ALTER TABLE `managers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `sent_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `customer_id` int DEFAULT NULL,
  `manager_id` int DEFAULT NULL,
  `sender_manager_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_messages_customer` (`customer_id`),
  KEY `fk_messages_manager` (`manager_id`),
  KEY `fk_messages_sender` (`sender_manager_id`),
  CONSTRAINT `fk_messages_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_messages_manager` FOREIGN KEY (`manager_id`) REFERENCES `managers` (`manager_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_messages_sender` FOREIGN KEY (`sender_manager_id`) REFERENCES `managers` (`manager_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (2,'Cảnh báo đăng nhập bất thường','Phát hiện đăng nhập từ IP: 0:0:0:0:0:0:0:1 trên thiết bị: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0.0 vào lúc 2025-04-05T12:26:32.884633800','2025-04-05 12:26:33',0,4,NULL,1),(7,'Cảnh báo đăng nhập bất thường','Phát hiện đăng nhập từ IP: 0:0:0:0:0:0:0:1 trên thiết bị: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36 Edg/135.0.0.0 vào lúc 2025-04-08T11:48:14.492494900','2025-04-08 11:48:15',0,NULL,2,1);
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
  `order_detail_id` int NOT NULL AUTO_INCREMENT,
  `distance` float NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `quantity` int NOT NULL,
  `subtotal` decimal(38,2) NOT NULL,
  `weight` float NOT NULL,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `shipping_fee_id` int DEFAULT NULL,
  PRIMARY KEY (`order_detail_id`),
  KEY `FKjyu2qbqt8gnvno9oe9j2s2ldk` (`order_id`),
  KEY `FK4q98utpd73imf4yhttm3w0eax` (`product_id`),
  KEY `FKckiti9l1ed35bbj7rqjn6prpk` (`shipping_fee_id`),
  CONSTRAINT `FK4q98utpd73imf4yhttm3w0eax` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `FKckiti9l1ed35bbj7rqjn6prpk` FOREIGN KEY (`shipping_fee_id`) REFERENCES `shipping_fees` (`fee_id`),
  CONSTRAINT `FKjyu2qbqt8gnvno9oe9j2s2ldk` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_statuses`
--

DROP TABLE IF EXISTS `order_statuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_statuses` (
  `order_status_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`order_status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_statuses`
--

LOCK TABLES `order_statuses` WRITE;
/*!40000 ALTER TABLE `order_statuses` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_statuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_tracking`
--

DROP TABLE IF EXISTS `order_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_tracking` (
  `tracking_id` int NOT NULL AUTO_INCREMENT,
  `changed_at` datetime(6) NOT NULL,
  `order_id` int NOT NULL,
  `order_status_id` int NOT NULL,
  PRIMARY KEY (`tracking_id`),
  KEY `FKeu0lumcx8bcx6lk035xiklty0` (`order_id`),
  KEY `FKo0odg4ndkin3fc8hpvvf73pug` (`order_status_id`),
  CONSTRAINT `FKeu0lumcx8bcx6lk035xiklty0` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKo0odg4ndkin3fc8hpvvf73pug` FOREIGN KEY (`order_status_id`) REFERENCES `order_statuses` (`order_status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_tracking`
--

LOCK TABLES `order_tracking` WRITE;
/*!40000 ALTER TABLE `order_tracking` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_tracking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_vouchers`
--

DROP TABLE IF EXISTS `order_vouchers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_vouchers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `discount_amount` decimal(38,2) NOT NULL,
  `total_after` decimal(38,2) NOT NULL,
  `order_id` int NOT NULL,
  `voucher_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKou5nvss1n60lohkt93jff4ma6` (`order_id`),
  KEY `FKdw65d1fy6feqn3ywydnipbkgc` (`voucher_id`),
  CONSTRAINT `FKdw65d1fy6feqn3ywydnipbkgc` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`voucher_id`),
  CONSTRAINT `FKkya6rv5ngvecp1qv0a9oifi1y` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_vouchers`
--

LOCK TABLES `order_vouchers` WRITE;
/*!40000 ALTER TABLE `order_vouchers` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_vouchers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `sender_address` varchar(255) NOT NULL,
  `commission_fee` decimal(38,2) NOT NULL,
  `delivery_date` date DEFAULT NULL,
  `order_date` date NOT NULL,
  `ship_address` varchar(255) NOT NULL,
  `shipping_fee` decimal(38,2) NOT NULL,
  `total` decimal(38,2) NOT NULL,
  `customer_id` int NOT NULL,
  `manager_id` int NOT NULL,
  `order_status_id` int NOT NULL,
  `payment_id` int NOT NULL,
  `shipper_id` int NOT NULL,
  PRIMARY KEY (`order_id`),
  KEY `FKpxtb8awmi0dk6smoh2vp1litg` (`customer_id`),
  KEY `FK3kb98kmk1xemhxbjomoaec280` (`manager_id`),
  KEY `FKcbbqf26brulgfgvd0mf74rv4y` (`order_status_id`),
  KEY `FK8aol9f99s97mtyhij0tvfj41f` (`payment_id`),
  KEY `FKsk2tyu7xrdu2ienuay5yrpgoe` (`shipper_id`),
  CONSTRAINT `FK3kb98kmk1xemhxbjomoaec280` FOREIGN KEY (`manager_id`) REFERENCES `managers` (`manager_id`),
  CONSTRAINT `FK8aol9f99s97mtyhij0tvfj41f` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`payment_id`),
  CONSTRAINT `FKcbbqf26brulgfgvd0mf74rv4y` FOREIGN KEY (`order_status_id`) REFERENCES `order_statuses` (`order_status_id`),
  CONSTRAINT `FKpxtb8awmi0dk6smoh2vp1litg` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `FKsk2tyu7xrdu2ienuay5yrpgoe` FOREIGN KEY (`shipper_id`) REFERENCES `shippers` (`shipper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_feedbacks`
--

DROP TABLE IF EXISTS `product_feedbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_feedbacks` (
  `feedback_id` int NOT NULL AUTO_INCREMENT,
  `comment` text,
  `created_at` datetime(6) NOT NULL,
  `rating` int NOT NULL,
  `customer_id` int NOT NULL,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`feedback_id`),
  KEY `FKb7ubfaxwfpmq7b9mntky0cqd5` (`customer_id`),
  KEY `FKbdf0kxmhc7jidsjuvywnjniil` (`order_id`),
  KEY `FKowkidhxixma5y7qo5fxq9qyd8` (`product_id`),
  CONSTRAINT `FKb7ubfaxwfpmq7b9mntky0cqd5` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `FKbdf0kxmhc7jidsjuvywnjniil` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKowkidhxixma5y7qo5fxq9qyd8` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_feedbacks`
--

LOCK TABLES `product_feedbacks` WRITE;
/*!40000 ALTER TABLE `product_feedbacks` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_feedbacks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `picture` mediumblob,
  `weight` float DEFAULT NULL,
  `author_id` int NOT NULL,
  `category_id` int NOT NULL,
  `publisher_id` int NOT NULL,
  PRIMARY KEY (`product_id`),
  KEY `FKy2kver9ldog29n3mi9b12w64` (`author_id`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  KEY `FK6dd6en5l5avuu43towdby894e` (`publisher_id`),
  CONSTRAINT `FK6dd6en5l5avuu43towdby894e` FOREIGN KEY (`publisher_id`) REFERENCES `publishers` (`publisher_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`),
  CONSTRAINT `FKy2kver9ldog29n3mi9b12w64` FOREIGN KEY (`author_id`) REFERENCES `authors` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `publishers`
--

DROP TABLE IF EXISTS `publishers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `publishers` (
  `publisher_id` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`publisher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `publishers`
--

LOCK TABLES `publishers` WRITE;
/*!40000 ALTER TABLE `publishers` DISABLE KEYS */;
/*!40000 ALTER TABLE `publishers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` int NOT NULL,
  `role_name` enum('ADMIN','SHOP') NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMIN'),(2,'SHOP');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `search_history`
--

DROP TABLE IF EXISTS `search_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `search_history` (
  `search_id` bigint NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) NOT NULL,
  `searched_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `customer_id` int NOT NULL,
  PRIMARY KEY (`search_id`),
  KEY `fk_search_history_customer` (`customer_id`),
  CONSTRAINT `fk_search_history_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `search_history`
--

LOCK TABLES `search_history` WRITE;
/*!40000 ALTER TABLE `search_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `search_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shippers`
--

DROP TABLE IF EXISTS `shippers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shippers` (
  `shipper_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  PRIMARY KEY (`shipper_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shippers`
--

LOCK TABLES `shippers` WRITE;
/*!40000 ALTER TABLE `shippers` DISABLE KEYS */;
INSERT INTO `shippers` VALUES (1,'one@gmail.com','Shipper one','0123456789'),(2,'two@gmail.com','Shipper two','0123456789'),(3,'three@gmail.com','Shipper three','0123456789');
/*!40000 ALTER TABLE `shippers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_fees`
--

DROP TABLE IF EXISTS `shipping_fees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_fees` (
  `fee_id` int NOT NULL AUTO_INCREMENT,
  `distance` float NOT NULL,
  `fee` decimal(38,2) NOT NULL,
  `weight` float NOT NULL,
  PRIMARY KEY (`fee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_fees`
--

LOCK TABLES `shipping_fees` WRITE;
/*!40000 ALTER TABLE `shipping_fees` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipping_fees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_product`
--

DROP TABLE IF EXISTS `shop_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `price` decimal(38,2) NOT NULL,
  `quantity` int NOT NULL,
  `manager_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKddowg4n6tnyi24xntsbfb7g57` (`manager_id`),
  KEY `FKubxhxe1ahvu51e4lcm752mh0` (`product_id`),
  CONSTRAINT `FKddowg4n6tnyi24xntsbfb7g57` FOREIGN KEY (`manager_id`) REFERENCES `managers` (`manager_id`),
  CONSTRAINT `FKubxhxe1ahvu51e4lcm752mh0` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_product`
--

LOCK TABLES `shop_product` WRITE;
/*!40000 ALTER TABLE `shop_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_subscriptions`
--

DROP TABLE IF EXISTS `shop_subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_subscriptions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `end_date` datetime(6) NOT NULL,
  `start_date` datetime(6) NOT NULL,
  `status` enum('ACTIVE','CANCELLED','EXPIRED','PENDING') NOT NULL,
  `manager_id` int NOT NULL,
  `plan_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_shop_subscriptions_manager` (`manager_id`),
  KEY `fk_shop_subscriptions_plan` (`plan_id`),
  CONSTRAINT `fk_shop_subscriptions_manager` FOREIGN KEY (`manager_id`) REFERENCES `managers` (`manager_id`),
  CONSTRAINT `fk_shop_subscriptions_plan` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plans` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_subscriptions`
--

LOCK TABLES `shop_subscriptions` WRITE;
/*!40000 ALTER TABLE `shop_subscriptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscription_benefits`
--

DROP TABLE IF EXISTS `subscription_benefits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription_benefits` (
  `id` int NOT NULL AUTO_INCREMENT,
  `benefit_type` varchar(50) NOT NULL,
  `benefit_value` decimal(10,2) NOT NULL,
  `plan_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa0r9quicjw2fpqjfq3qsp413k` (`plan_id`),
  CONSTRAINT `FKa0r9quicjw2fpqjfq3qsp413k` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plans` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription_benefits`
--

LOCK TABLES `subscription_benefits` WRITE;
/*!40000 ALTER TABLE `subscription_benefits` DISABLE KEYS */;
INSERT INTO `subscription_benefits` VALUES (1,'Vận chuyển',10.00,1),(2,'Giảm hoa hồng',10.00,2),(3,'Gì cũng có',10.00,3);
/*!40000 ALTER TABLE `subscription_benefits` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscription_plans`
--

DROP TABLE IF EXISTS `subscription_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription_plans` (
  `id` int NOT NULL AUTO_INCREMENT,
  `duration` int NOT NULL,
  `plan_name` varchar(255) NOT NULL,
  `price` float NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription_plans`
--

LOCK TABLES `subscription_plans` WRITE;
/*!40000 ALTER TABLE `subscription_plans` DISABLE KEYS */;
INSERT INTO `subscription_plans` VALUES (1,10,'Cơ bản',10),(2,20,'Trung bình',20),(3,30,'Cao cấp',30);
/*!40000 ALTER TABLE `subscription_plans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `payment_status` enum('COMPLETED','FAILED','PENDING','REFUNDED') NOT NULL,
  `customer_id` int NOT NULL,
  `order_id` int NOT NULL,
  `payment_id` int NOT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `FKpnnreq9lpejqyjfct60v7n7x1` (`customer_id`),
  KEY `FKfyxndk58yiq2vpn0yd4m09kbt` (`order_id`),
  KEY `FKmt44qv8av8abvaqb5nbhjnmi2` (`payment_id`),
  CONSTRAINT `FKfyxndk58yiq2vpn0yd4m09kbt` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKmt44qv8av8abvaqb5nbhjnmi2` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`payment_id`),
  CONSTRAINT `FKpnnreq9lpejqyjfct60v7n7x1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verification_token`
--

DROP TABLE IF EXISTS `verification_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verification_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) DEFAULT NULL,
  `otp` varchar(255) DEFAULT NULL,
  `customer_id` int NOT NULL,
  `manager_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlmgntqylcm2ni08tx1kce5ntw` (`customer_id`),
  UNIQUE KEY `UKa060dp25nmfsm6eai5qshwl1j` (`manager_id`),
  CONSTRAINT `FKc2nlatnjj1maco33dyio98yyv` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `FKk1qssnhvpiio1td9rvyjgluiw` FOREIGN KEY (`manager_id`) REFERENCES `managers` (`manager_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verification_token`
--

LOCK TABLES `verification_token` WRITE;
/*!40000 ALTER TABLE `verification_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `verification_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vouchers`
--

DROP TABLE IF EXISTS `vouchers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vouchers` (
  `voucher_id` int NOT NULL AUTO_INCREMENT,
  `created_at` date NOT NULL,
  `discount_percentage` float NOT NULL,
  `expiration_date` date NOT NULL,
  `voucher_code` varchar(255) NOT NULL,
  PRIMARY KEY (`voucher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vouchers`
--

LOCK TABLES `vouchers` WRITE;
/*!40000 ALTER TABLE `vouchers` DISABLE KEYS */;
INSERT INTO `vouchers` VALUES (1,'2025-04-05',10,'2025-05-05','Voucher one');
/*!40000 ALTER TABLE `vouchers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

