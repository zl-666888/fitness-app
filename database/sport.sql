-- ============================================================
-- 健身打卡小程序 - 数据库初始化脚本
-- 数据库名：sport
-- 用户名：root
-- 密码：6666
-- 字符集：utf8mb4（支持emoji和特殊字符）
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `sport`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `sport`;

-- ============================================================
-- 1. 用户表（users）
-- ============================================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id`          INT           NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(50)   NOT NULL COMMENT '用户名（唯一）',
  `password`    VARCHAR(255)  NOT NULL COMMENT '密码（bcrypt加密）',
  `nickname`    VARCHAR(50)   DEFAULT NULL COMMENT '昵称',
  `avatar`      VARCHAR(255)  DEFAULT NULL COMMENT '头像URL',
  `height`      DECIMAL(5,2)  DEFAULT NULL COMMENT '身高（cm）',
  `weight`      DECIMAL(5,2)  DEFAULT NULL COMMENT '体重（kg）',
  `gender`      TINYINT       DEFAULT 0 COMMENT '性别（0-未知，1-男，2-女）',
  `birth_date`  DATE          DEFAULT NULL COMMENT '出生日期',
  `phone`       VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
  `status`      TINYINT       DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
  `created_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `updated_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 管理员表（admins）
-- ============================================================
DROP TABLE IF EXISTS `admins`;
CREATE TABLE `admins` (
  `id`          INT           NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username`    VARCHAR(50)   NOT NULL COMMENT '管理员账号（唯一）',
  `password`    VARCHAR(255)  NOT NULL COMMENT '密码（bcrypt加密）',
  `role`        ENUM('super', 'admin') NOT NULL DEFAULT 'admin' COMMENT '角色：super-超级管理员，admin-普通管理员',
  `real_name`   VARCHAR(50)   DEFAULT NULL COMMENT '真实姓名',
  `status`      TINYINT       DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
  `created_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_username` (`username`),
  KEY `idx_admin_role` (`role`),
  KEY `idx_admin_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ============================================================
-- 3. 打卡记录表（checkins）
-- ============================================================
DROP TABLE IF EXISTS `checkins`;
CREATE TABLE `checkins` (
  `id`              INT           NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id`         INT           NOT NULL COMMENT '用户ID',
  `checkin_date`    DATE          NOT NULL COMMENT '打卡日期（同一天只允许一条）',
  `checkin_time`    TIME          DEFAULT NULL COMMENT '打卡时间',
  `duration`        INT           DEFAULT NULL COMMENT '运动时长（分钟）',
  `type`            VARCHAR(50)   DEFAULT NULL COMMENT '运动类型（跑步/力量/瑜伽等）',
  `intensity`       ENUM('low','medium','high') DEFAULT 'medium' COMMENT '运动强度',
  `content`         TEXT          DEFAULT NULL COMMENT '打卡备注',
  `calories_burned` INT           DEFAULT NULL COMMENT '消耗卡路里（kcal）',
  `created_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_checkin_date` (`checkin_date`),
  CONSTRAINT `fk_checkin_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡记录表';

-- ============================================================
-- 4. 健身视频表（videos）
-- ============================================================
DROP TABLE IF EXISTS `videos`;
CREATE TABLE `videos` (
  `id`          INT           NOT NULL AUTO_INCREMENT COMMENT '视频ID',
  `title`       VARCHAR(200)  NOT NULL COMMENT '视频标题',
  `description` TEXT          DEFAULT NULL COMMENT '视频描述',
  `cover_url`   VARCHAR(255)  DEFAULT NULL COMMENT '封面图URL',
  `video_url`   VARCHAR(255)  NOT NULL COMMENT '视频播放URL',
  `category`    VARCHAR(50)   DEFAULT NULL COMMENT '分类（胸部/腿部/核心/全身等）',
  `duration`    INT           DEFAULT NULL COMMENT '视频时长（秒）',
  `difficulty`  ENUM('beginner','intermediate','advanced') DEFAULT 'beginner' COMMENT '难度等级',
  `coach`       VARCHAR(50)   DEFAULT NULL COMMENT '教练/演示者',
  `sort_order`  INT           DEFAULT 0 COMMENT '排序权重',
  `status`      TINYINT       DEFAULT 1 COMMENT '状态（0-下架，1-上架）',
  `created_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_video_category` (`category`),
  KEY `idx_video_difficulty` (`difficulty`),
  KEY `idx_video_status` (`status`),
  KEY `idx_video_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身视频表';

