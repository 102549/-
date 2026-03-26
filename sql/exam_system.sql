/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80044
 Source Host           : localhost:3306
 Source Schema         : exam_system

 Target Server Type    : MySQL
 Target Server Version : 80044
 File Encoding         : 65001

 Date: 23/03/2026 15:33:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ec_classes
-- ----------------------------
DROP TABLE IF EXISTS `ec_classes`;
CREATE TABLE `ec_classes`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称',
  `course_id` int UNSIGNED NOT NULL COMMENT '课程ID',
  `teacher_id` int NOT NULL COMMENT '老师ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ec_classes
-- ----------------------------
INSERT INTO `ec_classes` VALUES (101, 'SpringCloud高级班', 17, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_classes` VALUES (102, 'Vue3实战班', 6, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_classes` VALUES (103, '微服务架构专题班', 17, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for ec_courses
-- ----------------------------
DROP TABLE IF EXISTS `ec_courses`;
CREATE TABLE `ec_courses`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程封面',
  `introduce` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程介绍',
  `is_public` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '是否公开',
  `status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '课程状态',
  `user_id` int NOT NULL COMMENT '创建者',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ec_courses
-- ----------------------------
INSERT INTO `ec_courses` VALUES (101, 'SpringCloud微服务实战', 'https://example.com/springcloud.jpg', '深入学习Spring Cloud微服务架构，包括服务注册发现、配置中心、负载均衡、熔断器、网关等核心组件。', 1, 0, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_courses` VALUES (102, 'Vue3前端框架', 'https://example.com/vue3.jpg', '学习Vue3框架核心概念，包括组合式API、响应式系统、路由管理、状态管理等。', 1, 0, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_courses` VALUES (103, 'Spring Boot企业级开发', 'https://example.com/springboot.jpg', '掌握Spring Boot企业级开发技能，包括自动配置、数据访问、安全控制、缓存管理等。', 1, 0, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_courses` VALUES (104, '分布式系统架构设计', 'https://example.com/distributed.jpg', '学习分布式系统架构设计原则、常见模式、事务一致性、服务网格等。', 1, 0, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_courses` VALUES (105, '在线考试系统综合实践', 'https://example.com/exam.jpg', '综合运用前后端技术，构建完整的在线考试系统。', 1, 0, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for ec_join_class
-- ----------------------------
DROP TABLE IF EXISTS `ec_join_class`;
CREATE TABLE `ec_join_class`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `class_id` int NOT NULL COMMENT '班级ID',
  `student_id` int NOT NULL COMMENT '学生ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_class_student`(`class_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1009 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级学生关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ec_join_class
-- ----------------------------
INSERT INTO `ec_join_class` VALUES (1001, 1, 14, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_join_class` VALUES (1002, 1, 13, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_join_class` VALUES (1003, 1, 15, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_join_class` VALUES (1004, 2, 14, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_join_class` VALUES (1005, 2, 16, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_join_class` VALUES (1006, 3, 13, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_join_class` VALUES (1007, 3, 14, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ec_join_class` VALUES (1008, 3, 17, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for ed_exam_answer_result
-- ----------------------------
DROP TABLE IF EXISTS `ed_exam_answer_result`;
CREATE TABLE `ed_exam_answer_result`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '学生ID',
  `exam_info_id` int NOT NULL COMMENT '考试ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `option_id` int NULL DEFAULT NULL COMMENT '选项ID',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生答案',
  `result_type` tinyint NULL DEFAULT 0 COMMENT '结果类型：0对、1错、2半错',
  `score` float NULL DEFAULT 0 COMMENT '得分',
  `max_score` float NULL DEFAULT 0 COMMENT '最高分',
  `ai_graded` tinyint(1) NULL DEFAULT 0 COMMENT '是否AI批改',
  `personalized_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个性化评语',
  `keyword_matched` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '匹配到的关键词',
  `similarity_score` double NULL DEFAULT 0 COMMENT '相似度得分',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_user`(`exam_info_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5006 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作答结果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ed_exam_answer_result
-- ----------------------------
INSERT INTO `ed_exam_answer_result` VALUES (5001, 14, 28, 1001, NULL, 'Spring Boot的核心配置文件是application.properties或application.yml，它主要用于配置应用程序的各种属性，包括服务器端口、数据库连接等。', 0, 9.5, 10, 1, '【Spring Boot框架的核心配置文件是什么？它的主要作用是什么？】\n\n优秀：答案准确完整，要点齐全，表达清晰流畅。\n\n【题目解析】Spring Boot的核心配置文件是application.properties或application.yml，用于配置应用程序的各种属性，如服务器端口、数据库连接等。', NULL, 0, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ed_exam_answer_result` VALUES (5002, 14, 28, 1002, NULL, '微服务架构的主要特点是服务拆分、独立部署，每个服务聚焦特定业务。优势是提升开发效率。', 2, 10, 15, 1, '【请简述微服务架构的主要特点及其优势。】\n\n中等：答案基本正确，但对部分关键知识点的理解还不够深入。\n缺失的关键知识点: 独立部署、容错隔离、去中心化\n\n【建议重点复习】独立部署、容错隔离、去中心化', NULL, 0, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ed_exam_answer_result` VALUES (5003, 14, 28, 1003, NULL, 'Nacos是服务发现和配置管理平台。', 2, 6, 10, 1, '【什么是Nacos？它在微服务架构中承担什么角色？】\n\n及格：答案存在一定偏差，需要加强对基础知识的理解。\n缺失的关键知识点: 配置热更新、服务健康检查\n\n【题目解析】Nacos是一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。', NULL, 0, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ed_exam_answer_result` VALUES (5004, 13, 28, 1001, NULL, 'Spring Boot使用application.yml作为核心配置文件，用于配置应用程序属性。', 0, 9, 10, 1, '良好：答案基本正确，主要知识点都已涵盖，仅有轻微瑕疵。', NULL, 0, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ed_exam_answer_result` VALUES (5005, 13, 28, 1002, NULL, '微服务架构特点：单一职责、独立部署、去中心化。优势：提升开发效率、支持独立扩展。', 0, 13, 15, 1, '优秀：答案准确完整，要点齐全，表达清晰流畅。', NULL, 0, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for ed_student_ability_profile
-- ----------------------------
DROP TABLE IF EXISTS `ed_student_ability_profile`;
CREATE TABLE `ed_student_ability_profile`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '学生ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `total_answers` int NULL DEFAULT 0 COMMENT '总答题数量',
  `correct_answers` int NULL DEFAULT 0 COMMENT '正确答题数量',
  `accuracy_rate` double NULL DEFAULT 0 COMMENT '正确率(0-100)',
  `avg_answer_time` double NULL DEFAULT 0 COMMENT '平均答题时间(秒)',
  `knowledge_point_status` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '知识点掌握情况(JSON格式)',
  `ability_level` int NULL DEFAULT 1 COMMENT '能力等级(1-5)',
  `last_exam_id` int NULL DEFAULT NULL COMMENT '最近考试ID',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_course`(`user_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_ability_level`(`ability_level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生能力画像表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ed_student_ability_profile
-- ----------------------------
INSERT INTO `ed_student_ability_profile` VALUES (1, 14, 17, 50, 42, 84, 45.5, '[{\"tagId\":1,\"tagName\":\"Spring基础\",\"masteryRate\":90.0},{\"tagId\":2,\"tagName\":\"SpringCloud\",\"masteryRate\":78.0}]', 4, 28, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ed_student_ability_profile` VALUES (2, 13, 17, 30, 24, 80, 55, '[{\"tagId\":1,\"tagName\":\"Spring基础\",\"masteryRate\":85.0},{\"tagId\":2,\"tagName\":\"SpringCloud\",\"masteryRate\":75.0}]', 4, 26, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ed_student_ability_profile` VALUES (3, 14, 6, 20, 16, 80, 60, '[{\"tagId\":3,\"tagName\":\"Vue基础\",\"masteryRate\":80.0}]', 4, NULL, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for ed_student_behavior_data
-- ----------------------------
DROP TABLE IF EXISTS `ed_student_behavior_data`;
CREATE TABLE `ed_student_behavior_data`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '学生ID',
  `exam_info_id` int NOT NULL COMMENT '考试ID',
  `class_id` int NULL DEFAULT NULL COMMENT '班级ID',
  `mouse_avg_speed` double NULL DEFAULT NULL COMMENT '鼠标平均移动速度(像素/秒)',
  `click_frequency` double NULL DEFAULT NULL COMMENT '鼠标点击频率(次/分钟)',
  `screen_switch_count` int NULL DEFAULT 0 COMMENT '切屏次数',
  `answer_duration` int NULL DEFAULT 0 COMMENT '答题时长(秒)',
  `answer_modify_count` int NULL DEFAULT 0 COMMENT '答案修改次数',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `answer_hour` tinyint NULL DEFAULT NULL COMMENT '答题时段(0-23)',
  `risk_level` tinyint NULL DEFAULT 0 COMMENT '风险等级:0低风险,1中风险,2高风险',
  `anomaly_count` int NULL DEFAULT 0 COMMENT '异常特征数量',
  `anomaly_details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '异常特征详情(JSON格式)',
  `is_alerted` tinyint(1) NULL DEFAULT 0 COMMENT '是否已预警',
  `alert_time` timestamp NULL DEFAULT NULL COMMENT '预警时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_exam`(`user_id` ASC, `exam_info_id` ASC) USING BTREE,
  INDEX `idx_risk_level`(`risk_level` ASC) USING BTREE,
  INDEX `idx_is_alerted`(`is_alerted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生行为数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ed_student_behavior_data
-- ----------------------------
INSERT INTO `ed_student_behavior_data` VALUES (1, 14, 28, 12, 350.5, 25, 2, 1800, 3, '192.168.1.101', 14, 0, 0, '[]', 0, NULL, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ed_student_behavior_data` VALUES (2, 13, 28, 12, 680, 85, 8, 1200, 15, '192.168.1.102', 3, 2, 4, '[\"鼠标移动速度异常(过快，可能为脚本操作)\", \"点击频率异常(过高，可能为机器作答)\", \"切屏次数过多(疑似查阅外部资料)\", \"异常答题时段(深夜作答)\"]', 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ed_student_behavior_data` VALUES (3, 14, 26, 13, 420, 30, 3, 2000, 5, '192.168.1.103', 10, 0, 0, '[]', 0, NULL, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for ee_exam_info
-- ----------------------------
DROP TABLE IF EXISTS `ee_exam_info`;
CREATE TABLE `ee_exam_info`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '考试ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试标题',
  `exam_id` int NOT NULL COMMENT '试卷ID',
  `teacher_id` int NOT NULL COMMENT '老师ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `question_disorder` tinyint(1) NULL DEFAULT 1 COMMENT '题目乱序',
  `option_disorder` tinyint(1) NULL DEFAULT 1 COMMENT '选项乱序',
  `end_visible` tinyint(1) NULL DEFAULT 0 COMMENT '结束可见',
  `is_monitor` tinyint(1) NULL DEFAULT 1 COMMENT '开启监控',
  `is_copy_paste` tinyint(1) NULL DEFAULT 0 COMMENT '允许复制粘贴',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `enable_anti_cheat` tinyint(1) NULL DEFAULT 1 COMMENT '启用防作弊',
  `enable_adaptive_paper` tinyint(1) NULL DEFAULT 0 COMMENT '启用自适应组卷',
  `passing_score` float NULL DEFAULT 60 COMMENT '及格分数',
  `total_score` float NULL DEFAULT 100 COMMENT '总分',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ee_exam_info
-- ----------------------------
INSERT INTO `ee_exam_info` VALUES (101, 'Spring Cloud微服务架构测试', 1, 10, 17, 1, 1, 0, 1, 0, '2024-01-15 18:00:00', '2024-01-15 14:00:00', '2024-01-15 16:00:00', 1, 1, 60, 100, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_info` VALUES (102, 'Vue前端框架测试', 2, 10, 6, 1, 1, 1, 1, 1, '2024-01-20 16:00:00', '2024-01-20 14:00:00', '2024-01-20 16:00:00', 1, 0, 60, 100, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_info` VALUES (103, '在线考试系统综合测试', 3, 10, 17, 1, 1, 0, 1, 0, '2024-01-25 18:00:00', '2024-01-25 14:00:00', '2024-01-25 16:00:00', 1, 1, 60, 100, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for ee_exam_paper
-- ----------------------------
DROP TABLE IF EXISTS `ee_exam_paper`;
CREATE TABLE `ee_exam_paper`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '试卷标题',
  `course_id` int NOT NULL COMMENT '课程ID',
  `total_score` float NULL DEFAULT 100 COMMENT '总分',
  `question_count` int NULL DEFAULT 0 COMMENT '题目数量',
  `is_random` tinyint(1) NULL DEFAULT 0 COMMENT '是否随机出题',
  `adaptive_enabled` tinyint(1) NULL DEFAULT 0 COMMENT '是否启用自适应',
  `difficulty` tinyint NULL DEFAULT 3 COMMENT '默认难度',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ee_exam_paper
-- ----------------------------
INSERT INTO `ee_exam_paper` VALUES (101, 'Spring Cloud标准试卷', 17, 100, 10, 0, 0, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_paper` VALUES (102, 'Vue框架标准试卷', 6, 100, 8, 0, 0, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_paper` VALUES (103, '综合能力测试卷', 17, 100, 12, 1, 1, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for ee_exam_question
-- ----------------------------
DROP TABLE IF EXISTS `ee_exam_question`;
CREATE TABLE `ee_exam_question`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_id` int NOT NULL COMMENT '试卷ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `score` float NULL DEFAULT 5 COMMENT '分值',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1025 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试题目关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ee_exam_question
-- ----------------------------
INSERT INTO `ee_exam_question` VALUES (1001, 1, 1001, 1, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1002, 1, 1002, 2, 15, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1003, 1, 1003, 3, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1004, 1, 1004, 4, 5, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1005, 1, 1005, 5, 8, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1006, 1, 1006, 6, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1007, 1, 1007, 7, 5, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1008, 1, 1011, 8, 5, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1009, 1, 1012, 9, 4, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1010, 2, 1008, 1, 12, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1011, 2, 1009, 2, 8, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1012, 2, 1010, 3, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1013, 3, 1001, 1, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1014, 3, 1002, 2, 15, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1015, 3, 1003, 3, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1016, 3, 1004, 4, 5, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1017, 3, 1005, 5, 8, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1018, 3, 1006, 6, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1019, 3, 1007, 7, 5, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1020, 3, 1008, 8, 12, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1021, 3, 1009, 9, 8, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1022, 3, 1010, 10, 10, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1023, 3, 1011, 11, 5, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `ee_exam_question` VALUES (1024, 3, 1012, 12, 4, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for eq_question
-- ----------------------------
DROP TABLE IF EXISTS `eq_question`;
CREATE TABLE `eq_question`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容',
  `type` tinyint NOT NULL COMMENT '题目类型：0单选、1多选、2判断、3填空、4主观',
  `analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '题目解析',
  `difficulty` tinyint NULL DEFAULT 3 COMMENT '题目难度(0-5)',
  `score` float NULL DEFAULT 5 COMMENT '题目分值',
  `is_public` tinyint NULL DEFAULT 0 COMMENT '是否公开：0自己、1课程、2公开',
  `course_id` int NOT NULL COMMENT '课程ID',
  `tag_id` int NULL DEFAULT NULL COMMENT '题目标签ID',
  `teacher_id` int NOT NULL COMMENT '教师ID',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '正确答案(主观题用)',
  `keyword_weight` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '关键词权重(JSON格式)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE,
  INDEX `idx_difficulty`(`difficulty` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1013 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eq_question
-- ----------------------------
INSERT INTO `eq_question` VALUES (1001, 'Spring Boot框架的核心配置文件是什么？它的主要作用是什么？', 4, 'Spring Boot的核心配置文件是application.properties或application.yml，用于配置应用程序的各种属性，如服务器端口、数据库连接等。', 3, 10, 1, 17, 1, 10, 'Spring Boot的核心配置文件是application.properties或application.yml，它主要用于配置应用程序的各种属性，包括服务器端口、数据库连接、日志配置等。', '[{\"keyword\":\"配置文件\",\"weight\":0.3},{\"keyword\":\"application\",\"weight\":0.3},{\"keyword\":\"属性配置\",\"weight\":0.2}]', '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1002, '请简述微服务架构的主要特点及其优势。', 4, '微服务架构是一种将单个应用程序拆分为多个小服务的架构风格，每个服务运行在独立进程中，服务之间通过轻量级通信机制协作。', 4, 15, 1, 17, 2, 10, '微服务架构的主要特点包括：1)单一职责，每个服务聚焦特定业务；2)独立部署，服务可独立部署和扩展；3)去中心化，不依赖统一技术栈；4)容错隔离，单个服务故障不影响整体。优势：提升开发效率、支持独立扩展、提高系统可靠性、便于技术迭代。', '[{\"keyword\":\"单一职责\",\"weight\":0.2},{\"keyword\":\"独立部署\",\"weight\":0.2},{\"keyword\":\"服务拆分\",\"weight\":0.2},{\"keyword\":\"容错隔离\",\"weight\":0.2}]', '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1003, '什么是Nacos？它在微服务架构中承担什么角色？', 4, 'Nacos是一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。', 3, 10, 1, 17, 2, 10, 'Nacos是阿里巴巴开源的项目，主要承担以下角色：1)服务发现与服务注册，支持DNS和RPC模式；2)配置管理，提供配置的热更新和版本管理；3)服务健康检查，确保服务可用性。它是微服务架构中的核心组件之一。', '[{\"keyword\":\"服务发现\",\"weight\":0.25},{\"keyword\":\"配置管理\",\"weight\":0.25},{\"keyword\":\"Nacos\",\"weight\":0.25}]', '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1004, '以下哪个不是Spring Cloud的核心组件？', 0, 'Ribbon是负载均衡组件，Feign是声明式服务调用，Hystrix是熔断器，Gateway是网关，它们都是Spring Cloud的组件。', 2, 5, 1, 17, 1, 10, NULL, NULL, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1005, '微服务架构中高可用的实现方式有哪些？', 1, '高可用实现方式包括：服务集群、负载均衡、熔断器、限流降级等。', 4, 8, 1, 17, 2, 10, NULL, NULL, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1006, '判断：Spring Boot自动配置的核心注解是@EnableAutoConfiguration。', 2, 'Spring Boot自动配置的核心注解确实是@EnableAutoConfiguration，配合@SpringBootApplication使用。', 2, 3, 1, 17, 1, 10, NULL, NULL, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1007, 'Nacos支持哪几种服务发现模式？', 1, 'Nacos支持两种服务发现模式：DNS-X和RPC模式。', 3, 5, 1, 17, 2, 10, NULL, NULL, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1008, '请描述Vue3相比Vue2有哪些重大改进？', 4, 'Vue3的重大改进包括：使用Proxy代替Object.defineProperty实现响应式、Composition API、更好的TypeScript支持、虚拟DOM重写、性能提升等。', 3, 12, 1, 6, 3, 10, 'Vue3相比Vue2的重大改进：1)响应式系统升级，使用Proxy代替Object.defineProperty；2)Composition API，提供更灵活的代码组织方式；3)更好的TypeScript支持；4)虚拟DOM重写，编译时优化；5)性能大幅提升，包体积更小。', '[{\"keyword\":\"Proxy\",\"weight\":0.2},{\"keyword\":\"Composition API\",\"weight\":0.25},{\"keyword\":\"TypeScript\",\"weight\":0.2},{\"keyword\":\"性能优化\",\"weight\":0.2}]', '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1009, 'Element Plus是什么？请列举至少3个常用组件。', 4, 'Element Plus是基于Vue3的UI组件库。', 2, 8, 1, 6, 3, 10, 'Element Plus是基于Vue 3的UI组件库，是Element UI的升级版。常用组件包括：1)Button按钮；2)Table表格；3)Form表单；4)Dialog对话框；5)Select选择器等。', '[{\"keyword\":\"Vue3\",\"weight\":0.2},{\"keyword\":\"组件库\",\"weight\":0.3},{\"keyword\":\"Element\",\"weight\":0.2}]', '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1010, 'MyBatis-Plus相比MyBatis有哪些优势？', 4, 'MyBatis-Plus在MyBatis基础上增强了很多功能。', 3, 10, 1, 6, 4, 10, 'MyBatis-Plus的优势：1)内置通用CRUD操作，无需编写重复SQL；2)强大的条件构造器，简化复杂查询；3)内置分页插件，开箱即用；4)自动填充功能；5)逻辑删除支持；6)性能分析插件。', '[{\"keyword\":\"CRUD\",\"weight\":0.2},{\"keyword\":\"条件构造器\",\"weight\":0.2},{\"keyword\":\"分页插件\",\"weight\":0.2},{\"keyword\":\"自动填充\",\"weight\":0.2}]', '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1011, 'Redis支持哪些数据结构？', 1, 'Redis支持5种基本数据结构：String、Hash、List、Set、Zset。', 2, 5, 1, 17, 5, 10, NULL, NULL, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question` VALUES (1012, '以下关于Redis的说法正确的是？', 0, 'Redis是内存数据库，但支持数据持久化。', 2, 4, 1, 17, 5, 10, NULL, NULL, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for eq_question_item
-- ----------------------------
DROP TABLE IF EXISTS `eq_question_item`;
CREATE TABLE `eq_question_item`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NOT NULL COMMENT '题目ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项内容',
  `is_correct` tinyint(1) NULL DEFAULT 0 COMMENT '是否正确答案',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2021 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目选项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eq_question_item
-- ----------------------------
INSERT INTO `eq_question_item` VALUES (2001, 1004, 'Ribbon', 0, 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2002, 1004, 'Feign', 0, 2, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2003, 1004, 'Hystrix', 0, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2004, 1004, 'Gateway', 0, 4, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2005, 1005, '服务集群部署', 1, 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2006, 1005, '负载均衡', 1, 2, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2007, 1005, '分布式事务', 0, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2008, 1005, '熔断器机制', 1, 4, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2009, 1007, 'DNS-X模式', 1, 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2010, 1007, 'RPC模式', 1, 2, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2011, 1007, 'SOA模式', 0, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2012, 1007, '微服务模式', 0, 4, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2013, 1011, 'String', 1, 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2014, 1011, 'Hash', 1, 2, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2015, 1011, 'Tree', 0, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2016, 1011, 'Set', 1, 4, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2017, 1012, 'Redis是纯内存数据库，无法持久化', 0, 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2018, 1012, 'Redis支持RDB和AOF两种持久化方式', 1, 2, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2019, 1012, 'Redis只能做缓存使用', 0, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_question_item` VALUES (2020, 1012, 'Redis不支持事务', 0, 4, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for eq_tags
-- ----------------------------
DROP TABLE IF EXISTS `eq_tags`;
CREATE TABLE `eq_tags`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `course_id` int NOT NULL COMMENT '课程ID',
  `teacher_id` int NOT NULL COMMENT '教师ID',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 108 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eq_tags
-- ----------------------------
INSERT INTO `eq_tags` VALUES (101, 'Spring基础', 17, 10, 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_tags` VALUES (102, 'SpringCloud', 17, 10, 2, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_tags` VALUES (103, 'Vue基础', 6, 10, 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_tags` VALUES (104, 'MyBatis', 6, 10, 2, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_tags` VALUES (105, 'Redis', 17, 10, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_tags` VALUES (106, '微服务架构', 17, 10, 4, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `eq_tags` VALUES (107, '数据库设计', 6, 10, 3, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

-- ----------------------------
-- Table structure for es_user
-- ----------------------------
DROP TABLE IF EXISTS `es_user`;
CREATE TABLE `es_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `picture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `role` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '角色：0学生、1教师',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：0启用，>0封禁',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 205 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of es_user
-- ----------------------------
INSERT INTO `es_user` VALUES (1, 'admin', '管理员', '123456', NULL, NULL, NULL, '1', 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `es_user` VALUES (201, 'teacher', '教师账户', '123456', NULL, NULL, NULL, '1', 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `es_user` VALUES (202, 'student001', '张三', '123456', NULL, NULL, NULL, '0', 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `es_user` VALUES (203, 'student002', '李四', '123456', NULL, NULL, NULL, '0', 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');
INSERT INTO `es_user` VALUES (204, 'student003', '王五', '123456', NULL, NULL, NULL, '0', 1, '2026-03-23 15:28:27', '2026-03-23 15:28:27');

SET FOREIGN_KEY_CHECKS = 1;
