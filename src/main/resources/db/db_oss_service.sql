/*
 Navicat Premium Data Transfer

 Source Server         : 47.101.42.169
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : 47.101.42.169:3306
 Source Schema         : db_oss_service

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 20/08/2019 11:30:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for access_key_info
-- ----------------------------
DROP TABLE IF EXISTS `access_key_info`;
CREATE TABLE `access_key_info`  (
  `access_key_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密钥id(AK)',
  `access_key_secret` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'SK',
  `user_id` int(11) UNSIGNED NOT NULL COMMENT '用户id',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `state` bit(1) NULL DEFAULT NULL COMMENT '密钥状态，启用，禁用',
  PRIMARY KEY (`access_key_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '密钥对信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of access_key_info
-- ----------------------------
INSERT INTO `access_key_info` VALUES ('UmAWuGv6aC5pE7bQ6il8wO', 'URbe6TvfdF5XhEeRXiB7yewYcU5PFEe', 2, '2019-07-04 09:26:24', '2019-07-04 09:26:24', b'1');
INSERT INTO `access_key_info` VALUES ('VQ5Brcc6CDJJcH5v.ybXI7', 'NLghXz00gli26IZtvV8dTbOMHjqpIbN', 1, '2019-07-13 09:16:06', '2019-07-13 09:16:06', b'1');
INSERT INTO `access_key_info` VALUES ('x7ADHaFHBRTZ94twOGl4dz', 'YeGtWeqt5j7yrr5jAENo/lYi7JprgeK', 2, '2019-07-21 08:24:40', '2019-07-21 08:24:40', b'1');

-- ----------------------------
-- Table structure for bucket_info
-- ----------------------------
DROP TABLE IF EXISTS `bucket_info`;
CREATE TABLE `bucket_info`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'id主键',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Bucket名称',
  `acl` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '读写权限',
  `region_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '区域id',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `bucket name unique`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '存储空间信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bucket_info
-- ----------------------------
INSERT INTO `bucket_info` VALUES ('5d1dd083ceeae10b2c841a9b', 2, 'cooper', 'PRIVATE', 'hqsrqtwf61m7h7tr7cb9pz4y', '2019-07-04 10:10:11', '2019-07-04 10:10:11');
INSERT INTO `bucket_info` VALUES ('5d299e5aceeae164ab357d40', 1, 'test', 'PRIVATE', 'hqsrqtwf61m7h7tr7cb9pz4y', '2019-07-13 09:03:22', '2019-07-13 09:03:22');
INSERT INTO `bucket_info` VALUES ('5d391d3c7c8b9e3ffc30df89', 2, 'test2', 'PUBLIC_READ_WRITE', 'hqsrqtwf61m7h7tr7cb9pz4y', '2019-07-25 03:08:44', '2019-07-25 03:08:44');
INSERT INTO `bucket_info` VALUES ('5d4a7ee27c8b9e3ffc30df8d', 2, 'yyy', 'PUBLIC_READ_WRITE', 'hqsrqtwf61m7h7tr7cb9pz4y', '2019-08-07 07:33:54', '2019-08-07 07:33:54');

-- ----------------------------
-- Table structure for group_acl_info
-- ----------------------------
DROP TABLE IF EXISTS `group_acl_info`;
CREATE TABLE `group_acl_info`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `group_id` int(11) NULL DEFAULT NULL COMMENT '分组id',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '存储空间名称',
  `acl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '组对bucket的acl',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '分组权限' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_info
-- ----------------------------
DROP TABLE IF EXISTS `group_info`;
CREATE TABLE `group_info`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int(11) UNSIGNED NOT NULL COMMENT '用户id',
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '组名',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户组主表信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for object_hash
-- ----------------------------
DROP TABLE IF EXISTS `object_hash`;
CREATE TABLE `object_hash`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'hash值',
  `file_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件对象id',
  `size` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '文件大小',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `reference_count` int(11) UNSIGNED NULL DEFAULT 1 COMMENT '被引用次数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `hash_2`(`hash`) USING BTREE COMMENT '文件hash唯一',
  UNIQUE INDEX `hash`(`hash`, `size`) USING BTREE COMMENT '文件hash，文件大小联合唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '对象 hash' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of object_hash
-- ----------------------------
INSERT INTO `object_hash` VALUES (1, 'EBEBA4D18EEF6C537F4716FC5E0B440401EA34CB3E41D7FFE24B036E4F10E9E0', '5d299e63ceeae164ab357d41', 12699, '2019-07-13 09:03:31', 2);
INSERT INTO `object_hash` VALUES (2, '17927F12F7696A750A767C89597EC6AA38802DB4ED1EEBF8107D44016730C108', '5d299e8dceeae164ab357d49', 12644, '2019-07-13 09:04:13', 2);
INSERT INTO `object_hash` VALUES (3, 'A75FC472A51786F9A181AFEF3A85D03260D13446EE3169F02C8465E9F0B63415', '5d29a074ceeae164ab357d4f', 118901, '2019-07-13 09:12:20', 2);
INSERT INTO `object_hash` VALUES (4, 'F40BF20F255C369AD13B21F0641B78AEB52E816015171EE4F3FB80DDCBB67FAC', '5d29a088ceeae164ab357d53', 232215, '2019-07-13 09:12:40', 1);
INSERT INTO `object_hash` VALUES (5, '524D14914637D6A069CABA61330B8F9B297DE4CA03F2B1B5B3EA214388587D46', '5d2af7687c8b9e2d1866f7e1', 335772, '2019-07-14 09:35:37', 1);
INSERT INTO `object_hash` VALUES (6, 'A08635506169F7AB8799779411D7D7B074DD70BC9F336DD56DEA9357515EF265', '5d2af7737c8b9e2d1866f7e3', 78557, '2019-07-14 09:35:47', 1);
INSERT INTO `object_hash` VALUES (7, 'BC8DAFFD9BE3036B6E19CB8B0A5B8A6D69E25D7075C5BBDCFC60CCBB5E100509', '5d2c112f7c8b9e2d1866f7ea', 26233, '2019-07-15 05:37:51', 1);
INSERT INTO `object_hash` VALUES (8, 'BC03DF13F18922B544F67537472072DDB95016AC1C38C16D315609B2FC2A40F2', '5d4a7f0a7c8b9e3ffc30df8e', 32256, '2019-08-07 07:34:34', 1);
INSERT INTO `object_hash` VALUES (9, '7160F31327AB60B351FB54B6B59DFFB67F1250AE8C304DD63C314C2118CB7E04', '5d4a7f1b7c8b9e3ffc30df90', 135645, '2019-08-07 07:34:51', 1);
INSERT INTO `object_hash` VALUES (10, '52BF9BF85103671D56CD6EB0B86F68DF7CBCB9D2E7D78F2696C0158B1AA1F9FD', '5d4a7fd87c8b9e3ffc30df92', 305609, '2019-08-07 07:38:00', 3);
INSERT INTO `object_hash` VALUES (11, 'B535C45A10E5C6EA8759BD715D0FEE12BCA4029A539778C214FAAD23286E9D05', '5d56380f6d24472b803ca26b', 232077, '2019-08-16 04:58:55', 1);

-- ----------------------------
-- Table structure for object_info
-- ----------------------------
DROP TABLE IF EXISTS `object_info`;
CREATE TABLE `object_info`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `bucket_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'bucket id',
  `acl` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '读写权限(目录没有)',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件名',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件路径',
  `file_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件对象id',
  `hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'hash值',
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件类型',
  `size` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '文件大小',
  `formatted_size` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '格式化文件大小',
  `is_dir` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否为文件夹',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `bucket_path_name`(`bucket_id`, `file_name`, `file_path`) USING BTREE,
  INDEX `bucket_id`(`bucket_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '对象信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of object_info
-- ----------------------------
INSERT INTO `object_info` VALUES ('5d2af7567c8b9e2d1866f7de', 1, '5d299e5aceeae164ab357d40', 'EXTEND_BUCKET', 'NotFound-260-260.png', '/', '5d299e8dceeae164ab357d49', '17927F12F7696A750A767C89597EC6AA38802DB4ED1EEBF8107D44016730C108', 'PNG', 12644, '12.35KB', b'0', '2019-07-14 09:35:18', '2019-07-14 09:35:18');
INSERT INTO `object_info` VALUES ('5d2af75e7c8b9e2d1866f7df', 1, '5d299e5aceeae164ab357d40', 'EXTEND_BUCKET', 'main_bg.png', '/', '5d299e63ceeae164ab357d41', 'EBEBA4D18EEF6C537F4716FC5E0B440401EA34CB3E41D7FFE24B036E4F10E9E0', 'PNG', 12699, '12.40KB', b'0', '2019-07-14 09:35:26', '2019-07-14 09:35:26');
INSERT INTO `object_info` VALUES ('5d2af7637c8b9e2d1866f7e0', 1, '5d299e5aceeae164ab357d40', 'EXTEND_BUCKET', 'main_bg_sea.png', '/', '5d29a074ceeae164ab357d4f', 'A75FC472A51786F9A181AFEF3A85D03260D13446EE3169F02C8465E9F0B63415', 'PNG', 118901, '116.11KB', b'0', '2019-07-14 09:35:31', '2019-07-14 09:35:31');
INSERT INTO `object_info` VALUES ('5d2af7687c8b9e2d1866f7e2', 1, '5d299e5aceeae164ab357d40', 'EXTEND_BUCKET', 'main_bg_mouton.png', '/', '5d2af7687c8b9e2d1866f7e1', '524D14914637D6A069CABA61330B8F9B297DE4CA03F2B1B5B3EA214388587D46', 'PNG', 335772, '327.90KB', b'0', '2019-07-14 09:35:36', '2019-07-14 09:35:36');
INSERT INTO `object_info` VALUES ('5d2af7737c8b9e2d1866f7e4', 1, '5d299e5aceeae164ab357d40', 'EXTEND_BUCKET', '天空之城吉他.png', '/', '5d2af7737c8b9e2d1866f7e3', 'A08635506169F7AB8799779411D7D7B074DD70BC9F336DD56DEA9357515EF265', 'PNG', 78557, '76.72KB', b'0', '2019-07-14 09:35:47', '2019-07-14 09:35:47');
INSERT INTO `object_info` VALUES ('5d2c112f7c8b9e2d1866f7eb', 2, '5d1dd083ceeae10b2c841a9b', 'EXTEND_BUCKET', 'timg.jpg', '/', '5d2c112f7c8b9e2d1866f7ea', 'BC8DAFFD9BE3036B6E19CB8B0A5B8A6D69E25D7075C5BBDCFC60CCBB5E100509', 'JPG', 26233, '25.62KB', b'0', '2019-07-15 05:37:51', '2019-07-15 05:37:51');
INSERT INTO `object_info` VALUES ('5d391d697c8b9e3ffc30df8a', 2, '5d391d3c7c8b9e3ffc30df89', NULL, 'app', '/', NULL, NULL, NULL, NULL, NULL, b'1', '2019-07-25 03:09:29', '2019-07-25 03:09:29');
INSERT INTO `object_info` VALUES ('5d391d697c8b9e3ffc30df8b', 2, '5d391d3c7c8b9e3ffc30df89', NULL, '2019', '/app', NULL, NULL, NULL, NULL, NULL, b'1', '2019-07-25 03:09:29', '2019-07-25 03:09:29');
INSERT INTO `object_info` VALUES ('5d391d697c8b9e3ffc30df8c', 2, '5d391d3c7c8b9e3ffc30df89', NULL, '0725', '/app/2019', NULL, NULL, NULL, NULL, NULL, b'1', '2019-07-25 03:09:29', '2019-07-25 03:09:29');
INSERT INTO `object_info` VALUES ('5d4a7f0a7c8b9e3ffc30df8f', 2, '5d4a7ee27c8b9e3ffc30df8d', 'EXTEND_BUCKET', '获取图片url+缩略图接口文档.doc', '/', '5d4a7f0a7c8b9e3ffc30df8e', 'BC03DF13F18922B544F67537472072DDB95016AC1C38C16D315609B2FC2A40F2', 'DOC', 32256, '31.50KB', b'0', '2019-08-07 07:34:34', '2019-08-07 07:34:34');
INSERT INTO `object_info` VALUES ('5d4a7f1b7c8b9e3ffc30df91', 2, '5d4a7ee27c8b9e3ffc30df8d', 'EXTEND_BUCKET', 'yangzihao.png', '/', '5d4a7f1b7c8b9e3ffc30df90', '7160F31327AB60B351FB54B6B59DFFB67F1250AE8C304DD63C314C2118CB7E04', 'PNG', 135645, '132.47KB', b'0', '2019-08-07 07:34:51', '2019-08-07 07:34:51');
INSERT INTO `object_info` VALUES ('5d4a7fd87c8b9e3ffc30df93', 2, '5d4a7ee27c8b9e3ffc30df8d', 'EXTEND_BUCKET', '1.jpg', '/', '5d4a7fd87c8b9e3ffc30df92', '52BF9BF85103671D56CD6EB0B86F68DF7CBCB9D2E7D78F2696C0158B1AA1F9FD', 'JPG', 305609, '298.45KB', b'0', '2019-08-07 07:38:00', '2019-08-07 07:38:00');
INSERT INTO `object_info` VALUES ('5d4a82797c8b9e3ffc30df94', 2, '5d4a7ee27c8b9e3ffc30df8d', 'PUBLIC_READ_WRITE', '2.jpg', '/', '5d4a7fd87c8b9e3ffc30df92', '52BF9BF85103671D56CD6EB0B86F68DF7CBCB9D2E7D78F2696C0158B1AA1F9FD', 'JPG', 305609, '298.45KB', b'0', '2019-08-07 07:49:13', '2019-08-07 07:49:13');
INSERT INTO `object_info` VALUES ('5d4a93297c8b9e3ffc30df95', 2, '5d4a7ee27c8b9e3ffc30df8d', 'EXTEND_BUCKET', '2 - 副本.jpg', '/', '5d4a7fd87c8b9e3ffc30df92', '52BF9BF85103671D56CD6EB0B86F68DF7CBCB9D2E7D78F2696C0158B1AA1F9FD', 'JPG', 305609, '298.45KB', b'0', '2019-08-07 09:00:25', '2019-08-07 09:00:25');
INSERT INTO `object_info` VALUES ('5d5638336d2447335c79307f', 1, '5d299e5aceeae164ab357d40', 'PUBLIC_READ', 'byte_test.png', '/', '5d56380f6d24472b803ca26b', 'B535C45A10E5C6EA8759BD715D0FEE12BCA4029A539778C214FAAD23286E9D05', 'PNG', 232077, '226.64KB', b'0', '2019-08-16 04:59:31', '2019-08-16 04:59:31');

-- ----------------------------
-- Table structure for policy_info
-- ----------------------------
DROP TABLE IF EXISTS `policy_info`;
CREATE TABLE `policy_info`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键id',
  `bucket` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'bucket 名称',
  `effect` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '禁止或允许访问，禁止优先级高于允许Deny/Allow',
  `action_type` int(1) NOT NULL COMMENT '授权类型 1-只读，2-读写，3-完全控制，4-拒绝访问',
  `resource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '授权资源',
  `principal` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '授权用户',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `bucket`(`bucket`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of policy_info
-- ----------------------------
INSERT INTO `policy_info` VALUES ('1151073668568264705', 'cooper', 'Allow', 1, 'cooper/*', 'admin', '2019-07-16 10:18:42', '2019-07-16 10:18:42');
INSERT INTO `policy_info` VALUES ('1151074340520931329', 'cooper', 'Allow', 1, 'cooper/test/*', 'admin', '2019-07-16 10:21:22', '2019-07-16 10:21:22');
INSERT INTO `policy_info` VALUES ('1151074418883112961', 'cooper', 'Allow', 1, 'cooper/timg.png', 'admin,test', '2019-07-16 10:21:41', '2019-07-16 10:21:41');
INSERT INTO `policy_info` VALUES ('1151143316031819778', 'test', 'Allow', 1, 'test/天空之城吉他.png', 'test', '2019-07-16 14:55:28', '2019-07-16 14:55:28');
INSERT INTO `policy_info` VALUES ('1151146668706779138', 'test', 'Allow', 1, 'test/main_bg_sea.png', 'test', '2019-07-16 15:08:47', '2019-07-16 15:08:47');
INSERT INTO `policy_info` VALUES ('1151308532756344833', 'test', 'Allow', 1, 'test/*', 'test', '2019-07-17 01:51:57', '2019-07-17 01:51:57');

-- ----------------------------
-- Table structure for region_and_server
-- ----------------------------
DROP TABLE IF EXISTS `region_and_server`;
CREATE TABLE `region_and_server`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `region_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region id',
  `server_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '服务器 id',
  `state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '区域服务器关联关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of region_and_server
-- ----------------------------
INSERT INTO `region_and_server` VALUES (1, 'hqsrqtwf61m7h7tr7cb9pz4y', '5kgbvjoqvjadyhce2hrv3rzj', 'up', '2019-06-24 04:05:46', '2019-06-24 04:05:46');
INSERT INTO `region_and_server` VALUES (2, 'hqsrqtwf61m7h7tr7cb9pz4y', 'i00mdflqycivuwc4n6u1rpdc', 'down', '2019-06-24 04:05:46', '2019-06-25 01:54:15');
INSERT INTO `region_and_server` VALUES (3, 'hqsrqtwf61m7h7tr7cb9pz4y', 'kkovpt8zevpec8oh1xnvwyoh', 'down', '2019-06-24 04:05:46', '2019-06-25 01:54:15');
INSERT INTO `region_and_server` VALUES (4, 'hqsrqtwf61m7h7tr7cb9pz4y', 'lyquqymo6awjxkdfvdjxm9xb', 'down', '2019-06-24 04:05:46', '2019-06-25 01:54:15');
INSERT INTO `region_and_server` VALUES (5, 'hqsrqtwf61m7h7tr7cb9pz4y', 'mzf4lhmabye7fehgnqndjews', 'down', '2019-06-24 04:05:46', '2019-06-25 01:54:15');
INSERT INTO `region_and_server` VALUES (6, 'hqsrqtwf61m7h7tr7cb9pz4y', 'tmfrpb8tlxfch7r6rgpgjvh9', 'down', '2019-06-24 04:05:46', '2019-06-25 01:54:15');

-- ----------------------------
-- Table structure for region_info
-- ----------------------------
DROP TABLE IF EXISTS `region_info`;
CREATE TABLE `region_info`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键id',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '区域名称，128',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '区域代码，英文数字',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注信息，255',
  `capacity` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '容量，单位GB',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '区域信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of region_info
-- ----------------------------
INSERT INTO `region_info` VALUES ('hqsrqtwf61m7h7tr7cb9pz4y', '华东1 上海', 'oss-shanghai-1', NULL, 500, '2019-06-24 03:45:29', '2019-06-25 09:20:17');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int(11) NOT NULL COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '角色名',
  `description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'ROLE_ADMIN', '管理员');
INSERT INTO `role` VALUES (2, 'ROLE_USER', '普通用户');

-- ----------------------------
-- Table structure for server_info
-- ----------------------------
DROP TABLE IF EXISTS `server_info`;
CREATE TABLE `server_info`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键id',
  `ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ip',
  `port` int(8) UNSIGNED NULL DEFAULT NULL COMMENT '端口',
  `capacity` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '机器可用容量，单位GB',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注信息，255',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '服务器信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of server_info
-- ----------------------------
INSERT INTO `server_info` VALUES ('5kgbvjoqvjadyhce2hrv3rzj', '192.168.2.194', 8081, 100, NULL, '2019-06-24 04:03:34', '2019-06-25 01:18:50');
INSERT INTO `server_info` VALUES ('i00mdflqycivuwc4n6u1rpdc', '192.168.2.194', 8082, 100, NULL, '2019-06-24 04:03:34', '2019-06-25 01:18:51');
INSERT INTO `server_info` VALUES ('kkovpt8zevpec8oh1xnvwyoh', '192.168.2.194', 8083, 100, NULL, '2019-06-24 04:03:34', '2019-06-25 01:18:55');
INSERT INTO `server_info` VALUES ('lyquqymo6awjxkdfvdjxm9xb', '192.168.2.194', 8084, 100, NULL, '2019-06-24 04:03:34', '2019-06-25 01:18:56');
INSERT INTO `server_info` VALUES ('mzf4lhmabye7fehgnqndjews', '192.168.2.194', 8085, 100, NULL, '2019-06-24 04:03:34', '2019-06-25 01:18:58');
INSERT INTO `server_info` VALUES ('tmfrpb8tlxfch7r6rgpgjvh9', '192.168.2.194', 8086, 100, NULL, '2019-06-24 04:03:34', '2019-06-25 01:18:59');

-- ----------------------------
-- Table structure for shard_info
-- ----------------------------
DROP TABLE IF EXISTS `shard_info`;
CREATE TABLE `shard_info`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `file_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件对象id',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件名',
  `shard_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '分片json',
  `hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'hash值',
  `size` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '文件大小',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `singleton` bit(1) NULL DEFAULT NULL COMMENT '是否为单机模式分块',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `file_id`(`file_id`) USING BTREE COMMENT '文件id'
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '分片存储信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shard_info
-- ----------------------------
INSERT INTO `shard_info` VALUES (1, '5d299e63ceeae164ab357d41', 'main_bg.png', './test/main_bg.png', 'EBEBA4D18EEF6C537F4716FC5E0B440401EA34CB3E41D7FFE24B036E4F10E9E0', 12699, '2019-07-13 09:03:31', b'1');
INSERT INTO `shard_info` VALUES (2, '5d299e8dceeae164ab357d49', 'NotFound-260-260.png', './test/NotFound-260-260.png', '17927F12F7696A750A767C89597EC6AA38802DB4ED1EEBF8107D44016730C108', 12644, '2019-07-13 09:04:13', b'1');
INSERT INTO `shard_info` VALUES (3, '5d29a074ceeae164ab357d4f', 'main_bg_sea.png', './test/main_bg_sea.png', 'A75FC472A51786F9A181AFEF3A85D03260D13446EE3169F02C8465E9F0B63415', 118901, '2019-07-13 09:12:20', b'1');
INSERT INTO `shard_info` VALUES (4, '5d29a088ceeae164ab357d53', 'main_bg_night.png', './test/main_bg_night.png', 'F40BF20F255C369AD13B21F0641B78AEB52E816015171EE4F3FB80DDCBB67FAC', 232215, '2019-07-13 09:12:40', b'1');
INSERT INTO `shard_info` VALUES (5, '5d2af7687c8b9e2d1866f7e1', 'main_bg_mouton.png', './test/main_bg_mouton.png', '524D14914637D6A069CABA61330B8F9B297DE4CA03F2B1B5B3EA214388587D46', 335772, '2019-07-14 09:35:36', b'1');
INSERT INTO `shard_info` VALUES (6, '5d2af7737c8b9e2d1866f7e3', '天空之城吉他.png', './test/天空之城吉他.png', 'A08635506169F7AB8799779411D7D7B074DD70BC9F336DD56DEA9357515EF265', 78557, '2019-07-14 09:35:47', b'1');
INSERT INTO `shard_info` VALUES (7, '5d2c112f7c8b9e2d1866f7ea', 'timg.jpg', './cooper/timg.jpg', 'BC8DAFFD9BE3036B6E19CB8B0A5B8A6D69E25D7075C5BBDCFC60CCBB5E100509', 26233, '2019-07-15 05:37:51', b'1');
INSERT INTO `shard_info` VALUES (8, '5d4a7f0a7c8b9e3ffc30df8e', '获取图片url+缩略图接口文档.doc', './yyy/获取图片url+缩略图接口文档.doc', 'BC03DF13F18922B544F67537472072DDB95016AC1C38C16D315609B2FC2A40F2', 32256, '2019-08-07 07:34:34', b'1');
INSERT INTO `shard_info` VALUES (9, '5d4a7f1b7c8b9e3ffc30df90', 'yangzihao.png', './yyy/yangzihao.png', '7160F31327AB60B351FB54B6B59DFFB67F1250AE8C304DD63C314C2118CB7E04', 135645, '2019-08-07 07:34:51', b'1');
INSERT INTO `shard_info` VALUES (10, '5d4a7fd87c8b9e3ffc30df92', '1.jpg', './yyy/1.jpg', '52BF9BF85103671D56CD6EB0B86F68DF7CBCB9D2E7D78F2696C0158B1AA1F9FD', 305609, '2019-08-07 07:38:00', b'1');
INSERT INTO `shard_info` VALUES (11, '5d56380f6d24472b803ca26b', 'byte_test.png', '.\\test/byte_test.png', 'B535C45A10E5C6EA8759BD715D0FEE12BCA4029A539778C214FAAD23286E9D05', 232077, '2019-08-16 04:58:55', b'1');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '上次修改时间',
  `activated` tinyint(1) NULL DEFAULT NULL COMMENT '激活状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$5//RRdiBhvNv8S4IgoWCqOanWqnLfOuRZ9Y2UHkIwI8/kf9uHx9z2', '2018-12-03 15:07:57', NULL, 1);
INSERT INTO `user` VALUES (2, 'test', '$2a$10$lIDgoU57ilp3Vk1DYymm3uaM4gSA39TV5U5ej2CnURy/hZ7/h9xZS', '2019-06-28 20:44:32', NULL, 1);

-- ----------------------------
-- Table structure for user_and_group
-- ----------------------------
DROP TABLE IF EXISTS `user_and_group`;
CREATE TABLE `user_and_group`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `group_id` int(10) UNSIGNED NULL DEFAULT NULL COMMENT '组id',
  `user_id` int(10) UNSIGNED NULL DEFAULT NULL COMMENT '用户id',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` int(11) NOT NULL COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `role_id` int(11) NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `role_id`(`role_id`) USING BTREE,
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户角色关联关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1, 1);
INSERT INTO `user_role` VALUES (2, 1, 2);
INSERT INTO `user_role` VALUES (3, 2, 2);

SET FOREIGN_KEY_CHECKS = 1;
