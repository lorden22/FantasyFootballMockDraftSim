-- MariaDB dump 10.19  Distrib 10.5.23-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: db
-- ------------------------------------------------------
-- Server version       10.5.23-MariaDB-0+deb11u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `db`;

--
-- Table structure for table `drafts`
--

DROP TABLE IF EXISTS `drafts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drafts` (
  `draft_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `complete_status` tinyint(4) DEFAULT 0,
  `num_teams` int(11) NOT NULL,
  `curr_round` int(11) NOT NULL,
  `curr_pick` int(11) NOT NULL,
  `date` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`draft_id`),
  KEY `fk_username` (`username`),
  CONSTRAINT `fk_username` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drafts`
--

LOCK TABLES `drafts` WRITE;
/*!40000 ALTER TABLE `drafts` DISABLE KEYS */;
INSERT INTO `drafts` VALUES (1,'a',1,14,16,1,'2024-06-25 17:39:47');
/*!40000 ALTER TABLE `drafts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `players` (
  `player_rank` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `position` varchar(3) NOT NULL,
  `predicted_score` decimal(5,2) NOT NULL,
  PRIMARY KEY (`player_rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `players`
--

LOCK TABLES `players` WRITE;
/*!40000 ALTER TABLE `players` DISABLE KEYS */;
INSERT INTO `players` VALUES (1,'Christian McCaffrey','RB',338.00),(2,'Jonathan Taylor','RB',334.00),(3,'Austin Ekeler','RB',325.00),(4,'Dalvin Cook','RB',265.30),(5,'Justin Jefferson','WR',311.90),(6,'Cooper Kupp','WR',357.30),(7,'Najee Harris','RB',274.20),(8,'Derrick Henry','RB',292.40),(9,'D\'Andre Swift','RB',255.10),(10,'Travis Kelce','TE',272.00),(11,'Ja\'Marr Chase','WR',301.10),(12,'Joe Mixon','RB',272.30),(13,'Stefon Diggs','WR',280.20),(14,'Davante Adams','WR',293.10),(15,'Aaron Jones','RB',259.30),(16,'Saquon Barkley','RB',252.60),(17,'Alvin Kamara','RB',278.90),(18,'CeeDee Lamb','WR',264.40),(19,'Mark Andrews','TE',268.40),(20,'Leonard Fournette','RB',261.90),(21,'Tyreek Hill','WR',262.30),(22,'Keenan Allen','WR',248.60),(23,'Mike Evans','WR',256.50),(24,'Javonte Williams','RB',241.90),(25,'Deebo Samuel','WR',277.10),(26,'Michael Pittman Jr.','WR',245.70),(27,'Nick Chubb','RB',222.00),(28,'A.J. Brown','WR',239.60),(29,'Tee Higgins','WR',250.90),(30,'DJ Moore','WR',238.40),(31,'James Conner','RB',245.10),(32,'Kyle Pitts','TE',199.90),(33,'Ezekiel Elliott','RB',221.10),(34,'Josh Allen','QB',401.30),(35,'Diontae Johnson','WR',237.60),(36,'Travis Etienne Jr.','RB',186.80),(37,'Breece Hall','RB',211.30),(38,'Jaylen Waddle','WR',229.00),(39,'David Montgomery','RB',230.60),(40,'Terry McLaurin','WR',220.40),(41,'Cam Akers','RB',211.30),(42,'Justin Herbert','QB',355.90),(43,'Mike Williams','WR',231.30),(44,'Brandin Cooks','WR',243.10),(45,'Courtland Sutton','WR',210.90),(46,'Patrick Mahomes II','QB',372.80),(47,'Allen Robinson II','WR',201.90),(48,'George Kittle','TE',189.90),(49,'DK Metcalf','WR',224.30),(50,'J.K. Dobbins','RB',208.00),(51,'Lamar Jackson','QB',352.40),(52,'Darren Waller','TE',199.50),(53,'Josh Jacobs','RB',209.30),(54,'AJ Dillon','RB',181.00),(55,'Elijah Mitchell','RB',198.00),(56,'Michael Thomas','WR',222.80),(57,'Marquise Brown','WR',205.90),(58,'Darnell Mooney','WR',213.00),(59,'Jerry Jeudy','WR',212.20),(60,'Antonio Gibson','RB',196.30),(61,'Amon-Ra St. Brown','WR',224.60),(62,'Rashod Bateman','WR',196.20),(63,'Chris Godwin','WR',214.20),(64,'Kyler Murray','QB',351.10),(65,'Dalton Schultz','TE',194.10),(66,'Chase Edmonds','RB',171.20),(67,'Amari Cooper','WR',203.80),(68,'JuJu Smith-Schuster','WR',206.00),(69,'Jalen Hurts','QB',349.60),(70,'Clyde Edwards-Helaire','RB',183.30),(71,'Miles Sanders','RB',195.80),(72,'Kareem Hunt','RB',169.10),(73,'Elijah Moore','WR',202.40),(74,'Tony Pollard','RB',188.60),(75,'Joe Burrow','QB',323.80),(76,'Adam Thielen','WR',203.80),(77,'Damien Harris','RB',190.20),(78,'T.J. Hockenson','TE',166.80),(79,'Gabriel Davis','WR',209.00),(80,'Rashaad Penny','RB',167.50),(81,'Devin Singletary','RB',181.20),(82,'Rhamondre Stevenson','RB',173.60),(83,'Dallas Goedert','TE',172.40),(84,'Cordarrelle Patterson','RB',190.00),(85,'Tom Brady','QB',337.60),(86,'Russell Wilson','QB',306.70),(87,'Dak Prescott','QB',336.80),(88,'Melvin Gordon III','RB',153.30),(89,'DeVonta Smith','WR',181.90),(90,'Hunter Renfrow','WR',208.80),(91,'Brandon Aiyuk','WR',178.50),(92,'Drake London','WR',185.60),(93,'Zach Ertz','TE',158.70),(94,'Trey Lance','QB',315.10),(95,'Ken Walker III','RB',134.30),(96,'Tyler Lockett','WR',207.80),(97,'Christian Kirk','WR',185.80),(98,'Matthew Stafford','QB',316.00),(99,'Allen Lazard','WR',172.60),(100,'DeAndre Hopkins','WR',165.60),(101,'Aaron Rodgers','QB',315.50),(102,'Robert Woods','WR',198.10),(103,'Kadarius Toney','WR',169.40),(104,'James Cook','RB',138.00),(105,'Pat Freiermuth','TE',148.20),(106,'Michael Carter','RB',132.50),(107,'Nyheim Hines','RB',124.50),(108,'Cole Kmet','TE',145.60),(109,'Dameon Pierce','RB',140.10),(110,'Dawson Knox','TE',145.20),(111,'Kirk Cousins','QB',298.70),(112,'Darrell Henderson Jr.','RB',123.00),(113,'Chase Claypool','WR',182.10),(114,'Derek Carr','QB',295.30),(115,'Treylon Burks','WR',177.90),(116,'Chris Olave','WR',153.20),(117,'Alexander Mattison','RB',111.90),(118,'James Robinson','RB',149.40),(119,'Jakobi Meyers','WR',177.00),(120,'Tyler Boyd','WR',164.40),(121,'Kenneth Gainwell','RB',121.40),(122,'J.D. McKissic','RB',125.50),(123,'Garrett Wilson','WR',152.90),(124,'Russell Gage','WR',166.20),(125,'Skyy Moore','WR',147.20),(126,'Mike Gesicki','TE',152.00),(127,'Jarvis Landry','WR',158.90),(128,'Tua Tagovailoa','QB',280.30),(129,'Hunter Henry','TE',151.70),(130,'DeVante Parker','WR',159.90),(131,'Rondale Moore','WR',136.00),(132,'Marquez Valdes-Scantling','WR',150.90),(133,'Justin Fields','QB',270.10),(134,'Jamaal Williams','RB',118.60),(135,'Noah Fant','TE',148.30),(136,'Isaiah Spiller','RB',105.50),(137,'Irv Smith Jr.','TE',132.80),(138,'Kenny Golladay','WR',161.40),(139,'Khalil Herbert','RB',86.60),(140,'DJ Chark Jr.','WR',140.10),(141,'Albert Okwuegbunam','TE',126.80),(142,'Trevor Lawrence','QB',275.10),(143,'Raheem Mostert','RB',110.90),(144,'Michael Gallup','WR',149.40),(145,'Tyler Allgeier','RB',103.70),(146,'George Pickens','WR',140.70),(147,'Julio Jones','WR',129.10),(148,'Gerald Everett','TE',137.20),(149,'Tyler Higbee','TE',145.80),(150,'Jahan Dotson','WR',138.10),(151,'David Njoku','TE',138.80),(152,'Robert Tonyan','TE',124.60),(153,'Rachaad White','RB',101.80),(154,'Jameis Winston','QB',257.90),(155,'Evan Engram','TE',0.00),(156,'Buffalo Bills','DST',113.60),(157,'Corey Davis','WR',138.70),(158,'Brian Robinson Jr.','RB',101.10),(159,'Tampa Bay Buccaneers','DST',114.90),(160,'Matt Ryan','QB',261.80),(161,'Marlon Mack','RB',116.70),(162,'Robbie Anderson','WR',156.10),(163,'Nico Collins','WR',134.30),(164,'Austin Hooper','TE',124.10),(165,'Darrel Williams','RB',92.40),(166,'San Francisco 49ers','DST',109.00),(167,'Ronald Jones II','RB',74.30),(168,'Mecole Hardman','WR',166.10),(169,'Gus Edwards','RB',96.90),(170,'Mark Ingram II','RB',105.00),(171,'Indianapolis Colts','DST',106.80),(172,'Van Jefferson','WR',133.50),(173,'Joshua Palmer','WR',127.10),(174,'Jameson Williams','WR',105.40),(175,'Ryan Tannehill','QB',277.40),(176,'Marvin Jones Jr.','WR',151.20),(177,'Sony Michel','RB',65.50),(178,'Justin Tucker','K',129.80),(179,'D\'Onta Foreman','RB',68.30),(180,'Logan Thomas','TE',126.30),(181,'K.J. Osborn','WR',130.10),(182,'New Orleans Saints','DST',109.50),(183,'Denver Broncos','DST',102.50),(184,'New England Patriots','DST',105.70),(185,'Mac Jones','QB',258.50),(186,'Los Angeles Rams','DST',110.80),(187,'Tyler Bass','K',126.70),(188,'Daniel Jones','QB',263.80),(189,'Hayden Hurst','TE',109.10),(190,'Christian Watson','WR',107.70),(191,'Matt Gay','K',128.90),(192,'Parris Campbell','WR',128.90),(193,'Jamison Crowder','WR',113.20),(194,'Wan\'Dale Robinson','WR',107.20),(195,'KJ Hamler','WR',110.10),(196,'Los Angeles Chargers','DST',108.70),(197,'Zamir White','RB',69.00),(198,'Dallas Cowboys','DST',112.40),(199,'Jalen Tolbert','WR',152.20),(200,'Daniel Carlson','K',129.30),(201,'Curtis Samuel','WR',128.10),(202,'Kendrick Bourne','WR',114.00),(203,'Evan McPherson','K',124.50),(204,'Green Bay Packers','DST',112.50),(205,'Donovan Peoples-Jones','WR',127.30),(206,'Carson Wentz','QB',253.10),(207,'Miami Dolphins','DST',109.40),(208,'Kenyan Drake','RB',93.60),(209,'Zach Wilson','QB',240.50),(210,'Sterling Shepard','WR',110.00),(211,'Kansas City Chiefs','DST',104.70),(212,'David Bell','WR',75.60),(213,'Jared Goff','QB',247.80),(214,'A.J. Green','WR',104.40),(215,'Brevin Jordan','TE',100.40),(216,'Laviska Shenault Jr.','WR',101.50),(217,'Ryan Succop','K',127.30),(218,'Chuba Hubbard','RB',54.20),(219,'Matt Prater','K',125.30),(220,'Harrison Butker','K',129.40),(221,'Cleveland Browns','DST',105.30),(222,'Samaje Perine','RB',67.20),(223,'Isaiah McKenzie','WR',74.90),(224,'Baltimore Ravens','DST',101.80),(225,'Boston Scott','RB',62.00),(226,'Alec Pierce','WR',108.30),(227,'Philadelphia Eagles','DST',99.30),(228,'Pittsburgh Steelers','DST',113.30),(229,'Jonnu Smith','TE',74.60),(230,'Isiah Pacheco','RB',78.40),(231,'Rodrigo Blankenship','K',122.80),(232,'Younghoe Koo','K',116.30),(233,'Terrace Marshall Jr.','WR',80.40),(234,'Tyrion Davis-Price','RB',54.60),(235,'Damien Williams','RB',78.90),(236,'Jason Sanders','K',114.40),(237,'Trey Sermon','RB',22.50),(238,'Baker Mayfield','QB',238.20),(239,'D\'Ernest Johnson','RB',44.40),(240,'Nick Folk','K',123.90),(241,'Jerick McKinnon','RB',86.10),(242,'Brandon McManus','K',121.50),(243,'Davis Mills','QB',231.70),(244,'Braxton Berrios','WR',90.40),(245,'Byron Pringle','WR',101.10),(246,'Arizona Cardinals','DST',103.80),(247,'Adam Trautman','TE',89.70),(248,'Romeo Doubs','WR',84.60),(249,'Zay Jones','WR',106.00),(250,'Robbie Gould','K',122.20),(251,'Jeff Wilson Jr.','RB',42.30),(252,'Mo Alie-Cox','TE',96.30),(253,'Myles Gaskin','RB',29.30),(254,'Jake Elliott','K',115.90),(255,'Cincinnati Bengals','DST',101.90),(256,'Bryan Edwards','WR',103.60),(257,'Mike Davis','RB',38.20),(258,'Dustin Hopkins','K',120.50),(259,'Rex Burkhead','RB',104.80),(260,'Tennessee Titans','DST',102.00),(261,'Carolina Panthers','DST',99.80),(262,'Randall Cobb','WR',121.50),(263,'Zack Moss','RB',28.90),(264,'Dan Arnold','TE',71.50),(265,'Deshaun Watson','QB',132.10),(266,'C.J. Uzomah','TE',89.20),(267,'Greg Zuerlein','K',101.80),(268,'Minnesota Vikings','DST',105.50),(269,'Sammy Watkins','WR',103.60),(270,'William Fuller V','WR',23.00),(271,'Wil Lutz','K',115.80),(272,'Chris Boswell','K',119.70),(273,'Cedrick Wilson Jr.','WR',101.50),(274,'Odell Beckham Jr.','WR',24.00),(275,'Marquez Callaway','WR',54.80),(276,'Cameron Brate','TE',84.50),(277,'Chicago Bears','DST',101.60),(278,'Mason Crosby','K',124.80),(279,'Marcus Mariota','QB',212.70),(280,'Nelson Agholor','WR',85.30),(281,'Eno Benjamin','RB',38.40),(282,'Matt Breida','RB',63.20),(283,'Washington Commanders','DST',101.80),(284,'Jason Myers','K',103.70),(285,'Graham Gano','K',112.70),(286,'Greg Joseph','K',126.50),(287,'Mitch Trubisky','QB',185.60),(288,'Tre\'Quan Smith','WR',16.50),(289,'O.J. Howard','TE',60.30),(290,'Trey McBride','TE',49.80),(291,'New York Giants','DST',97.20),(292,'Randy Bullock','K',116.60),(293,'Cairo Santos','K',112.40),(294,'Chris Evans','RB',44.90),(295,'Hassan Haskins','RB',56.10),(296,'Seattle Seahawks','DST',94.50),(297,'Josh Reynolds','WR',60.60),(298,'New York Jets','DST',93.20),(299,'Ty Johnson','RB',9.10),(300,'Quez Watkins','WR',72.10),(301,'Darius Slayton','WR',51.30),(302,'Kyle Rudolph','TE',87.60),(303,'Ka\'imi Fairbairn','K',103.20),(304,'Ke\'Shawn Vaughn','RB',42.00),(305,'Phillip Lindsay','RB',38.90),(306,'Jalen Reagor','WR',16.40),(307,'Giovani Bernard','RB',28.90),(308,'Joshua Kelley','RB',40.60),(309,'Jacksonville Jaguars','DST',93.70),(310,'Kyren Williams','RB',18.40),(311,'Pierre Strong Jr.','RB',20.60),(312,'Dyami Brown','WR',34.80),(313,'Velus Jones Jr.','WR',87.20),(314,'Quintez Cephus','WR',40.50),(315,'Emmanuel Sanders','WR',0.00),(316,'Darrynton Evans','RB',12.10),(317,'Greg Dulcich','TE',47.50),(318,'Justin Jackson','RB',43.00),(319,'Devin Duvernay','WR',107.90),(320,'Salvon Ahmed','RB',0.00),(321,'Drew Lock','QB',155.30),(322,'Daniel Bellinger','TE',57.60),(323,'Kenny Pickett','QB',93.70),(324,'Cole Beasley','WR',0.00),(325,'Tyler Conklin','TE',75.50),(326,'Duke Johnson Jr.','RB',31.00),(327,'Nick Westbrook-Ikhine','WR',110.10),(328,'Tyler Badie','RB',17.40),(329,'Harrison Bryant','TE',73.10),(330,'Chase McLaughlin','K',0.00),(331,'Jimmy Garoppolo','QB',16.30),(332,'Jerome Ford','RB',15.40),(333,'Tommy Tremble','TE',68.50),(334,'Dontrell Hilliard','RB',77.00),(335,'Jacoby Brissett','QB',144.70),(336,'Josh Lambo','K',0.00),(337,'Tevin Coleman','RB',35.80),(338,'Snoop Conner','RB',18.60),(339,'Kyle Philips','WR',16.90),(340,'Tyquan Thornton','WR',22.70),(341,'Jared Cook','TE',0.00),(342,'Ricky Seals-Jones','TE',55.40),(343,'Geno Smith','QB',92.50),(344,'Anthony Firkser','TE',51.50),(345,'David Johnson','RB',0.00),(346,'Jaret Patterson','RB',20.20),(347,'Donald Parham Jr.','TE',54.40),(348,'Malcolm Brown','RB',0.00),(349,'Jalen Guyton','WR',67.70),(350,'T.Y. Hilton','WR',0.00),(351,'James Proche II','WR',67.90),(352,'Ameer Abdullah','RB',50.60),(353,'Sam Darnold','QB',21.70),(354,'Cade York','K',108.90),(355,'Jelani Woods','TE',55.90),(356,'Amari Rodgers','WR',72.30),(357,'Las Vegas Raiders','DST',93.40),(358,'Devontae Booker','RB',0.00),(359,'Latavius Murray','RB',0.00),(360,'Khalil Shakir','WR',17.70),(361,'Calvin Austin III','WR',36.70),(362,'Ty Montgomery','RB',36.00),(363,'Demarcus Robinson','WR',66.90),(364,'Keaontay Ingram','RB',18.80),(365,'Cade Otton','TE',49.70),(366,'Detroit Lions','DST',92.50),(367,'John Metchie III','WR',0.00),(368,'Desmond Ridder','QB',81.10),(369,'James Washington','WR',86.10),(370,'Atlanta Falcons','DST',87.60),(371,'Olamide Zaccheaus','WR',80.70),(372,'Devonta Freeman','RB',0.00),(373,'Danny Gray','WR',19.50),(374,'Jaylen Warren','RB',5.70),(375,'Foster Moreau','TE',65.60),(376,'John Bates','TE',44.90),(377,'Wayne Gallman Jr.','RB',0.00),(378,'Laquon Treadwell','WR',44.50),(379,'DeeJay Dallas','RB',32.10),(380,'Tyler Johnson','WR',24.40),(381,'Kylen Granson','TE',47.20),(382,'Anthony Schwartz','WR',42.70),(383,'Joey Slye','K',109.70),(384,'Jauan Jennings','WR',76.70),(385,'Rashard Higgins','WR',49.60),(386,'Kene Nwangwu','RB',19.10),(387,'Houston Texans','DST',93.70),(388,'Jonathan Garibay','K',0.00),(389,'Josiah Deguara','TE',46.20),(390,'Dee Eskridge','WR',67.30),(391,'Noah Brown','WR',38.90),(392,'Tutu Atwell','WR',24.30),(393,'Chris Conley','WR',90.00),(394,'Equanimeous St. Brown','WR',36.30),(395,'Derrick Gore','RB',14.70),(396,'Antonio Brown','WR',0.00),(397,'Pharaoh Brown','TE',54.10),(398,'Isaiah Likely','TE',40.00),(399,'Trestan Ebner','RB',10.30),(400,'Tylan Wallace','WR',44.70),(401,'Zane Gonzalez','K',107.80),(402,'Tristan Vizcaino','K',0.00),(403,'Brett Maher','K',92.50),(404,'Deonte Harty','WR',15.30),(405,'Abram Smith','RB',15.70),(406,'Eric Ebron','TE',0.00),(407,'Benny Snell Jr.','RB',26.10),(408,'Austin Seibert','K',112.40),(409,'Travis Homer','RB',34.20),(410,'Craig Reynolds','RB',33.00),(411,'Taysom Hill','QB',19.60),(412,'Ashton Dulin','WR',23.40),(413,'Zach Pascal','WR',33.20),(414,'Teddy Bridgewater','QB',15.50),(415,'Brandon Bolden','RB',46.80),(416,'Ryan Santoso','K',99.80),(417,'Anthony McFarland Jr.','RB',27.40),(418,'Jeremy Ruckert','TE',9.00),(419,'Juwan Johnson','TE',15.00),(420,'Tony Jones Jr.','RB',19.60),(421,'Kyle Juszczyk','RB',59.70),(422,'Riley Patterson','K',95.80),(423,'Will Dissly','TE',40.10),(424,'Keelan Cole Sr.','WR',62.00),(425,'Blake Jarwin','TE',0.00),(426,'Ihmir Smith-Marsette','WR',30.40),(427,'Tarik Cohen','RB',0.00),(428,'Ben Skowronek','WR',41.60),(429,'JaMycal Hasty','RB',18.90),(430,'Carlos Hyde','RB',0.00),(431,'Malik Willis','QB',9.80),(432,'Jordan Howard','RB',0.00),(433,'Jamal Agnew','WR',37.50),(434,'Mack Hollins','WR',14.10),(435,'Chris Herndon IV','TE',9.90),(436,'Auden Tate','WR',17.00),(437,'N\'Keal Harry','WR',15.50),(438,'Freddie Swain','WR',66.70),(439,'Demetric Felton Jr.','RB',24.00),(440,'Phillip Dorsett II','WR',53.30),(441,'Kevin Harris','RB',9.20),(442,'Jake Ferguson','TE',31.90),(443,'Denzel Mims','WR',8.10),(444,'Qadree Ollison','RB',16.10),(445,'Tyler Huntley','QB',18.80),(446,'Geoff Swaim','TE',64.00),(447,'Antoine Wesley','WR',29.80),(448,'Kalif Raymond','WR',33.90),(449,'Quinn Nordin','K',0.00),(450,'Ian Thomas','TE',42.80),(451,'Ty Chandler','RB',13.10),(452,'Jordan Akins','TE',46.50),(453,'Justin Watson','WR',2.50),(454,'Simi Fehoko','WR',17.50),(455,'Le\'Veon Bell','RB',0.00),(456,'Kylin Hill','RB',13.50),(457,'Ryquell Armstead','RB',27.00),(458,'Carlos Martinez','K',0.00),(459,'Jeremy McNichols','RB',0.00),(460,'Justyn Ross','WR',0.00),(461,'Breshad Perriman','WR',28.20),(462,'Chris Moore','WR',39.20),(463,'Lirim Hajrullahu','K',58.50),(464,'Jacob Harris','TE',31.20),(465,'Montrell Washington','WR',36.40),(466,'Ryan Griffin','TE',32.80),(467,'Dare Ogunbowale','RB',18.40),(468,'Patrick Ricard','RB',24.60),(469,'DeSean Jackson','WR',0.00),(470,'James O\'Shaughnessy','TE',56.00),(471,'Jashaun Corbin','RB',0.00),(472,'Tajae Sharpe','WR',15.50),(473,'Jimmy Graham','TE',0.00),(474,'Royce Freeman','RB',16.30),(475,'Durham Smythe','TE',52.80),(476,'Gardner Minshew II','QB',14.80),(477,'Tyrell Williams','WR',0.00),(478,'Chigoziem Okonkwo','TE',13.50),(479,'Bo Melton','WR',6.30),(480,'Justice Hill','RB',14.10),(481,'Dez Fitzpatrick','WR',35.30),(482,'Maxx Williams','TE',34.00),(483,'Drew Sample','TE',28.30),(484,'Jalen Wydermyer','TE',0.00),(485,'Noah Gray','TE',27.40),(486,'Joe Flacco','QB',26.80),(487,'Charlie Kolar','TE',35.50),(488,'Mike Boone','RB',18.40),(489,'Godwin Igwebuike','RB',5.50),(490,'Nick Vannett','TE',20.90),(491,'Kennedy Brooks','RB',0.00),(492,'Tyler Kroft','TE',29.50),(493,'Marcedes Lewis','TE',40.00),(494,'Jermar Jefferson','RB',27.40),(495,'Jordan Mason','RB',0.00),(496,'Greg Dortch','WR',9.90),(497,'Rico Dowdle','RB',15.00),(498,'Blake Bell','TE',24.00),(499,'Hunter Long','TE',13.00),(500,'Andrew Jacas','K',0.00),(501,'C.J. Ham','RB',21.50),(502,'Larry Rountree III','RB',9.60),(503,'Dazz Newsome','WR',17.50),(504,'Zach Gentry','TE',28.40),(505,'Ross Dwelley','TE',20.50),(506,'Gary Brightwell','RB',23.70),(507,'Julius Chestnut','RB',10.10),(508,'Preston Williams','WR',17.70),(509,'J.J. Taylor','RB',28.70),(510,'Tyrod Taylor','QB',20.10),(511,'Collin Johnson','WR',6.90),(512,'Tre\' McKitty','TE',19.60),(513,'Nick Boyle','TE',35.30),(514,'Jacob Hollister','TE',9.50),(515,'Mike Thomas','WR',12.00),(516,'Tyler Davis','TE',0.00),(517,'Jerrion Ealy','RB',0.00),(518,'Scotty Miller','WR',7.80),(519,'Chester Rogers','WR',35.10),(520,'Marquise Goodwin','WR',13.30),(521,'Miles Boykin','WR',17.20),(522,'Trent Sherfield','WR',23.50),(523,'Alec Ingold','RB',13.20),(524,'Ray-Ray McCloud','WR',31.40),(525,'Trayveon Williams','RB',6.80),(526,'Sincere McCormick','RB',0.00),(527,'La\'Mical Perine','RB',10.30),(528,'Damiere Byrd','WR',35.80),(529,'Travis Fulgham','WR',6.20),(530,'Tyron Johnson','WR',9.90),(531,'Johnny Mundt','TE',18.00),(532,'Colby Parkinson','TE',12.90),(533,'Tyler Goodson','RB',0.00),(534,'Brock Wright','TE',26.90),(535,'Cyril Grayson Jr.','WR',10.90),(536,'Bisi Johnson','WR',10.40),(537,'Keith Smith','RB',16.70),(538,'Jakob Johnson','RB',10.30),(539,'Cole Turner','TE',15.40),(540,'Dezmon Patmon','WR',15.40);
/*!40000 ALTER TABLE `players` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teams`
--

DROP TABLE IF EXISTS `teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `teams` (
  `team_name` varchar(50) NOT NULL,
  `draft_spot` int(11) NOT NULL,
  `draft_id` int(11) NOT NULL,
  `user_team` tinyint(4) NOT NULL,
  `team_array` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`team_name`,`draft_id`),
  KEY `fk_draft_id` (`draft_id`),
  CONSTRAINT `fk_draft_id` FOREIGN KEY (`draft_id`) REFERENCES `drafts` (`draft_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teams`
--

LOCK TABLES `teams` WRITE;
/*!40000 ALTER TABLE `teams` DISABLE KEYS */;
INSERT INTO `teams` VALUES ('adsfa',1,1,1,'1,20,29,42,56,73,85,99,109,126,138,178,156,157,173,'),('CPU Team 10',10,1,0,'11,26,38,52,66,79,94,108,121,131,150,232,198,174,185,'),('CPU Team 11',11,1,0,'12,23,41,54,67,81,92,114,122,148,151,236,204,168,188,'),('CPU Team 12',12,1,0,'8,24,39,55,68,82,95,128,123,149,155,240,207,172,192,'),('CPU Team 13',13,1,0,'13,27,40,59,69,83,97,115,124,132,152,242,211,169,195,'),('CPU Team 14',14,1,0,'14,28,43,53,70,84,98,107,129,136,153,154,221,170,193,'),('CPU Team 2',2,1,0,'6,15,30,44,58,71,89,101,110,125,139,187,159,158,175,'),('CPU Team 3',3,1,0,'2,16,31,45,57,76,86,100,112,135,140,191,166,160,176,'),('CPU Team 4',4,1,0,'3,21,32,46,60,72,87,102,113,127,145,200,171,161,177,'),('CPU Team 5',5,1,0,'4,17,33,49,61,74,88,111,116,137,142,203,182,162,179,'),('CPU Team 6',6,1,0,'5,25,34,47,62,80,90,103,119,141,143,217,183,164,180,'),('CPU Team 7',7,1,0,'7,18,36,48,63,75,96,104,120,133,144,219,184,165,189,'),('CPU Team 8',8,1,0,'9,19,35,50,64,77,93,105,117,134,146,220,186,163,190,'),('CPU Team 9',9,1,0,'10,22,37,51,65,78,91,106,118,130,147,231,196,167,181,');
/*!40000 ALTER TABLE `teams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `salt` char(64) NOT NULL,
  `hash_pass` varchar(256) NOT NULL,
  `recent_session_id` varchar(256) DEFAULT NULL,
  `session_created_at` datetime DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `reset_token` varchar(64) DEFAULT NULL,
  `reset_token_expires` datetime DEFAULT NULL,
  PRIMARY KEY (`username`),
  UNIQUE KEY `email_unique` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('a','7abf883fa820fa5097be8cd2f7013b51','ea93af66de80eb734a52a3c8671ed393631dca350cc67ccbdf6e1d3ac21e9bc3','');
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

-- Dump completed on 2024-06-25 19:45:29