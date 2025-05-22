-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: restaurant
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Закуски'),(2,'Основные блюда'),(3,'Десерты'),(4,'Напитки');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu_item_images`
--

DROP TABLE IF EXISTS `menu_item_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_item_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_item_id` bigint NOT NULL,
  `image_url` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `menu_item_id` (`menu_item_id`),
  CONSTRAINT `menu_item_images_ibfk_1` FOREIGN KEY (`menu_item_id`) REFERENCES `menu_items` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_item_images`
--

LOCK TABLES `menu_item_images` WRITE;
/*!40000 ALTER TABLE `menu_item_images` DISABLE KEYS */;
INSERT INTO `menu_item_images` VALUES (1,1,'https://res.cloudinary.com/drixmxite/image/upload/v1744543150/salat-cesar_fv9wtq.png'),(2,2,'https://res.cloudinary.com/drixmxite/image/upload/v1744543149/Borsch_fwdr3z.png'),(3,3,'https://res.cloudinary.com/drixmxite/image/upload/v1744543151/steak_hf4rx7.png'),(4,4,'https://res.cloudinary.com/drixmxite/image/upload/v1744543154/tort-napoleon_rijugj.png'),(5,5,'https://res.cloudinary.com/drixmxite/image/upload/v1744543150/pasta-carbonara_kwcl53.png'),(6,6,'https://res.cloudinary.com/drixmxite/image/upload/v1744543150/lasagna_rydkj4.png'),(7,7,'https://res.cloudinary.com/drixmxite/image/upload/v1744543150/minestrone-soup_sjtx2o.png'),(8,8,'https://res.cloudinary.com/drixmxite/image/upload/v1744543149/cheesecake_cqp8wp.png'),(9,9,'https://res.cloudinary.com/drixmxite/image/upload/v1744543148/apple-pie_mepzhp.png'),(10,10,'https://res.cloudinary.com/drixmxite/image/upload/v1744543149/beef-burger_wlcykh.png'),(11,11,'https://res.cloudinary.com/drixmxite/image/upload/v1744543149/chicken-wings_nehgv2.png'),(12,12,'https://res.cloudinary.com/drixmxite/image/upload/v1744543154/vegetable-salad_glthf4.png'),(13,13,'https://res.cloudinary.com/drixmxite/image/upload/v1744543153/tiramisu_imx9fb.png'),(14,14,'https://res.cloudinary.com/drixmxite/image/upload/v1744543149/coca-cola_e8q1wt.png'),(15,15,'https://res.cloudinary.com/drixmxite/image/upload/v1744543149/borjomi_ze9xw3.png'),(16,16,'https://res.cloudinary.com/drixmxite/image/upload/v1744543150/orange-juice_h0jsjs.png'),(17,17,'https://res.cloudinary.com/drixmxite/image/upload/v1744543152/tea_puibhu.png'),(18,18,'https://res.cloudinary.com/drixmxite/image/upload/v1744543149/coffee_wi3xew.png'),(19,19,'https://res.cloudinary.com/drixmxite/image/upload/v1744543150/milkshake_hgnr9e.png'),(20,20,'https://res.cloudinary.com/drixmxite/image/upload/v1744543150/mojito_m6mdml.png'),(21,1,'https://res.cloudinary.com/drixmxite/image/upload/v1744543150/salat-cesar_fv9wtq.png');
/*!40000 ALTER TABLE `menu_item_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu_items`
--

DROP TABLE IF EXISTS `menu_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `category_id` bigint NOT NULL,
  `price` double NOT NULL,
  `image_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `menu_items_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_items`
--

LOCK TABLES `menu_items` WRITE;
/*!40000 ALTER TABLE `menu_items` DISABLE KEYS */;
INSERT INTO `menu_items` VALUES (1,'Салат Цезарь','Салат с курицей, сыром пармезан и соусом Цезарь',1,250,1),(2,'Борщ','Классический свекольный суп',1,150,2),(3,'Стейк','Мясной стейк средней прожарки',2,500,3),(4,'Торт Наполеон','Сладкое пирожное с кремом',3,120,4),(5,'Спагетти Карбонара','Паста с яичным соусом, беконом и пармезаном',2,350,5),(6,'Лазанья','Слоеное блюдо с мясом и сыром',2,400,6),(7,'Суп Минестроне','Итальянский овощной суп с макаронами',1,180,7),(8,'Чизкейк','Нежный десерт с сыром и основой из печенья',3,250,8),(9,'Пирог с яблоками','Классический домашний пирог с яблоками',3,220,9),(10,'Бургер с говядиной','Сочный бургер с мясом и свежими овощами',2,450,10),(11,'Куриные крылышки','Хрустящие куриные крылышки с соусом',1,300,11),(12,'Овощной салат','Салат из свежих овощей со знакомым соусом',1,200,12),(13,'Тирамису','Традиционный итальянский десерт с кофе',3,280,13),(14,'Кола','Освежающий газированный напиток',4,100,14),(15,'Минеральная вода','Чистая газированная или негазированная вода',4,80,15),(16,'Сок апельсиновый','Сочный апельсиновый сок',4,150,16),(17,'Чай','Чистый черный или зеленый чай',4,120,17),(18,'Кофе','Ароматный свежемолотый кофе',4,160,18),(19,'Милкшейк','Кремовый коктейль из мороженого и молока',4,200,19),(20,'Мохито','Освежающий напиток с мятой и лаймом',4,250,20);
/*!40000 ALTER TABLE `menu_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_item_id` bigint DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `menu_item_id` (`menu_item_id`),
  KEY `order_items_ibfk_2` (`order_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`menu_item_id`) REFERENCES `menu_items` (`id`),
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (3,19,2,2),(4,18,1,2),(5,14,1,2),(8,1,2,4),(9,1,2,5),(10,19,3,6),(11,18,3,6),(12,1,2,7),(13,3,1,7),(14,1,2,8),(15,3,1,8),(16,1,2,9),(17,3,1,9),(18,1,2,10),(19,3,1,10),(20,1,2,11),(21,3,1,11),(22,1,2,12),(23,3,1,12),(24,19,3,13),(25,18,3,13),(26,20,2,14),(27,17,2,14),(28,5,1,15),(29,3,1,15),(30,8,1,15),(31,19,1,16),(32,18,2,16),(33,20,2,16),(34,19,3,17),(35,20,3,17),(36,19,3,18),(37,19,3,19),(38,19,3,20),(39,2,1,21),(40,1,1,21),(41,12,1,21),(42,19,3,22),(43,14,3,23),(44,19,2,23),(45,18,1,23),(46,16,1,23),(47,9,1,23),(48,13,1,23),(49,11,1,23),(50,7,1,23),(51,19,4,24),(52,20,4,24),(53,5,4,25),(54,6,2,25),(55,18,3,26),(56,17,3,26),(57,16,4,26),(58,2,8,27),(59,7,3,28),(60,18,2,28),(61,2,1,29),(62,18,3,29),(63,17,3,29),(64,17,3,30),(65,18,10,30),(66,19,2,31),(67,10,1,32),(68,20,1,32),(69,1,2,33),(70,3,1,33),(71,1,3,34),(72,19,2,34),(73,2,3,35),(74,17,3,35),(75,18,2,35),(76,19,2,35),(77,1,3,36),(78,9,2,36),(79,17,3,36),(80,18,2,37),(81,7,2,38),(82,6,3,38),(83,18,2,39),(84,17,2,39),(85,16,2,39),(86,13,2,39),(87,17,4,40);
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
  `table_number` varchar(255) DEFAULT NULL,
  `order_notes` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `order_time` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','COMPLETED') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (2,'2','два кофе',3,NULL,NULL,'COMPLETED'),(4,'5','Без лука',3,NULL,NULL,'COMPLETED'),(5,'5','Без лука',3,NULL,NULL,'COMPLETED'),(6,'2','принесите заказ по-быстрее',3,NULL,NULL,'ACTIVE'),(7,'5','Без лука',3,NULL,NULL,'ACTIVE'),(8,'5','Без лука',3,NULL,NULL,'ACTIVE'),(9,'5','Без лука',3,NULL,NULL,'ACTIVE'),(10,'5','Без лука',3,NULL,NULL,'ACTIVE'),(11,'5','Без лука',3,NULL,NULL,'ACTIVE'),(12,'5','Без лука',3,NULL,NULL,'ACTIVE'),(13,'2','принесите заказ побыстрее',3,NULL,NULL,'ACTIVE'),(14,'16','быстро!',3,NULL,NULL,'ACTIVE'),(15,'17','аааааааааа',3,NULL,NULL,'COMPLETED'),(16,'13','аааааааааааааа',3,NULL,NULL,'COMPLETED'),(17,'1','побыстрее',3,'2025-04-09 15:22:02','2025-04-09 18:22:02.119204','ACTIVE'),(18,'2','fg',3,'2025-04-09 15:22:32','2025-04-09 18:22:32.069172','COMPLETED'),(19,'2','fg',3,'2025-04-09 15:22:49','2025-04-09 18:22:49.457873','COMPLETED'),(20,'2','fg',3,'2025-04-09 15:25:06','2025-04-09 18:25:06.593263','COMPLETED'),(21,'2','test',3,'2025-04-09 15:27:09','2025-04-09 18:27:09.668577','COMPLETED'),(22,'2','1',3,'2025-04-09 21:02:53','2025-04-10 00:02:53.920401','COMPLETED'),(23,'3','Пожалуйста побыстрее!',38,'2025-04-10 08:31:01','2025-04-10 11:31:01.690019','ACTIVE'),(24,'3','efghjk',38,'2025-04-10 08:33:00','2025-04-10 11:33:00.847704','COMPLETED'),(25,'2','быстрее',3,'2025-04-11 07:47:16','2025-04-11 10:47:16.642852','ACTIVE'),(26,'1','официаааант по быстрееееее!! чаевые дам! :)',3,'2025-04-12 12:37:06','2025-04-12 15:37:06.282869','ACTIVE'),(27,'6','фыавм',3,'2025-04-12 12:37:26','2025-04-12 15:37:26.177144','COMPLETED'),(28,'6','f',3,'2025-04-12 17:41:05','2025-04-12 20:41:05.349295','COMPLETED'),(29,'2','принести побыстрее!',3,'2025-04-13 05:10:11','2025-04-13 08:10:11.802907','ACTIVE'),(30,'1','дам на чай!',3,'2025-04-13 17:24:13','2025-04-13 20:24:13.276689','ACTIVE'),(31,'2','быстро',3,'2025-04-14 17:02:11','2025-04-14 20:02:11.037830','ACTIVE'),(32,'2','',3,'2025-04-15 12:29:48','2025-04-15 15:29:48.533946','ACTIVE'),(33,'5','Без лука',3,'2025-04-15 13:07:51','2025-04-15 16:07:51.288837','ACTIVE'),(34,'2','По быстрее',3,'2025-04-19 20:59:23','2025-04-19 23:59:23.518012','ACTIVE'),(35,'2','По быстрее',3,'2025-04-19 21:08:22','2025-04-20 00:08:22.101966','ACTIVE'),(36,'3','по быстрее, дам чаевые!',3,'2025-04-19 21:15:04','2025-04-20 00:15:04.924600','ACTIVE'),(37,'1','f',3,'2025-05-03 20:30:15','2025-05-03 23:30:14.964032','ACTIVE'),(38,'1','wd',68,'2025-05-03 20:54:42','2025-05-03 23:54:42.165480','COMPLETED'),(39,'3','побыстрее',48,'2025-05-05 05:42:32','2025-05-05 08:42:32.012022','COMPLETED'),(40,'1','По быстрее пожалуйста!',70,'2025-05-22 22:32:10','2025-05-23 01:32:09.988354','COMPLETED');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `table_id` bigint NOT NULL,
  `reservation_time` datetime(6) NOT NULL,
  `number_of_people` int NOT NULL,
  `reservation_end_time` datetime(6) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `FKlmsyoaj81pfgb83w1jhupfq0g` (`table_id`),
  CONSTRAINT `FKlmsyoaj81pfgb83w1jhupfq0g` FOREIGN KEY (`table_id`) REFERENCES `restaurant_tables` (`id`),
  CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`table_id`) REFERENCES `tables` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES (19,45,3,'2025-10-28 19:00:00.000000',3,'2025-10-28 22:00:00.000000','Polina'),(21,3,1,'2025-04-25 14:00:00.000000',2,'2025-04-25 17:00:00.000000','Раул'),(24,51,17,'2222-11-11 19:30:00.000000',6,'2222-11-11 22:30:00.000000','Крокодилдо'),(25,54,1,'2025-04-27 19:30:00.000000',3,'2025-04-27 22:30:00.000000','Оливер'),(34,60,6,'2025-04-29 18:00:00.000000',5,'2025-04-29 21:00:00.000000','Nadya'),(35,63,7,'2025-04-30 17:00:00.000000',2,'2025-04-30 20:00:00.000000','Amina'),(36,64,10,'2025-12-29 16:00:00.000000',5,'2025-12-29 19:00:00.000000','Руслан'),(37,65,10,'2025-06-11 11:00:00.000000',5,'2025-06-11 14:00:00.000000','Рюрик'),(63,48,1,'2025-04-29 19:00:00.000000',2,'2025-04-29 22:00:00.000000','Даниил'),(64,3,2,'2025-04-29 19:00:00.000000',1,'2025-04-29 22:00:00.000000','root'),(66,3,10,'2025-05-18 12:00:00.000000',5,'2025-05-18 15:00:00.000000','Раул'),(67,3,5,'2025-05-05 13:00:00.000000',3,'2025-05-05 16:00:00.000000','Иван'),(68,3,16,'2025-05-05 10:00:00.000000',4,'2025-05-05 13:00:00.000000','Анастасия'),(69,3,12,'2025-05-05 16:00:00.000000',2,'2025-05-05 19:00:00.000000','Михаил'),(70,3,7,'2025-05-05 17:00:00.000000',1,'2025-05-05 20:00:00.000000','Полина'),(71,3,10,'2025-05-05 15:00:00.000000',5,'2025-05-05 18:00:00.000000','Павео'),(72,3,5,'2025-05-06 14:30:00.000000',3,'2025-05-06 17:30:00.000000','Яна'),(73,3,15,'2025-05-06 20:00:00.000000',5,'2025-05-06 23:00:00.000000','Денис'),(74,3,12,'2025-05-05 10:00:00.000000',2,'2025-05-05 13:00:00.000000','Иван'),(75,69,11,'2025-06-15 15:00:00.000000',3,'2025-06-15 18:00:00.000000','Liza');
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restaurant_tables`
--

DROP TABLE IF EXISTS `restaurant_tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant_tables` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `table_number` varchar(255) DEFAULT NULL,
  `seats` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurant_tables`
