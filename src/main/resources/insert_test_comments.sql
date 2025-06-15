-- 创建测试评论数据
USE rest;

-- 先确保有用户数据
INSERT IGNORE INTO users (user_id, username, password, email, avatar_url, created_at, updated_at) VALUES
(1, '张三', '123456', 'zhangsan@example.com', 'images/avatars/user1.jpg', NOW(), NOW()),
(2, '李四', '123456', 'lisi@example.com', 'images/avatars/user2.jpg', NOW(), NOW()),
(3, '王五', '123456', 'wangwu@example.com', 'images/avatars/user3.jpg', NOW(), NOW()),
(4, '赵六', '123456', 'zhaoliu@example.com', 'images/avatars/user4.jpg', NOW(), NOW());

-- 插入测试评论数据（假设帖子ID为1-5）
INSERT IGNORE INTO comments (post_id, user_id, content, created_at, updated_at, status) VALUES
-- 帖子1的评论
(1, 1, '这个帖子写得很好！👍', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, 1),
(1, 2, '我也觉得，内容很有意思。', NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR, 1),
(1, 3, '学到了很多，感谢分享！', NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 30 MINUTE, 1),

-- 帖子2的评论  
(2, 2, '支持楼主的观点！', NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR, 1),
(2, 4, '非常有启发性的内容', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, 1),

-- 帖子3的评论
(3, 1, '很实用的教程，收藏了！', NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR, 1),
(3, 3, '希望能出更多这样的内容', NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR, 1),

-- 帖子4的评论
(4, 2, '写得太棒了！', NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR, 1),
(4, 4, '期待下一篇文章', NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR, 1),

-- 帖子5的评论
(5, 1, '这个观点很新颖', NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR, 1),
(5, 3, '受益匪浅，谢谢分享', NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR, 1);

-- 显示插入结果
SELECT 
    c.comment_id,
    c.post_id,
    u.username,
    c.content,
    c.created_at,
    c.status
FROM comments c
LEFT JOIN users u ON c.user_id = u.user_id
WHERE c.status = 1
ORDER BY c.post_id, c.created_at DESC; 