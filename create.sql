CREATE DATABASE `imooc-dubbo-item` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;
CREATE TABLE `items` (
  `name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `counts` int(11) DEFAULT NULL,
  `id` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
CREATE DATABASE `imooc-dubbo-order` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
CREATE TABLE `orders` (
  `id` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `order_num` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `item_id` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