--

LOCK TABLES `restaurant_tables` WRITE;
/*!40000 ALTER TABLE `restaurant_tables` DISABLE KEYS */;
INSERT INTO `restaurant_tables` VALUES (1,'Table 1',4),(2,'Table 2',2),(3,'Table 3',6),(4,'Table 4',4),(5,'Table 5',3),(6,'Table 6',5),(7,'Table 7',2),(8,'Table 8',6),(9,'Table 9',4),(10,'Table 10',5),(11,'Table 11',3),(12,'Table 12',2),(13,'Table 13',6),(14,'Table 14',4),(15,'Table 15',5),(16,'Table 16',4),(17,'Table 17',6),(18,'Table 18',3),(19,'Table 19',2),(20,'Table 20',5);
/*!40000 ALTER TABLE `restaurant_tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `rating` int NOT NULL,
  `review_text` varchar(500) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6cpw2nlklblpvc7hyt7ko6v3e` (`user_id`),
  CONSTRAINT `FK6cpw2nlklblpvc7hyt7ko6v3e` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,'2025-04-02 00:07:16.855000',5,'Ресторан очень хороший!!! Шев-повар отвечаю такой тактичный!',3),(2,'2025-04-02 00:10:26.829000',5,'Замечательный ресторан!',3),(3,'2025-04-02 00:12:01.140000',5,'супер!!',3),(4,'2025-04-02 00:14:26.154000',5,'Отличный ресторан, очень вкусная еда!',3),(5,'2025-04-02 00:15:42.818000',1,'ужас',3),(6,'2025-04-02 00:28:51.525000',4,'GREAT!',3),(7,'2025-04-02 11:37:08.537000',5,'все СУПЕР ПРОСТО КЛАСС!!!!!',3),(8,'2025-04-02 22:34:00.113000',5,'ВАУУУУУУ, ТАК КРУТО',3),(9,'2025-04-10 10:14:03.514000',5,'ой как классно!',37),(10,'2025-04-10 10:18:30.681000',5,'Обс',38),(11,'2025-04-15 15:32:14.385000',1,'НУ ПРИКОЛ((((',39),(12,'2025-04-20 00:01:25.828000',5,'Все прекрасно, очень понравилось обслуживание!',3),(13,'2025-04-20 00:10:12.930000',5,'Мне все понравилось! оставил чаевые!',3),(14,'2025-04-20 00:17:11.305000',5,'Всё гуд!!',3),(15,'2025-04-22 22:25:28.958000',5,'Отличный ресторан! Вкусная еда и приятная атмосфера. Всем советую!!!',3),(16,'2025-04-23 00:17:07.380000',1,'это просто ужас',46),(17,'2025-04-23 01:05:34.804000',4,'очень понравилось!!!',47),(18,'2025-04-23 14:42:55.666000',4,'Всё понравилось! Выгодные акции!',49),(19,'2025-04-23 17:12:24.123000',5,'?',50),(20,'2025-04-23 17:16:34.895000',5,'?',50),(22,'2025-04-25 01:01:00.266000',5,'Прекрасно провели время с семьей!!! очень красивая веранда',49),(23,'2025-04-25 01:02:15.577000',4,'Остались очень приятные эмоции с посещении данного ресторана! Вес советую',52),(24,'2025-04-25 01:03:41.246000',2,'Остались не приятные эмоции, плохое качество обслуживания клиентов. Не советую...',53),(32,'2025-05-04 15:59:04.339000',5,'Приятные работники. Очень вкусно!!!',3);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ROLE_ADMIN'),(3,'ROLE_CLIENT'),(4,'ROLE_GUEST'),(5,'ROLE_KITCHEN_STAFF'),(2,'ROLE_WAITER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tables`
--

DROP TABLE IF EXISTS `tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tables` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `number` int NOT NULL,
  `capacity` int NOT NULL,
  `status` enum('available','reserved') NOT NULL DEFAULT 'available',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tables`
--

LOCK TABLES `tables` WRITE;
/*!40000 ALTER TABLE `tables` DISABLE KEYS */;
INSERT INTO `tables` VALUES (1,1,4,'available'),(2,2,2,'available'),(3,3,6,'reserved'),(4,4,4,'available'),(5,5,3,'available'),(6,6,5,'reserved'),(7,7,2,'available'),(8,8,6,'available'),(9,9,4,'reserved'),(10,10,5,'available'),(11,11,3,'available'),(12,12,2,'reserved'),(13,13,6,'available'),(14,14,4,'available'),(15,15,5,'reserved'),(16,16,4,'available'),(17,17,6,'reserved'),(18,18,3,'available'),(19,19,2,'available'),(20,20,5,'reserved');
/*!40000 ALTER TABLE `tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (70,2),(1,3),(2,3),(3,3),(16,3),(37,3),(38,3),(39,3),(40,3),(41,3),(42,3),(43,3),(44,3),(45,3),(46,3),(47,3),(48,3),(49,3),(50,3),(51,3),(52,3),(53,3),(54,3),(55,3),(56,3),(57,3),(58,3),(59,3),(60,3),(61,3),(62,3),(63,3),(64,3),(65,3),(66,3),(67,3),(68,3),(69,3);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Иван Иванов','ivan@mail.ru','password123'),(2,'Ольга Петрова','olga@mail.ru','password456'),(3,'Раул','r@gmail.com','$2a$10$KGyifQ2J1zIh9kjRfMHhQOFq6nwdkP9eBauo3h7WLH7nDjrgW5o7O'),(4,'root','root@example.ru','$2a$10$zP2EEk7/xedRIZ0YX7zvG.LPzpyimXeFcGis2KGmKFs1H8hEkBSJ2'),(5,'Александрио','alex@gmail.com','$2a$10$9yguBWRaRv3Gq0t0v6vYfOwqrHk2cWIIEnoMu9NdevrD89xP7yn6y'),(6,'Александрио','alexqewt@gmail.com','$2a$10$p.wcAPbC5xMk7gbHotRW..arClv5PpzirI4mB9JaVcOjIDVK8hiT6'),(7,'dszfhj','zdzghcb@adgsh.com','$2a$10$NYrvYJ.ytkuVX/pseyeOqOv/uKi/yGQPHue.9cgAphYV5NJfjBn/y'),(8,'TestUser','test@example.com','$2a$10$/JVbvi78Ros.gTtTyXBjgOU7Qc36OIQFIPIrgdQcnXdglinqcsdPy'),(9,'oliver','oliver@gmail.com','$2a$10$Spf5r9MdX3sHxzRNn5EqtuCPRXH5v.pzXNaWPZ5p8Sh56/sl4V7s2'),(10,'sdfgdh','sfdgnf@sDB','$2a$10$yIZR7I9cqQzY4kURzub86.cSFU.2gxLOTZB4mudnR4PZk2AgWJAMK'),(11,'Pasha','p@g.c','$2a$10$Eui2PyujLB94qBSC4jj/xeLRjC.TRf7.n7c4LrQS3HzdjDs6tIr8i'),(15,'rrr','rrr@rr.com','$2a$10$01oNUSNCaJJcETRAQmF7N.vMb5i83o53EMhqD55Je3sR/yiDKyH2.'),(16,'aaa','aaa@gmail.com','$2a$10$UscQu3QSS4SAC3JG7XMC..n8DETLLtHSgStnZ7HUcbu4WM0bSua5m'),(37,'дарья','dar@mail.ru','$2a$10$0e57xlbHLUfYsv/glU0ZW.AMEwwYoi2EMRPByt25Be/CxgIiTLsOu'),(38,'Кесс','Kessy@gmail.com','$2a$10$T27aKP1zTDMuX3lV9L0Lh.M.iuv9E.e/qAjaQpwp7zaF1DmZ1RjT.'),(39,'Сергей','ser@gmail.com','$2a$10$F1fS2GK9BHi7jirjpb.WvePiq.RapVCxIuEXrkK6B8dNiFSVKjn2e'),(40,'Раул','raulmusaev3@gmail.com','$2a$10$pH58YkeP9LIhUwl6SKjDZuyMJJxHjVvQFk3Kn6tkGCuCFzPRQxVf.'),(41,'Надя','nadia@gmail.com','$2a$10$jK7Ijr.R8rtAeabVNNDed.qlHoTDYNbTzxlx7Pt51PX7tkuP7M7.G'),(42,'Раул','raulmusaev2020@gmail.com','$2a$10$Vy./PCFg9cLHdTGJe1a5WeXD8qYNocYf1HnIDNGkeFH.MqHHlsA.m'),(43,'Bo','us@gmail.com','$2a$10$uJqEQMV7GT5gIhi5EcYF9eCXpT68HVTZQ0KFGTt/lutjwXfO3gc36'),(44,'Коля','rgd@mail.ru','$2a$10$2E7mBNnYRKCBxvhjemrXvejQpuxaz3aXMBL3NxqmZY1LKo494zi7e'),(45,'Полина','polly001@mail.ru','$2a$10$UiC0GWYFSUY5qsXmEq2IguZjRJSEq6QBaQz/gje5s1JQJW.hNaWIe'),(46,'Иван','user@example.com','$2a$10$yxtEZGE1IsvDM5M1X/G1AegoFcTu6l32jL0IljSJwWzT3.ncS7a4m'),(47,'Коля','kolya12@gmail.com','$2a$10$ciQdA.Qh9p4b54odfLZ5Peb8aZ3YCRm9KFb0r5cRRi/44Zh7Syaje'),(48,'admin','admin@gmail.com','$2a$10$tyE4QVRbDwMAZKbt45.JQumeUvIXVh.4g42xLrAPwqFL08codS.j.'),(49,'Сергей','ser@mail.ru','$2a$10$2m3DZvWFVaCGlQq9XVqmpe/eDfJXPvCVDyzluHpH8pfpyOaBw/4Uq'),(50,'Раул','use@example.com','$2a$10$rV1Fkmij.KIt44AI/d.fjeE4gfl6C9EN0KfNF4p1luJumEi9sa/IS'),(51,'КрокодилдоПеннисини','crocodildo@example.com','$2a$10$q.bL5ACPpWpqnYqbipMqnu/IGNaGtS0Jz0jzwUDe7/2WCS.slcCxy'),(52,'Вероника','veronica@mail.ru','$2a$10$QGM0FLvcn7FWicqel.atM.fJRYAr.2q.t/5pqGj2on8/EQdpxBEBy'),(53,'София','sofia@gmail.com','$2a$10$803E/9InwQhd5LSPBXYeRO8VJV/SeSwUDYjOWK1ua/7QdxQbdyu6C'),(54,'Oliver','oliver1@gmail.com','$2a$10$.4m3MAvbWVwSEak50dLTxuUde7TmLCipSV3mpBrLRglUiGFo55qIK'),(55,'Трипитропа','g@exmp.com','$2a$10$8dUCNHg8qMFmj7/t6IM5kebPT0FS058n6OFCffw4nOPT0/T1iy2ym'),(56,'Nadya','nadya@yandex','$2a$10$PIIKJfu/8EOXyktcEUJsxe4BP4LUcJXCezwLA7Ij3D1xQZ7Ln2I6a'),(57,'Влад','andr@petukh.com','$2a$10$ilb1d3JoDVW3DK8bmN1a.O.QTpAfkIAKMy2VQngXC5auRW0SEmgb.'),(58,'Егор','ekudinov888@gmail.com','$2a$10$rzE/zdIHcueJ1S6mJANO4OrOZmcRL6U8U3jv.iu3uQPTMQHqWPvsW'),(59,'Олег','olegaaaa@gmail.com','$2a$10$EZv78fRiaJ2z0yJ4rbtl9ONlfo.FzEIsXS1PT.XSctlY93HafgTUm'),(60,'Nadya','nadya@yandex.ru','$2a$10$giz19NVC7qbK9JnT50Jo4.zMWCdtyyA6S3CWK1JjpyxNJXYe3hTAa'),(61,'Игорь','c@example.com','$2a$10$29fleF/BZ5prNA08PJiE.OVZH9QylyQj6NtGWPDhwGZHYcf/XSZ62'),(62,'Семыч','aboba@gmail.com','$2a$10$RQGZv.zHPyNDkQjAqj0Iz.BzFDHBL9zxl0N/1lGOnYaWUARxB.VpK'),(63,'Amina','aminaakbolatova@yandex.ru','$2a$10$UAho.xxPetDA.oD4Bkcw0Obck/WUtqUZvaXCvkAN2kAVkUojnNzWi'),(64,'Руслан','rus@mail.ru','$2a$10$wbc2g6AsOCU6PtYzUvXivekuEJtscIWTBdhrdAOWy4C1vAsnZBRCu'),(65,'Андрей','artdrinov@gmail.com','$2a$10$XFj5BA4dmbhQIakmikSmoe.JF9kFtQ0.wZgRE54gFpMnXRIsD1jta'),(66,'oleg','ol@mail.ru','$2a$10$2zaRMtgG73bF1fxmVhmdW.nJytJt1OWu5IWQDuWKK28Czf0sSZnC6'),(67,'Максим','max.ch@mail.ru','$2a$10$0HWs9PsG0dx36oZ2ktrjyeRAooqaZWCUAZXkBS1hnHsIP6Z86dWR.'),(68,'qq','qq@dd.com','$2a$10$nkoNQcyPjX1KIRfajeUoVu7Wy1kW.F4QAVAQhN4mE/YBlrQy1.Kta'),(69,'Гена','fygdetvvjyxgk@mail.ru','$2a$10$yxeqfd1HJkrGh.PqCI213e8LSXru7V8pJULzihKpdfJwk9daxmgO6'),(70,'manager','manager@gmail.com','$2a$10$8ndrXuvKXFD4YJd7eWRLheNr0Pn/7i0RBje72vATjfdJXwEJxnU3u');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-23  1:48:12
