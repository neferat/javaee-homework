package boot.spring.service.impl;

import boot.spring.mapper.PostMapper;
import boot.spring.po.Post;
import boot.spring.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public List<Post> getAllPosts() {
        return postMapper.getAllPosts();
    }

    @Override
    public List<Post> getPostsByCategory(String category) {
        return postMapper.getPostsByCategory(category);
    }

    @Override
    public Post getPostById(Long postId) {
        return postMapper.getPostById(postId);
    }

    @Override
    @Transactional
    public Post addPost(Post post) {
        // 设置默认值
        if (post.getLikes() == null) {
            post.setLikes(0);
        }
        if (post.getCommentsCount() == null) {
            post.setCommentsCount(0);
        }
        if (post.getCreatedAt() == null) {
            post.setCreatedAt(new Date());
        }
        post.setUpdatedAt(new Date());
        
        // 插入帖子
        postMapper.insertPost(post);
        return post; // insertPost会自动设置生成的主键
    }

    @Override
    @Transactional
    public Post updatePost(Post post) {
        // 设置更新时间
        post.setUpdatedAt(new Date());
        
        // 更新帖子
        postMapper.updatePost(post);
        return postMapper.getPostById(post.getPostId());
    }

    @Override
    @Transactional
    public void updateLikes(Long postId, Integer likes) {
        postMapper.updateLikes(postId, likes);
    }

    @Override
    @Transactional
    public void updateCommentsCount(Long postId, Integer commentsCount) {
        postMapper.updateCommentsCount(postId, commentsCount);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        postMapper.deletePost(postId);
    }
    
    @Override
    public List<Post> searchPosts(String query) {
        return postMapper.searchPosts(query);
    }
    
    @Override
    public List<Post> getHotPosts(Integer limit) {
        return postMapper.getHotPosts(limit);
    }
    
    @Override
    public List<Post> getPagePosts(int pageNum, int pageSize) {
        // 简单分页实现，类似于UserServiceImpl中的实现
        List<Post> allPosts = postMapper.getAllPosts();
        int offset = (pageNum - 1) * pageSize;
        int endIndex = Math.min(offset + pageSize, allPosts.size());
        
        if (offset >= allPosts.size()) {
            return new java.util.ArrayList<>();
        }
        
        return allPosts.subList(offset, endIndex);
    }
    
    @Override
    public int getPostCount() {
        return postMapper.getAllPosts().size();
    }
} 