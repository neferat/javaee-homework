# 🔧 404错误诊断和解决指南

## 📋 问题分析

你遇到的404错误可能由以下几个原因引起：

### 1. **登录状态问题** ⚠️
项目有 `LoginAspect` 切面拦截所有API请求检查登录状态。

### 2. **数据库连接问题**
项目使用多数据源（sakila, rest），需要确保数据库连接正常。

### 3. **评论表不存在**
需要先创建评论表才能使用评论功能。

## 🚀 解决步骤

### 第一步：快速登录测试
1. 访问浏览器：`http://localhost:8080/test/init`
2. 应该看到：`{"code":200,"data":"测试用户登录成功！..."}`
3. 检查登录状态：`http://localhost:8080/test/status`

### 第二步：检查数据库
1. 确保MySQL数据库 `sakila` 或 `rest` 存在并可连接
2. 执行 `comments_table.sql` 创建评论表：

```sql
-- 连接到你的MySQL数据库，执行以下SQL
USE sakila; -- 或者 USE rest;

-- 创建评论表
CREATE TABLE comments (
    comment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID', 
    content TEXT NOT NULL COMMENT '评论内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-已删除',
    
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 给帖子表添加评论计数字段（如果没有的话）
ALTER TABLE posts ADD COLUMN comment_count INT DEFAULT 0 COMMENT '评论数量';
```

### 第三步：验证功能
1. 重启应用
2. 访问：`http://localhost:8080` 
3. 点击帖子下方的评论图标
4. 测试发表和删除评论

## 🔍 调试命令

### 检查登录状态
```bash
# 访问测试页面
curl http://localhost:8080/test/status

# 快速登录
curl http://localhost:8080/test/init
```

### 检查API是否工作
```bash
# 获取帖子列表
curl http://localhost:8080/api/posts

# 获取评论列表（需要先登录）
curl http://localhost:8080/api/posts/1/comments
```

## ⚠️ 常见错误处理

### 1. 如果登录后仍然404
可能是数据库连接问题，检查 `application.yml` 中的数据库配置：
- 用户名密码是否正确
- 数据库是否存在
- 端口是否正确

### 2. 如果评论表创建失败
检查是否有创建表的权限，或者表是否已存在。

### 3. 如果JavaScript错误
打开浏览器开发者工具（F12）查看控制台错误信息。

## 🎯 快速修复

如果你想跳过登录验证进行测试，可以临时修改 `LoginAspect.java`：

```java
// 在 LoginAspect.java 的 auth 方法中临时添加：
if (requestURI.contains("/api/posts") && requestURI.contains("/comments")) {
    return joinPoint.proceed(); // 跳过评论相关API的登录检查
}
```

## 📞 如果仍有问题

1. 查看控制台日志输出
2. 检查浏览器网络选项卡中的具体404请求
3. 确认Spring Boot应用正常启动
4. 检查端口8080是否被占用

按照以上步骤操作，应该能解决404问题！ 