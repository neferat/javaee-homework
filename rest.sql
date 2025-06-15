/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : rest

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 16/06/2025 01:19:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for actor
-- ----------------------------
DROP TABLE IF EXISTS `actor`;
CREATE TABLE `actor`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `age` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of actor
-- ----------------------------
INSERT INTO `actor` VALUES (1, '梁朝伟', 23);
INSERT INTO `actor` VALUES (2, '刘德华', 23);
INSERT INTO `actor` VALUES (5, 'wsz', 22);
INSERT INTO `actor` VALUES (6, 'qq', 44);
INSERT INTO `actor` VALUES (7, 'qqd', 44);
INSERT INTO `actor` VALUES (8, 'tt', 55);
INSERT INTO `actor` VALUES (11, '阿萨德', 111);
INSERT INTO `actor` VALUES (12, '发', 21);
INSERT INTO `actor` VALUES (13, 'qq', 11);
INSERT INTO `actor` VALUES (14, 'ee', 11);

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
  `fileid` int NOT NULL AUTO_INCREMENT,
  `filename` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `realfilename` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `bz` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`fileid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file
-- ----------------------------
INSERT INTO `file` VALUES (1, 'AntiRK_ev.sys', '1498546833.sys', 'this is bz');
INSERT INTO `file` VALUES (2, '微信截图_20200909170817.png', '1599723996.png', 'this is bz');
INSERT INTO `file` VALUES (3, '微信截图_20200909170817.png', '1599724126.png', 'this is bz');

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`  (
  `id` bigint NOT NULL,
  `gmt_create` datetime NULL DEFAULT NULL,
  `gmt_update` datetime NULL DEFAULT NULL,
  `number` int NULL DEFAULT NULL,
  `version` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of item
-- ----------------------------
INSERT INTO `item` VALUES (1, '2018-05-10 10:33:54', '2021-09-17 19:26:30', 100, 1);
INSERT INTO `item` VALUES (2, '2021-10-14 13:22:23', '2021-10-14 13:22:25', 222, NULL);
INSERT INTO `item` VALUES (3, '2021-10-14 13:22:33', '2021-10-14 13:22:35', 333, 1);
INSERT INTO `item` VALUES (4, '2021-10-14 13:22:54', '2021-10-14 13:22:56', 123, 11);
INSERT INTO `item` VALUES (5, '2021-10-14 13:23:06', '2021-10-14 13:23:08', 112, 1);
INSERT INTO `item` VALUES (6, '2021-10-14 13:23:37', '2021-10-14 13:23:40', 12, 1);
INSERT INTO `item` VALUES (7, '2021-10-14 13:23:48', '2021-10-14 13:23:50', 12, 11);
INSERT INTO `item` VALUES (8, '2021-10-13 13:24:00', '2021-10-06 13:24:03', 1, 21);
INSERT INTO `item` VALUES (9, '2021-10-13 13:24:14', '2021-09-29 13:24:17', 2, 1);
INSERT INTO `item` VALUES (10, '2021-10-14 13:24:27', '2021-10-01 13:24:29', 1, 2);
INSERT INTO `item` VALUES (11, '2021-09-29 13:24:37', '2021-10-14 13:24:40', 2, 1);
INSERT INTO `item` VALUES (12, '2021-10-14 13:24:46', '2021-10-14 13:24:48', 3, 1);

SET FOREIGN_KEY_CHECKS = 1;
