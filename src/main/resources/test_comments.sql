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
(2, 4, '这个话题很有讨论价值。', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, 1),
(2, 1, '期待更多相关内容。', NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR, 1),

-- 帖子3的评论
(3, 3, '图片很精美！', NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR, 1),
(3, 1, '摄影技术不错👏', NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR, 1),

-- 帖子4的评论
(4, 4, '这个技术分享很实用。', NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR, 1),
(4, 2, '代码示例很清晰，收藏了！', NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR, 1),
(4, 3, '希望能有更多这样的教程。', NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR, 1),
(4, 1, '作者辛苦了，赞一个！', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, 1),

-- 帖子5的评论
(5, 1, '生活分享很温馨😊', NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR, 1),
(5, 3, '羡慕这样的生活状态。', NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR, 1),
(5, 4, '图片记录了美好时光。', NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR, 1);

-- 更新帖子的评论数量
UPDATE posts SET comments_count = (
    SELECT COUNT(*) FROM comments WHERE post_id = posts.post_id AND status = 1
) WHERE post_id IN (1, 2, 3, 4, 5);

-- 查看插入结果
SELECT 
    c.comment_id, c.post_id, c.user_id, c.content, 
    u.username, u.avatar_url,
    c.created_at
FROM comments c
LEFT JOIN users u ON c.user_id = u.user_id
WHERE c.status = 1
ORDER BY c.post_id, c.created_at; 