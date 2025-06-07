-- 创建users表和插入测试数据
USE sakila;

-- 创建users表
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone_number VARCHAR(20),
    avatar_url VARCHAR(255),
    bio TEXT,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    points INT DEFAULT 0,
    user_level INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'active',
    third_party_platform VARCHAR(50),
    third_party_id VARCHAR(100),
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
);

-- 插入测试用户数据
INSERT INTO users (username, password, email, phone_number, avatar_url, bio, points, user_level, status) VALUES
('admin', '123456', 'admin@example.com', '13800138000', '/images/avatars/admin.jpg', '论坛管理员', 1000, 5, 'active'),
('user1', '123456', 'user1@example.com', '13800138001', '/images/avatars/user1.jpg', '热爱技术分享的程序员', 500, 3, 'active'),
('user2', '123456', 'user2@example.com', '13800138002', '/images/avatars/user2.jpg', '喜欢学习新技术', 300, 2, 'active'),
('test', '123456', 'test@example.com', '13800138003', '/images/avatars/test.jpg', '测试用户', 100, 1, 'active'),
('guest', '123456', 'guest@example.com', '13800138004', '/images/avatars/guest.jpg', '访客用户', 50, 1, 'active');

-- 检查创建的表结构
DESCRIBE users;

-- 查看插入的数据
SELECT user_id, username, email, bio, points, user_level, status, registration_date FROM users ORDER BY user_id; 