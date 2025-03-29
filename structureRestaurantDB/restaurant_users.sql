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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Иван Иванов','ivan@mail.ru','password123'),(2,'Ольга Петрова','olga@mail.ru','password456'),(3,'Раул','r@gmail.com','$2a$10$KGyifQ2J1zIh9kjRfMHhQOFq6nwdkP9eBauo3h7WLH7nDjrgW5o7O'),(4,'root','root@example.ru','$2a$10$zP2EEk7/xedRIZ0YX7zvG.LPzpyimXeFcGis2KGmKFs1H8hEkBSJ2'),(5,'Александрио','alex@gmail.com','$2a$10$9yguBWRaRv3Gq0t0v6vYfOwqrHk2cWIIEnoMu9NdevrD89xP7yn6y'),(6,'Александрио','alexqewt@gmail.com','$2a$10$p.wcAPbC5xMk7gbHotRW..arClv5PpzirI4mB9JaVcOjIDVK8hiT6'),(7,'dszfhj','zdzghcb@adgsh.com','$2a$10$NYrvYJ.ytkuVX/pseyeOqOv/uKi/yGQPHue.9cgAphYV5NJfjBn/y'),(8,'TestUser','test@example.com','$2a$10$/JVbvi78Ros.gTtTyXBjgOU7Qc36OIQFIPIrgdQcnXdglinqcsdPy'),(9,'oliver','oliver@gmail.com','$2a$10$Spf5r9MdX3sHxzRNn5EqtuCPRXH5v.pzXNaWPZ5p8Sh56/sl4V7s2'),(10,'sdfgdh','sfdgnf@sDB','$2a$10$yIZR7I9cqQzY4kURzub86.cSFU.2gxLOTZB4mudnR4PZk2AgWJAMK'),(11,'Pasha','p@g.c','$2a$10$Eui2PyujLB94qBSC4jj/xeLRjC.TRf7.n7c4LrQS3HzdjDs6tIr8i');
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

-- Dump completed on 2025-03-30  0:00:21
