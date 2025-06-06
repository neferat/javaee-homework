package boot.spring.controller;

import boot.spring.po.Post;
import boot.spring.po.User;
import boot.spring.service.PostService;
import boot.spring.service.UserService;
import boot.spring.tools.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;
    
    @Autowired(required = false)
    private UserService userService;
    
    private static final Logger LOG = LoggerFactory.getLogger(PostController.class);

    /**
     * 获取所有帖子
     */
    @GetMapping
    public ResponseResult<List<Post>> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPosts();
            
            // 为每个帖子添加用户信息
            tryAddUserInfoToPosts(posts);
            
            LOG.info("获取所有帖子");
            return ResponseResult.success(posts);
        } catch (Exception e) {
            LOG.error("获取帖子列表失败", e);
            return ResponseResult.error("获取帖子列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据分类获取帖子
     */
    @GetMapping("/category/{category}")
    public ResponseResult<List<Post>> getPostsByCategory(@PathVariable String category) {
        try {
            List<Post> posts = postService.getPostsByCategory(category);
            
            // 为每个帖子添加用户信息
            tryAddUserInfoToPosts(posts);
            
            LOG.info("获取分类为{}的帖子", category);
            return ResponseResult.success(posts);
        } catch (Exception e) {
            LOG.error("获取分类帖子失败", e);
            return ResponseResult.error("获取分类帖子失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取帖子
     */
    @GetMapping("/{postId}")
    public ResponseResult<Post> getPostById(@PathVariable Long postId) {
        try {
            Post post = postService.getPostById(postId);
            if (post == null) {
                LOG.warn("未找到ID为{}的帖子", postId);
                return ResponseResult.error("帖子不存在");
            }
            
            // 添加用户信息
            tryAddUserInfoToPost(post);
            
            LOG.info("获取ID为{}的帖子", postId);
            return ResponseResult.success(post);
        } catch (Exception e) {
            LOG.error("获取帖子详情失败", e);
            return ResponseResult.error("获取帖子详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建帖子
     */
    @PostMapping
    public ResponseResult<Post> createPost(@RequestBody Post post) {
        try {
            LOG.info("收到创建帖子请求: {}", post.getTitle());
            
            // 设置默认值
            if (post.getUserId() == null) {
                post.setUserId(1L); // 默认用户ID
            }
            if (post.getLikes() == null) {
                post.setLikes(0);
            }
            if (post.getCommentsCount() == null) {
                post.setCommentsCount(0);
            }
            
            // 设置时间
            Date now = new Date();
            post.setCreatedAt(now);
            post.setUpdatedAt(now);
            
            // 处理图片文件名（简单处理，实际应保存文件）
            if (post.getImageUrl() == null || post.getImageUrl().isEmpty()) {
                // 如果没有提供图片URL，根据分类设置默认图片路径
                String category = post.getCategory() != null ? post.getCategory() : "default";
                post.setImageUrl("images/posts/" + category + "1.jpg");
            }
            
            Post newPost = postService.addPost(post);
            
            // 添加用户信息
            tryAddUserInfoToPost(newPost);
            
            LOG.info("创建帖子成功，ID: {}", newPost.getPostId());
            return ResponseResult.success(newPost);
        } catch (Exception e) {
            LOG.error("创建帖子失败", e);
            return ResponseResult.error("创建帖子失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新帖子点赞数
     */
    @PutMapping("/{postId}/likes")
    public ResponseResult<Void> updateLikes(@PathVariable Long postId, @RequestParam Integer likes) {
        try {
            postService.updateLikes(postId, likes);
            LOG.info("更新ID为{}的帖子点赞数为{}", postId, likes);
            return ResponseResult.success(null);
        } catch (Exception e) {
            LOG.error("更新帖子点赞数失败", e);
            return ResponseResult.error("更新帖子点赞数失败：" + e.getMessage());
        }
    }

    /**
     * 更新帖子评论数
     */
    @PutMapping("/{postId}/comments")
    public ResponseResult<Void> updateComments(@PathVariable Long postId, @RequestParam Integer comments) {
        try {
            postService.updateCommentsCount(postId, comments);
            LOG.info("更新ID为{}的帖子评论数为{}", postId, comments);
            return ResponseResult.success(null);
        } catch (Exception e) {
            LOG.error("更新帖子评论数失败", e);
            return ResponseResult.error("更新帖子评论数失败：" + e.getMessage());
        }
    }
    
    /**
     * 尝试为帖子列表添加用户信息
     */
    private void tryAddUserInfoToPosts(List<Post> posts) {
        if (userService == null || posts == null) {
            return;
        }
        
        for (Post post : posts) {
            tryAddUserInfoToPost(post);
        }
    }
    
    /**
     * 尝试为单个帖子添加用户信息
     */
    private void tryAddUserInfoToPost(Post post) {
        if (userService == null || post == null || post.getUserId() == null) {
            // 如果无法获取用户信息，设置默认用户对象
            setDefaultUser(post);
            return;
        }
        
        try {
            User user = userService.getUserById(post.getUserId());
            if (user != null) {
                post.setUser(user);
            } else {
                setDefaultUser(post);
            }
        } catch (Exception e) {
            LOG.warn("获取用户信息失败: userId={}, error={}", post.getUserId(), e.getMessage());
            setDefaultUser(post);
        }
    }
    
    /**
     * 设置默认用户信息
     */
    private void setDefaultUser(Post post) {
        User defaultUser = new User();
        defaultUser.setUserId(post.getUserId());
        defaultUser.setUsername("用户" + post.getUserId());
        defaultUser.setAvatarUrl("images/avatars/default.jpg");
        post.setUser(defaultUser);
    }
} 