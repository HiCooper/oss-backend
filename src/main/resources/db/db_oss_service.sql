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

 Date: 16/10/2019 13:47:29
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '分组权限' ROW_FORMAT = Dynamic;

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
  `reference_count` int(11) UNSIGNED NULL DEFAULT 1 COMMENT '被引用次数',
  `locked` bit(1) NULL DEFAULT b'0' COMMENT '是否已锁定',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `hash_unique`(`hash`) USING BTREE COMMENT '文件hash唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '对象 hash' ROW_FORMAT = Dynamic;

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
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `bucket`(`bucket`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for referer_info
-- ----------------------------
DROP TABLE IF EXISTS `referer_info`;
CREATE TABLE `referer_info`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `bucket_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'bucket id',
  `white_list` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '白名单',
  `black_list` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '黑明单',
  `allow_empty` bit(1) NULL DEFAULT NULL COMMENT '允许空 Referer',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '防盗链，http referer 白名单设置' ROW_FORMAT = Dynamic;

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

-- -----


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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '分片存储信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '昵称',
  `activated` bit(1) NULL DEFAULT b'0' COMMENT '激活状态',
  `enabled` bit(1) NULL DEFAULT b'1' COMMENT '启用状态',
  `locked` bit(1) NULL DEFAULT b'0' COMMENT '是否锁定',
  `expired` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '上次修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户信息' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$5//RRdiBhvNv8S4IgoWCqOanWqnLfOuRZ9Y2UHkIwI8/kf9uHx9z2', '2018-12-03 15:07:57', NULL, 1);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

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
