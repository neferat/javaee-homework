package boot.spring.service.impl;

import boot.spring.mapper.CommentMapper;
import boot.spring.po.Comment;
import boot.spring.service.CommentService;
import boot.spring.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private PostService postService;

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentMapper.getCommentsByPostId(postId);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentMapper.getCommentById(commentId);
    }

    @Override
    @Transactional
    public Comment addComment(Long postId, Long userId, String content) {
        // 创建评论对象
        Comment comment = new Comment(postId, userId, content);
        
        // 插入评论
        commentMapper.insertComment(comment);
        
        // 更新帖子评论数量
        updatePostCommentCount(postId);
        
        // 返回完整的评论信息（包含用户信息）
        return commentMapper.getCommentById(comment.getCommentId());
    }

    @Override
    @Transactional
    public Comment updateComment(Long commentId, Long userId, String content) {
        // 创建更新对象
        Comment comment = new Comment();
        comment.setCommentId(commentId);
        comment.setUserId(userId);
        comment.setContent(content);
        
        // 更新评论
        int rows = commentMapper.updateComment(comment);
        if (rows == 0) {
            throw new RuntimeException("评论更新失败，可能不存在或无权限");
        }
        
        // 返回更新后的评论信息
        return commentMapper.getCommentById(commentId);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 获取评论信息，用于获取帖子ID
        Comment comment = commentMapper.getCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 删除评论（软删除）
        int rows = commentMapper.deleteComment(commentId, userId);
        if (rows == 0) {
            throw new RuntimeException("评论删除失败，可能不存在或无权限");
        }
        
        // 更新帖子评论数量
        updatePostCommentCount(comment.getPostId());
    }

    @Override
    public int getCommentCount(Long postId) {
        return commentMapper.countCommentsByPostId(postId);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentMapper.getCommentsByUserId(userId);
    }
    
    /**
     * 更新帖子的评论数量
     * @param postId 帖子ID
     */
    private void updatePostCommentCount(Long postId) {
        try {
            int count = commentMapper.countCommentsByPostId(postId);
            postService.updateCommentsCount(postId, count);
        } catch (Exception e) {
            // 这里只记录日志，不影响评论操作的主流程
            System.err.println("更新帖子评论数失败: " + e.getMessage());
        }
    }
} 