-- 创建admin用户并设置权限
USE rest;

-- 插入admin用户（如果不存在）
INSERT IGNORE INTO users (user_id, username, password, email, phone_number, avatar_url, bio, registration_date, points, user_level, status, third_party_platform, third_party_id) 
VALUES 
(1, 'admin', '123456', 'admin@example.com', '13800000000', 'https://api.dicebear.com/7.x/initials/svg?seed=Admin&backgroundColor=6366f1', '系统管理员', NOW(), 10000, 5, 'active', NULL, NULL);

-- 创建普通测试用户
INSERT IGNORE INTO users (user_id, username, password, email, phone_number, avatar_url, bio, registration_date, points, user_level, status, third_party_platform, third_party_id) 
VALUES 
(2, 'testuser', '123456', 'test@example.com', '13800000001', 'https://api.dicebear.com/7.x/initials/svg?seed=Test&backgroundColor=10b981', '测试用户', NOW(), 100, 1, 'active', NULL, NULL),
(3, 'user1', '123456', 'user1@example.com', '13800000002', 'https://api.dicebear.com/7.x/initials/svg?seed=User1&backgroundColor=f59e0b', '普通用户1', NOW(), 50, 1, 'active', NULL, NULL),
(4, 'user2', '123456', 'user2@example.com', '13800000003', 'https://api.dicebear.com/7.x/initials/svg?seed=User2&backgroundColor=ef4444', '普通用户2', NOW(), 30, 1, 'active', NULL, NULL);

-- 更新admin用户信息，确保是管理员权限
UPDATE users SET 
    user_level = 5,
    points = 10000,
    bio = '系统管理员 - 拥有所有权限'
WHERE username = 'admin';

-- 查看创建的用户
SELECT user_id, username, email, user_level, points, status, bio 
FROM users 
ORDER BY user_id;

-- 显示admin用户的详细信息
SELECT * FROM users WHERE username = 'admin'; 