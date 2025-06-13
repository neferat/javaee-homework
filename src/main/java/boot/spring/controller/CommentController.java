package boot.spring.controller;

import boot.spring.po.Comment;
import boot.spring.service.CommentService;
import boot.spring.tools.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;
    
    private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);
    
    /**
     * 获取帖子的评论列表
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseResult<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPostId(postId);
            LOG.info("获取帖子{}的评论列表，共{}条", postId, comments.size());
            return ResponseResult.success(comments);
        } catch (Exception e) {
            LOG.error("获取评论列表失败", e);
            return ResponseResult.error("获取评论列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 发表评论
     */
    @PostMapping("/comments")
    public ResponseResult<Comment> addComment(@RequestBody Map<String, Object> request,
                                            HttpSession session) {
        try {
            // 检查用户登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                // 尝试从session获取用户名，然后获取用户ID
                String username = (String) session.getAttribute("currentUser");
                if (username != null) {
                    // 可以通过UserService根据用户名获取用户ID
                    // 这里暂时使用默认用户ID，实际项目中应该通过UserService获取
                    userId = 1L; // 临时解决方案
                } else {
                    return ResponseResult.error("请先登录");
                }
            }
            
            // 获取帖子ID和评论内容
            Object postIdObj = request.get("postId");
            Long postId = null;
            if (postIdObj instanceof Number) {
                postId = ((Number) postIdObj).longValue();
            } else if (postIdObj instanceof String) {
                postId = Long.valueOf((String) postIdObj);
            }
            
            if (postId == null) {
                return ResponseResult.error("帖子ID不能为空");
            }
            
            String content = (String) request.get("content");
            if (!StringUtils.hasText(content)) {
                return ResponseResult.error("评论内容不能为空");
            }
            
            // 内容长度限制
            if (content.length() > 500) {
                return ResponseResult.error("评论内容不能超过500字");
            }
            
            // 添加评论
            Comment comment = commentService.addComment(postId, userId, content.trim());
            
            LOG.info("用户{}在帖子{}发表评论", userId, postId);
            return ResponseResult.success(comment);
        } catch (Exception e) {
            LOG.error("发表评论失败", e);
            return ResponseResult.error("发表评论失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新评论
     */
    @PutMapping("/comments/{commentId}")
    public ResponseResult<Comment> updateComment(@PathVariable Long commentId,
                                               @RequestBody Map<String, String> request,
                                               HttpSession session) {
        try {
            // 检查用户登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                // 尝试从session获取用户名
                String username = (String) session.getAttribute("currentUser");
                if (username != null) {
                    userId = 1L; // 临时解决方案
                } else {
                    return ResponseResult.error("请先登录");
                }
            }
            
            // 获取评论内容
            String content = request.get("content");
            if (!StringUtils.hasText(content)) {
                return ResponseResult.error("评论内容不能为空");
            }
            
            // 内容长度限制
            if (content.length() > 500) {
                return ResponseResult.error("评论内容不能超过500字");
            }
            
            // 更新评论
            Comment comment = commentService.updateComment(commentId, userId, content.trim());
            
            LOG.info("用户{}更新评论{}", userId, commentId);
            return ResponseResult.success(comment);
        } catch (Exception e) {
            LOG.error("更新评论失败", e);
            return ResponseResult.error("更新评论失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseResult<Void> deleteComment(@PathVariable Long commentId,
                                            HttpSession session) {
        try {
            // 检查用户登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                // 尝试从session获取用户名
                String username = (String) session.getAttribute("currentUser");
                if (username != null) {
                    userId = 1L; // 临时解决方案
                } else {
                    return ResponseResult.error("请先登录");
                }
            }
            
            // 删除评论
            commentService.deleteComment(commentId, userId);
            
            LOG.info("用户{}删除评论{}", userId, commentId);
            return ResponseResult.success(null);
        } catch (Exception e) {
            LOG.error("删除评论失败", e);
            return ResponseResult.error("删除评论失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取评论详情
     */
    @GetMapping("/comments/{commentId}")
    public ResponseResult<Comment> getCommentById(@PathVariable Long commentId) {
        try {
            Comment comment = commentService.getCommentById(commentId);
            if (comment == null) {
                return ResponseResult.error("评论不存在");
            }
            
            LOG.info("获取评论{}详情", commentId);
            return ResponseResult.success(comment);
        } catch (Exception e) {
            LOG.error("获取评论详情失败", e);
            return ResponseResult.error("获取评论详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取帖子评论数量
     */
    @GetMapping("/posts/{postId}/comments/count")
    public ResponseResult<Integer> getCommentCount(@PathVariable Long postId) {
        try {
            int count = commentService.getCommentCount(postId);
            return ResponseResult.success(count);
        } catch (Exception e) {
            LOG.error("获取评论数量失败", e);
            return ResponseResult.error("获取评论数量失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的评论列表
     */
    @GetMapping("/users/{userId}/comments")
    public ResponseResult<List<Comment>> getUserComments(@PathVariable Long userId) {
        try {
            List<Comment> comments = commentService.getCommentsByUserId(userId);
            LOG.info("获取用户{}的评论列表，共{}条", userId, comments.size());
            return ResponseResult.success(comments);
        } catch (Exception e) {
            LOG.error("获取用户评论列表失败", e);
            return ResponseResult.error("获取用户评论列表失败：" + e.getMessage());
        }
    }
} 