-- ============================================================
-- 5. 食物热量表（foods）
-- ============================================================
DROP TABLE IF EXISTS `foods`;
CREATE TABLE `foods` (
  `id`            INT           NOT NULL AUTO_INCREMENT COMMENT '食物ID',
  `name`          VARCHAR(100)  NOT NULL COMMENT '食物名称',
  `category`      VARCHAR(50)   DEFAULT NULL COMMENT '分类（主食/水果/肉类/蔬菜等）',
  `calories`      DECIMAL(8,2)  DEFAULT 0.00 COMMENT '每100g热量（kcal）',
  `protein`       DECIMAL(8,2)  DEFAULT 0.00 COMMENT '蛋白质含量（g/100g）',
  `fat`           DECIMAL(8,2)  DEFAULT 0.00 COMMENT '脂肪含量（g/100g）',
  `carbohydrate`  DECIMAL(8,2)  DEFAULT 0.00 COMMENT '碳水化合物含量（g/100g）',
  `fiber`         DECIMAL(8,2)  DEFAULT 0.00 COMMENT '膳食纤维含量（g/100g）',
  `unit`          VARCHAR(20)   DEFAULT 'g' COMMENT '单位（g/份/个）',
  `image_url`     VARCHAR(255)  DEFAULT NULL COMMENT '食物图片URL',
  `status`        TINYINT       DEFAULT 1 COMMENT '状态（0-下架，1-上架）',
  `created_at`    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_food_name` (`name`),
  KEY `idx_food_category` (`category`),
  KEY `idx_food_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食物热量表';

-- ============================================================
-- 6. 打卡点赞表（checkin_likes）
-- ============================================================
DROP TABLE IF EXISTS `checkin_likes`;
CREATE TABLE `checkin_likes` (
  `id`          INT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `checkin_id`  INT       NOT NULL COMMENT '打卡记录ID',
  `user_id`     INT       NOT NULL COMMENT '点赞用户ID',
  `created_at`  DATETIME  DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_checkin_user` (`checkin_id`, `user_id`),
  KEY `idx_likes_checkin_id` (`checkin_id`),
  KEY `idx_likes_user_id` (`user_id`),
  CONSTRAINT `fk_like_checkin` FOREIGN KEY (`checkin_id`) REFERENCES `checkins` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡点赞表';

-- ============================================================
-- 7. 打卡评论表（checkin_comments）
-- ============================================================
DROP TABLE IF EXISTS `checkin_comments`;
CREATE TABLE `checkin_comments` (
  `id`          INT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `checkin_id`  INT       NOT NULL COMMENT '打卡记录ID',
  `user_id`     INT       NOT NULL COMMENT '评论用户ID',
  `content`     TEXT      NOT NULL COMMENT '评论内容',
  `created_at`  DATETIME  DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_checkin_id` (`checkin_id`),
  KEY `idx_comment_user_id` (`user_id`),
  CONSTRAINT `fk_comment_checkin` FOREIGN KEY (`checkin_id`) REFERENCES `checkins` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡评论表';

-- ============================================================
-- 8. 用户目标表（user_goals）
-- ============================================================
DROP TABLE IF EXISTS `user_goals`;
CREATE TABLE `user_goals` (
  `id`              INT           NOT NULL AUTO_INCREMENT COMMENT '目标ID',
  `user_id`         INT           NOT NULL COMMENT '用户ID',
  `goal_type`       VARCHAR(50)   DEFAULT NULL COMMENT '目标类型（减脂/增肌/塑形/保持健康）',
  `target_weight`   DECIMAL(5,2)  DEFAULT NULL COMMENT '目标体重（kg）',
  `weekly_frequency` INT          DEFAULT NULL COMMENT '每周运动次数',
  `reminder_time`   TIME          DEFAULT NULL COMMENT '提醒时间',
  `created_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_goal` (`user_id`),
  CONSTRAINT `fk_goal_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户目标表';

-- ============================================================
-- 9. 用户关注表（user_follows） - 社交功能
-- ============================================================
DROP TABLE IF EXISTS `user_follows`;
CREATE TABLE `user_follows` (
  `id`            INT       NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `follower_id`   INT       NOT NULL COMMENT '关注者用户ID',
  `following_id`  INT       NOT NULL COMMENT '被关注用户ID',
  `created_at`    DATETIME  DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follow` (`follower_id`, `following_id`),
  KEY `idx_follower` (`follower_id`),
  KEY `idx_following` (`following_id`),
  CONSTRAINT `fk_follow_follower` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_follow_following` FOREIGN KEY (`following_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注表';

-- ============================================================
-- 10. 视频收藏表（video_favorites） - 用户收藏视频
-- ============================================================
DROP TABLE IF EXISTS `video_favorites`;
CREATE TABLE `video_favorites` (
  `id`          INT       NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id`     INT       NOT NULL COMMENT '用户ID',
  `video_id`    INT       NOT NULL COMMENT '视频ID',
  `created_at`  DATETIME  DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fav_video_user` (`user_id`, `video_id`),
  KEY `idx_fav_user_id` (`user_id`),
  KEY `idx_fav_video_id` (`video_id`),
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_fav_video` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频收藏表';

-- ============================================================
-- 11. 操作日志表（operation_logs） - 管理员操作记录
-- ============================================================
DROP TABLE IF EXISTS `operation_logs`;
CREATE TABLE `operation_logs` (
  `id`          INT           NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `admin_id`    INT           NOT NULL COMMENT '操作管理员ID',
  `action`      VARCHAR(100)  NOT NULL COMMENT '操作类型（如：delete_user, add_video 等）',
  `target_type` VARCHAR(50)   DEFAULT NULL COMMENT '操作对象类型（user/video/food/admin）',
  `target_id`   INT           DEFAULT NULL COMMENT '操作对象ID',
  `detail`      TEXT          DEFAULT NULL COMMENT '操作详情（JSON格式）',
  `ip_address`  VARCHAR(45)   DEFAULT NULL COMMENT '操作IP',
  `created_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_log_admin_id` (`admin_id`),
  KEY `idx_log_action` (`action`),
  KEY `idx_log_created_at` (`created_at`),
  CONSTRAINT `fk_log_admin` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================================
-- 插入初始数据
-- ============================================================

-- 插入超级管理员账号
-- 密码：admin888（正式使用前请修改！）
-- bcrypt 加密后的密码：$2a$10$...（实际使用时需用程序生成）
-- 这里使用占位符，实际需用程序注册后替换
INSERT INTO `admins` (`username`, `password`, `role`, `real_name`) VALUES
('superadmin', '$2a$10$1RIzc67E/7mNOmvg93fy4Ota1qGZgwsQiUJQgU9PXfCb6VCcpjgO2', 'super', '系统管理员');

-- 插入示例视频分类数据（可选）
-- 可自行按需插入

-- ============================================================
-- 查看所有表结构确认
-- ============================================================
-- 使用以下命令确认所有表已创建成功：
-- SHOW TABLES;
-- DESC users;
-- DESC admins;
-- DESC checkins;
-- DESC videos;
-- DESC foods;
-- DESC checkin_likes;
-- DESC checkin_comments;
-- DESC user_goals;
-- DESC user_follows;
-- DESC video_favorites;
-- DESC operation_logs;
