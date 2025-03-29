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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_item_images`
--

LOCK TABLES `menu_item_images` WRITE;
/*!40000 ALTER TABLE `menu_item_images` DISABLE KEYS */;
INSERT INTO `menu_item_images` VALUES (1,1,'/images-menu/salat-cesar.png'),(2,2,'/images-menu/Borsch.png'),(3,3,'/images-menu/steak.png'),(4,4,'/images-menu/tort-napoleon.png'),(5,5,'/images-menu/pasta-carbonara.png'),(6,6,'/images-menu/lasagna.png'),(7,7,'/images-menu/minestrone-soup.png'),(8,8,'/images-menu/cheesecake.png'),(9,9,'/images-menu/apple-pie.png'),(10,10,'/images-menu/beef-burger.png'),(11,11,'/images-menu/chicken-wings.png'),(12,12,'/images-menu/vegetable-salad.png'),(13,13,'/images-menu/tiramisu.png'),(14,14,'/images-menu/coca-cola.png'),(15,15,'/images-menu/borjomi.png'),(16,16,'/images-menu/orange-juice.png'),(17,17,'/images-menu/tea.png'),(18,18,'/images-menu/coffee.png'),(19,19,'/images-menu/milkshake.png'),(20,20,'/images-menu/mojito.png');
/*!40000 ALTER TABLE `menu_item_images` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-30  0:00:21
