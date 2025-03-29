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
  `image_url` varchar(255) DEFAULT NULL,
  `image_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `menu_items_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_items`
--

LOCK TABLES `menu_items` WRITE;
/*!40000 ALTER TABLE `menu_items` DISABLE KEYS */;
INSERT INTO `menu_items` VALUES (1,'Салат Цезарь','Салат с курицей, сыром пармезан и соусом Цезарь',1,250,NULL,1),(2,'Борщ','Классический свекольный суп',1,150,NULL,2),(3,'Стейк','Мясной стейк средней прожарки',2,500,NULL,3),(4,'Торт Наполеон','Сладкое пирожное с кремом',3,120,NULL,4),(5,'Спагетти Карбонара','Паста с яичным соусом, беконом и пармезаном',2,350,NULL,5),(6,'Лазанья','Слоеное блюдо с мясом и сыром',2,400,NULL,6),(7,'Суп Минестроне','Итальянский овощной суп с макаронами',1,180,NULL,7),(8,'Чизкейк','Нежный десерт с сыром и основой из печенья',3,250,NULL,8),(9,'Пирог с яблоками','Классический домашний пирог с яблоками',3,220,NULL,9),(10,'Бургер с говядиной','Сочный бургер с мясом и свежими овощами',2,450,NULL,10),(11,'Куриные крылышки','Хрустящие куриные крылышки с соусом',1,300,NULL,11),(12,'Овощной салат','Салат из свежих овощей со знакомым соусом',1,200,NULL,12),(13,'Тирамису','Традиционный итальянский десерт с кофе',3,280,NULL,13),(14,'Кола','Освежающий газированный напиток',4,100,NULL,14),(15,'Минеральная вода','Чистая газированная или негазированная вода',4,80,NULL,15),(16,'Сок апельсиновый','Сочный апельсиновый сок',4,150,NULL,16),(17,'Чай','Чистый черный или зеленый чай',4,120,NULL,17),(18,'Кофе','Ароматный свежемолотый кофе',4,160,NULL,18),(19,'Милкшейк','Кремовый коктейль из мороженого и молока',4,200,NULL,19),(20,'Мохито','Освежающий напиток с мятой и лаймом',4,250,NULL,20);
/*!40000 ALTER TABLE `menu_items` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-30  0:00:20
