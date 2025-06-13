-- 评论功能数据库表创建脚本
-- 执行时间：请在MySQL中手动执行以下SQL语句

-- 1. 创建评论表
CREATE TABLE comments (
    comment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID', 
    content TEXT NOT NULL COMMENT '评论内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-已删除',
    
    -- 索引优化
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 2. 给帖子表添加评论计数字段
ALTER TABLE posts ADD COLUMN comment_count INT DEFAULT 0 COMMENT '评论数量';

-- 3. 初始化现有帖子的评论数量（可选）
UPDATE posts SET comment_count = 0 WHERE comment_count IS NULL;

-- 4. 插入一些测试数据（可选）
INSERT INTO comments (post_id, user_id, content) VALUES 
(1, 1, '这是第一条评论'),
(1, 2, '很不错的帖子！'),
(2, 1, '有趣的内容'),
(2, 3, '学到了很多');

-- 5. 更新帖子评论数量（基于现有评论）
UPDATE posts p SET comment_count = (
    SELECT COUNT(*) FROM comments c 
    WHERE c.post_id = p.post_id AND c.status = 1
);

-- 执行完成后，可以通过以下SQL验证：
-- SELECT * FROM comments;
-- SELECT post_id, comment_count FROM posts; 