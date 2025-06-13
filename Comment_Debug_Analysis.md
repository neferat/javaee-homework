# 评论获取和显示问题调试分析

## 问题描述
- 图标消失
- 获取不到评论的用户和头像
- 可以获取到评论数据

## 已确认的技术实现

### 1. 后端数据结构
- Comment 实体类包含：`username` 和 `userAvatar` 字段
- CommentMapper.xml 正确关联用户表：`u.username, u.avatar_url as user_avatar`
- SQL 查询包含 LEFT JOIN users 表

### 2. 前端修复
✅ **已修复**：`createModalCommentElement` 函数
- 原错误：`comment.user.username` 和 `comment.user.avatarUrl`
- 修复为：`comment.username` 和 `comment.userAvatar`

### 3. Font Awesome 图标
✅ **CDN 正确**：使用 Font Awesome 6.4.0 版本

## 可能的问题源

### 1. 数据库数据问题
需要检查：
- users 表中是否有实际的 avatar_url 数据
- 评论数据中的 user_id 是否正确关联到 users 表

### 2. API 响应格式
需要验证 `/api/posts/{postId}/comments` 返回的数据格式

### 3. 前端数据处理
检查浏览器开发者工具中的网络请求和响应

## 调试步骤

### Step 1: 检查数据库数据
```sql
-- 检查用户表数据
SELECT user_id, username, avatar_url FROM users LIMIT 5;

-- 检查评论关联查询
SELECT c.comment_id, c.content, c.user_id, u.username, u.avatar_url 
FROM comments c 
LEFT JOIN users u ON c.user_id = u.user_id 
WHERE c.status = 1 
LIMIT 5;
```

### Step 2: 检查API响应
在浏览器开发者工具中查看 `/api/posts/{postId}/comments` 的响应数据

### Step 3: 前端调试
在 `createModalCommentElement` 函数中添加 console.log：
```javascript
console.log('Comment data:', comment);
console.log('Username:', comment.username);
console.log('UserAvatar:', comment.userAvatar);
```

## 临时解决方案
如果用户头像为空，使用默认头像：
- 头像URL：`https://via.placeholder.com/32`
- 用户名：显示为"匿名用户"

## 后续优化
1. 为新用户设置默认头像
2. 添加头像上传功能
3. 完善用户信息编辑功能 