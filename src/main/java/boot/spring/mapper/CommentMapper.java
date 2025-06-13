package boot.spring.mapper;

import boot.spring.po.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    
    /**
     * 根据帖子ID获取评论列表
     * @param postId 帖子ID
     * @return 评论列表（包含用户信息）
     */
    List<Comment> getCommentsByPostId(@Param("postId") Long postId);
    
    /**
     * 根据评论ID获取评论
     * @param commentId 评论ID
     * @return 评论对象
     */
    Comment getCommentById(@Param("commentId") Long commentId);
    
    /**
     * 插入新评论
     * @param comment 评论对象
     * @return 影响的行数
     */
    int insertComment(Comment comment);
    
    /**
     * 更新评论内容
     * @param comment 评论对象
     * @return 影响的行数
     */
    int updateComment(Comment comment);
    
    /**
     * 删除评论（软删除）
     * @param commentId 评论ID
     * @param userId 用户ID（用于权限验证）
     * @return 影响的行数
     */
    int deleteComment(@Param("commentId") Long commentId, @Param("userId") Long userId);
    
    /**
     * 统计帖子的评论数量
     * @param postId 帖子ID
     * @return 评论数量
     */
    int countCommentsByPostId(@Param("postId") Long postId);
    
    /**
     * 获取用户的评论列表
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Comment> getCommentsByUserId(@Param("userId") Long userId);
} 