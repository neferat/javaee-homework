-- 更新用户头像为可用的默认头像
USE sakila;

-- 更新所有用户的头像为可访问的默认头像
UPDATE users SET avatar_url = 'https://api.dicebear.com/7.x/initials/svg?seed=Admin&backgroundColor=6366f1' WHERE username = 'admin';
UPDATE users SET avatar_url = 'https://api.dicebear.com/7.x/initials/svg?seed=User1&backgroundColor=10b981' WHERE username = 'user1';
UPDATE users SET avatar_url = 'https://api.dicebear.com/7.x/initials/svg?seed=User2&backgroundColor=f59e0b' WHERE username = 'user2';
UPDATE users SET avatar_url = 'https://api.dicebear.com/7.x/initials/svg?seed=Test&backgroundColor=ef4444' WHERE username = 'test';
UPDATE users SET avatar_url = 'https://api.dicebear.com/7.x/initials/svg?seed=Guest&backgroundColor=8b5cf6' WHERE username = 'guest';

-- 查看更新后的用户头像
SELECT user_id, username, avatar_url FROM users ORDER BY user_id;

-- 如果用户头像为空，则设置默认头像
UPDATE users 
SET avatar_url = CONCAT('https://api.dicebear.com/7.x/initials/svg?seed=', username, '&backgroundColor=6b7280') 
WHERE avatar_url IS NULL OR avatar_url = '' OR avatar_url LIKE '/images/avatars/%'; 