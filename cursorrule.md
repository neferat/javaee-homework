# Cursor 代码生成规范（cursorrule.md）

> 本文档为本项目自动代码生成的强制规范。**每次生成代码时，必须严格遵循本规范，禁止引入任何未在本规范允许的外部库依赖。**

---

## 1. 统一响应格式

### 1.1 AjaxResult（传统接口/页面）
- 返回结构：
```json
{
  "code": 200,      // 200成功，301警告，500错误
  "msg": "操作成功", // 响应消息
  "data": {}        // 响应数据
}
```
- 推荐使用 `AjaxResult.success(data)`、`AjaxResult.error(msg)` 静态方法。

### 1.2 ResponseResult（RESTful API）
- 返回结构：
```json
{
  "code": 200,         // 200成功，500错误
  "message": "success", // 响应消息
  "data": {}           // 响应数据
}
```
- 推荐使用 `ResponseResult.success(data)`、`ResponseResult.error(msg)` 静态方法。

## 2. 分页数据格式
- 统一使用 DataGrid：
```json
{
  "current": 1,    // 当前页码
  "rowCount": 10,  // 每页行数
  "total": 100,    // 总记录数
  "rows": []       // 数据列表
}
```

## 3. API 命名与路由规范
- RESTful API 路径以 `/api` 开头，资源用复数名词。
- 操作用 HTTP 方法区分（GET/POST/PUT/DELETE）。
- 传统接口用动词+名词，返回页面或 AjaxResult。
- 示例：
  - `GET /api/posts` 获取所有帖子
  - `POST /api/posts` 新建帖子
  - `PUT /api/posts/{id}` 更新帖子
  - `DELETE /api/posts/{id}` 删除帖子
  - `GET /login` 登录页面
  - `POST /loginvalidate` 登录校验

## 4. 错误处理
- 必须使用全局异常处理（如 `@RestControllerAdvice`），统一返回错误格式。
- 业务异常需自定义异常类，Service 层抛出。
- 日志必须记录异常堆栈。

## 5. 安全规范
- 登录校验用 Session+AOP 拦截，未登录重定向到登录页。
- 前端基础校验，后端严格校验。
- 敏感数据传输需加密（如密码）。

## 6. 文件上传与下载
- 上传用 MultipartFile，限制类型和大小。
- 下载需设置 Content-Type、Content-Disposition，流式传输。

## 7. 日志规范
- 关键操作、异常必须用 SLF4J 记录日志。
- 日志级别：ERROR > WARN > INFO > DEBUG。

## 8. 代码结构与生成要求
- 实体类必须实现 Serializable，字段加注释。
- Controller 用 `@Controller` 或 `@RestController`，方法加 `@RequestMapping`。
- Service 层分接口和实现，`@Service` 注解，事务用 `@Transactional`。
- DAO 层用 MyBatis 注解或 XML，基础 CRUD 必须实现。
- **禁止引入任何未在本项目已有的外部依赖库。**

---

**所有自动生成的代码必须严格遵循本规范，否则视为不合规。** 