# MeetingRoom
 预定会议室系统
具体接口请见localhost:8080/swagger-ui.html
前后端已集成在一起


回复 arraycto 
sql如下

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '部门表id',
  `department_name` varchar(50) NOT NULL COMMENT '部门名字(不可重复）',
  `department_level` int(4) NOT NULL COMMENT '部门等级，123，最高3',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '最后一次更新时间',
  `delete_time` varchar(50) DEFAULT '' COMMENT '有值则已经被删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '设备表id',
  `device_type` varchar(50) NOT NULL COMMENT '设备类型名称',
  `device_id` int(4) NOT NULL COMMENT '设备id,1投影仪,2电插头,3遥控器,4黑板,5HDMI接口',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '最后一次更新时间',
  `delete_time` varchar(50) DEFAULT '' COMMENT '有值则已经被删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for meeting
-- ----------------------------
DROP TABLE IF EXISTS `meeting`;
CREATE TABLE `meeting` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '会议id',
  `meeting_name` varchar(50) NOT NULL COMMENT '会议名称',
  `meeting_level` int(4) NOT NULL COMMENT '会议等级0面试1例会2高级3紧急',
  `meeting_id` varchar(50) NOT NULL COMMENT '全局meeting',
  `room_id` varchar(50) NOT NULL COMMENT '会议室id',
  `username` varchar(50) NOT NULL COMMENT '会议预定者',
  `booking_date` date NOT NULL COMMENT '会议预定日期',
  `booking_start_time` datetime NOT NULL COMMENT '定会议室起始时间',
  `booking_end_time` datetime NOT NULL COMMENT '定会议室结束时间',
  `required_time` decimal(10,2) NOT NULL COMMENT '会议时长',
  `department_name` varchar(50) NOT NULL COMMENT '预定的部门名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '最后一次更新时间',
  `delete_time` varchar(50) DEFAULT '' COMMENT '有值则已经被删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for meeting_room
-- ----------------------------
DROP TABLE IF EXISTS `meeting_room`;
CREATE TABLE `meeting_room` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户表id',
  `room_id` varchar(50) NOT NULL COMMENT '房间id（不可重复）',
  `room_scale` smallint(6) NOT NULL COMMENT '会议室可容纳人数',
  `room_name` varchar(50) NOT NULL COMMENT '中文房间名',
  `free_time_start` datetime NOT NULL COMMENT '开放时间开始',
  `free_time_end` datetime NOT NULL COMMENT '开放时间结束',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '最后一次更新时间',
  `delete_time` varchar(50) DEFAULT '' COMMENT '有值则已经被删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for room_device
-- ----------------------------
DROP TABLE IF EXISTS `room_device`;
CREATE TABLE `room_device` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '设备表id',
  `room_id` varchar(50) NOT NULL COMMENT '房间id',
  `device_id` int(4) NOT NULL COMMENT '设备id,1投影仪,2电插头,3遥控器,4黑板,5HDMI接口',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '最后一次更新时间',
  `delete_time` varchar(50) DEFAULT '' COMMENT '有值则已经被删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户表id',
  `username` varchar(50) NOT NULL COMMENT '用户名（不可重复）',
  `pswd` varchar(50) NOT NULL COMMENT '用户密码，MD5加密',
  `department_name` varchar(50) DEFAULT NULL COMMENT '部门名字（不可重复）',
  `email` varchar(50) DEFAULT NULL,
  `question` varchar(100) DEFAULT NULL COMMENT '找回密码问题',
  `answer` varchar(100) DEFAULT NULL COMMENT '找回密码答案',
  `role_id` int(4) NOT NULL COMMENT '角色0-管理员,1-普通用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '最后一次更新时间',
  `delete_time` varchar(50) DEFAULT '' COMMENT '有值则已经被删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
