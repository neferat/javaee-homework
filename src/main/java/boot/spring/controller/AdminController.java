package boot.spring.controller;

import boot.spring.annotation.RequireAdminPermission;
import boot.spring.pagemodel.AjaxResult;
import boot.spring.pagemodel.DataGrid;
import boot.spring.po.Post;
import boot.spring.po.User;
import boot.spring.service.PostService;
import boot.spring.service.UserService;
import boot.spring.tools.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 提供admin用户的管理功能
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PostService postService;
    
    /**
     * 管理员面板页面
     */
    @RequestMapping("/dashboard")
    @RequireAdminPermission("访问管理面板")
    public String dashboard() {
        LOG.info("Admin访问管理面板");
        return "admin/dashboard";
    }
    
    /**
     * 用户管理页面
     */
    @RequestMapping("/users")
    @RequireAdminPermission("用户管理")
    public String usersPage() {
        return "admin/users";
    }
    
    /**
     * 帖子管理页面
     */
    @RequestMapping("/posts")
    @RequireAdminPermission("帖子管理")
    public String postsPage() {
        return "admin/posts";
    }
    
    // ============= 用户管理API =============
    
    /**
     * 获取所有用户（分页）
     */
    @GetMapping("/api/users")
    @ResponseBody
    @RequireAdminPermission("查看用户列表")
    public AjaxResult getAllUsers(@RequestParam(value = "current", defaultValue = "1") int current,
                                 @RequestParam(value = "rowCount", defaultValue = "10") int rowCount) {
        try {
            List<User> users = userService.getPageUsers(current, rowCount);
            int total = userService.getUserNum();
            
            DataGrid<User> grid = new DataGrid<>();
            grid.setCurrent(current);
            grid.setRowCount(rowCount);
            grid.setTotal(total);
            grid.setRows(users);
            
            LOG.info("Admin获取用户列表，页码：{}，每页：{}", current, rowCount);
            return AjaxResult.success(grid);
        } catch (Exception e) {
            LOG.error("获取用户列表失败", e);
            return AjaxResult.error("获取用户列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/api/users/{userId}")
    @ResponseBody
    @RequireAdminPermission("编辑用户")
    public AjaxResult updateUser(@PathVariable Long userId, @RequestBody User user) {
        try {
            user.setUserId(userId);
            User updatedUser = userService.updateUser(user);
            LOG.info("Admin更新用户信息，用户ID：{}", userId);
            return AjaxResult.success(updatedUser);
        } catch (Exception e) {
            LOG.error("更新用户失败", e);
            return AjaxResult.error("更新用户失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/api/users/{userId}")
    @ResponseBody
    @RequireAdminPermission("删除用户")
    public AjaxResult deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            LOG.info("Admin删除用户，用户ID：{}", userId);
            return AjaxResult.success("用户删除成功");
        } catch (Exception e) {
            LOG.error("删除用户失败", e);
            return AjaxResult.error("删除用户失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建用户
     */
    @PostMapping("/api/users")
    @ResponseBody
    @RequireAdminPermission("创建用户")
    public AjaxResult createUser(@RequestBody User user) {
        try {
            User newUser = userService.addUser(user);
            LOG.info("Admin创建用户：{}", user.getUsername());
            return AjaxResult.success(newUser);
        } catch (Exception e) {
            LOG.error("创建用户失败", e);
            return AjaxResult.error("创建用户失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除用户
     */
    @DeleteMapping("/api/users/batch")
    @ResponseBody
    @RequireAdminPermission("批量删除用户")
    public AjaxResult batchDeleteUsers(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> userIds = (List<Long>) request.get("userIds");
            
            if (userIds == null || userIds.isEmpty()) {
                return AjaxResult.error("请选择要删除的用户");
            }
            
            int deletedCount = 0;
            for (Long userId : userIds) {
                try {
                    userService.deleteUser(userId);
                    deletedCount++;
                } catch (Exception e) {
                    LOG.warn("删除用户{}失败: {}", userId, e.getMessage());
                }
            }
            
            LOG.info("Admin批量删除用户，成功删除{}个", deletedCount);
            return AjaxResult.success("成功删除" + deletedCount + "个用户");
        } catch (Exception e) {
            LOG.error("批量删除用户失败", e);
            return AjaxResult.error("批量删除失败：" + e.getMessage());
        }
    }
    
    // ============= 帖子管理API =============
    
    /**
     * 获取所有帖子（分页）
     */
    @GetMapping("/api/posts")
    @ResponseBody
    @RequireAdminPermission("查看帖子列表")
    public ResponseResult<DataGrid<Post>> getAllPosts(@RequestParam(value = "current", defaultValue = "1") int current,
                                                     @RequestParam(value = "rowCount", defaultValue = "10") int rowCount) {
        try {
            List<Post> posts = postService.getPagePosts(current, rowCount);
            int total = postService.getPostCount();
            
            DataGrid<Post> grid = new DataGrid<>();
            grid.setCurrent(current);
            grid.setRowCount(rowCount);
            grid.setTotal(total);
            grid.setRows(posts);
            
            LOG.info("Admin获取帖子列表，页码：{}，每页：{}", current, rowCount);
            return ResponseResult.success(grid);
        } catch (Exception e) {
            LOG.error("获取帖子列表失败", e);
            return ResponseResult.error("获取帖子列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新帖子信息
     */
    @PutMapping("/api/posts/{postId}")
    @ResponseBody
    @RequireAdminPermission("编辑帖子")
    public ResponseResult<Post> updatePost(@PathVariable Long postId, @RequestBody Post post) {
        try {
            post.setPostId(postId);
            Post updatedPost = postService.updatePost(post);
            LOG.info("Admin更新帖子信息，帖子ID：{}", postId);
            return ResponseResult.success(updatedPost);
        } catch (Exception e) {
            LOG.error("更新帖子失败", e);
            return ResponseResult.error("更新帖子失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除帖子
     */
    @DeleteMapping("/api/posts/{postId}")
    @ResponseBody
    @RequireAdminPermission("删除帖子")
    public ResponseResult<String> deletePost(@PathVariable Long postId) {
        try {
            postService.deletePost(postId);
            LOG.info("Admin删除帖子，帖子ID：{}", postId);
            return ResponseResult.success("帖子删除成功");
        } catch (Exception e) {
            LOG.error("删除帖子失败", e);
            return ResponseResult.error("删除帖子失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除帖子
     */
    @DeleteMapping("/api/posts/batch")
    @ResponseBody
    @RequireAdminPermission("批量删除帖子")
    public ResponseResult<String> batchDeletePosts(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> postIds = (List<Long>) request.get("postIds");
            
            if (postIds == null || postIds.isEmpty()) {
                return ResponseResult.error("请选择要删除的帖子");
            }
            
            int deletedCount = 0;
            for (Long postId : postIds) {
                try {
                    postService.deletePost(postId);
                    deletedCount++;
                } catch (Exception e) {
                    LOG.warn("删除帖子{}失败: {}", postId, e.getMessage());
                }
            }
            
            LOG.info("Admin批量删除帖子，成功删除{}个", deletedCount);
            return ResponseResult.success("成功删除" + deletedCount + "个帖子");
        } catch (Exception e) {
            LOG.error("批量删除帖子失败", e);
            return ResponseResult.error("批量删除失败：" + e.getMessage());
        }
    }
    
    // ============= 统计信息API =============
    
    /**
     * 获取管理面板统计信息
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    @RequireAdminPermission("查看统计信息")
    public ResponseResult<Map<String, Object>> getStatistics() {
        try {
            int userCount = userService.getUserNum();
            int postCount = postService.getPostCount();
            
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("userCount", userCount);
            stats.put("postCount", postCount);
            stats.put("activeUsers", userCount); // 简化统计，实际可以统计活跃用户
            stats.put("todayPosts", 0); // 可以添加今日发帖数统计
            
            LOG.info("Admin获取统计信息");
            return ResponseResult.success(stats);
        } catch (Exception e) {
            LOG.error("获取统计信息失败", e);
            return ResponseResult.error("获取统计信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查当前用户是否为管理员
     */
    @GetMapping("/api/check-admin")
    @ResponseBody
    public ResponseResult<Boolean> checkAdminStatus(HttpSession session) {
        String currentUsername = (String) session.getAttribute("currentUser");
        User userInfo = (User) session.getAttribute("userInfo");
        
        boolean isAdmin = false;
        if ("admin".equals(currentUsername)) {
            isAdmin = true;
        } else if (userInfo != null && userInfo.getUserLevel() != null && userInfo.getUserLevel() >= 5) {
            isAdmin = true;
        } else if (userInfo != null && userInfo.getUserId() != null && userInfo.getUserId().equals(1L)) {
            isAdmin = true;
        }
        
        return ResponseResult.success(isAdmin);
    }
} 