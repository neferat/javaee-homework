package boot.spring.mapper;

import boot.spring.po.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    
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
    List<Post> getPostsByCategory(@Param("category") String category);
    
    /**
     * 根据ID获取帖子
     * @param postId 帖子ID
     * @return 帖子对象
     */
    Post getPostById(@Param("postId") Long postId);
    
    /**
     * 插入新帖子
     * @param post 帖子对象
     * @return 影响的行数
     */
    int insertPost(Post post);
    
    /**
     * 更新帖子信息
     * @param post 帖子对象
     * @return 影响的行数
     */
    int updatePost(Post post);
    
    /**
     * 更新帖子点赞数
     * @param postId 帖子ID
     * @param likes 点赞数
     * @return 影响的行数
     */
    int updateLikes(@Param("postId") Long postId, @Param("likes") Integer likes);
    
    /**
     * 更新帖子评论数
     * @param postId 帖子ID
     * @param commentsCount 评论数
     * @return 影响的行数
     */
    int updateCommentsCount(@Param("postId") Long postId, @Param("commentsCount") Integer commentsCount);
    
    /**
     * 删除帖子
     * @param postId 帖子ID
     * @return 影响的行数
     */
    int deletePost(@Param("postId") Long postId);
    
    /**
     * 搜索帖子（模糊查询）
     * @param query 搜索关键词
     * @return 匹配的帖子列表
     */
    List<Post> searchPosts(@Param("query") String query);
} 