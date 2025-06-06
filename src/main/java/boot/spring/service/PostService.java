package boot.spring.service;

import boot.spring.po.Post;

import java.util.List;

public interface PostService {
    /**
     * 获取所有帖子
     * @return 帖子列表
     */
    List<Post> getAllPosts();
    
    /**
     * 根据分类获取帖子
     * @param category 分类名称
     * @return 帖子列表
     */
    List<Post> getPostsByCategory(String category);
    
    /**
     * 根据ID获取帖子
     * @param postId 帖子ID
     * @return 帖子对象
     */
    Post getPostById(Long postId);
    
    /**
     * 添加帖子
     * @param post 帖子对象
     * @return 新增的帖子对象（包含帖子ID）
     */
    Post addPost(Post post);
    
    /**
     * 更新帖子信息
     * @param post 帖子对象
     * @return 更新后的帖子对象
     */
    Post updatePost(Post post);
    
    /**
     * 更新帖子点赞数
     * @param postId 帖子ID
     * @param likes 点赞数
     */
    void updateLikes(Long postId, Integer likes);
    
    /**
     * 更新帖子评论数
     * @param postId 帖子ID
     * @param commentsCount 评论数
     */
    void updateCommentsCount(Long postId, Integer commentsCount);
    
    /**
     * 删除帖子
     * @param postId 帖子ID
     */
    void deletePost(Long postId);
} 