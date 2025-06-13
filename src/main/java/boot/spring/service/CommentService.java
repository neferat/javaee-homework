package boot.spring.service;

import boot.spring.po.Comment;

import java.util.List;

public interface CommentService {
    
    /**
     * 根据帖子ID获取评论列表
     * @param postId 帖子ID
     * @return 评论列表（包含用户信息）
     */
    List<Comment> getCommentsByPostId(Long postId);
    
    /**
     * 根据评论ID获取评论
     * @param commentId 评论ID
     * @return 评论对象
     */
    Comment getCommentById(Long commentId);
    
    /**
     * 添加评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @return 新增的评论对象（包含评论ID）
     */
    Comment addComment(Long postId, Long userId, String content);
    
    /**
     * 更新评论内容
     * @param commentId 评论ID
     * @param userId 用户ID（用于权限验证）
     * @param content 新的评论内容
     * @return 更新后的评论对象
     */
    Comment updateComment(Long commentId, Long userId, String content);
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID（用于权限验证）
     */
    void deleteComment(Long commentId, Long userId);
    
    /**
     * 统计帖子的评论数量
     * @param postId 帖子ID
     * @return 评论数量
     */
    int getCommentCount(Long postId);
    
    /**
     * 获取用户的评论列表
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Comment> getCommentsByUserId(Long userId);
} 