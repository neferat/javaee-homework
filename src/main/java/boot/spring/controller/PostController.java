package boot.spring.controller;

import boot.spring.po.Post;
import boot.spring.po.User;
import boot.spring.service.PostService;
import boot.spring.service.UserService;
import boot.spring.tools.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public ResponseResult<List<Post>> getAllPosts(HttpSession session) {
        try {
            List<Post> posts = postService.getAllPosts();
            
            // 为每个帖子添加用户信息和点赞状态
            tryAddUserInfoToPosts(posts);
            addLikeStatusToPosts(posts, session);
            
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
    public ResponseResult<List<Post>> getPostsByCategory(@PathVariable String category, HttpSession session) {
        try {
            List<Post> posts = postService.getPostsByCategory(category);
            
            // 为每个帖子添加用户信息和点赞状态
            tryAddUserInfoToPosts(posts);
            addLikeStatusToPosts(posts, session);
            
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
     * 获取上传目录路径
     */
    private String getUploadDirectory() {
        try {
            // 方法1: 直接使用指定的绝对路径
            String specifiedPath = "D:\\dowmload\\ssmnew\\src\\main\\resources\\static\\images\\posts";
            File specifiedDir = new File(specifiedPath);
            
            LOG.info("尝试使用指定路径: {}", specifiedPath);
            
            // 检查路径是否存在，如果不存在则创建
            if (!specifiedDir.exists()) {
                boolean created = specifiedDir.mkdirs();
                LOG.info("创建指定目录: {} - 结果: {}", specifiedPath, created);
                if (created) {
                    return specifiedPath;
                }
            } else if (specifiedDir.isDirectory() && specifiedDir.canWrite()) {
                LOG.info("使用现有的指定路径: {}", specifiedPath);
                return specifiedPath;
            }
            
        } catch (Exception e) {
            LOG.error("无法使用指定路径: {}", e.getMessage());
        }
        
        try {
            // 方法2: 尝试使用ClassPath资源路径
            ClassPathResource resource = new ClassPathResource("static/images/posts/");
            if (resource.exists()) {
                String path = resource.getFile().getAbsolutePath();
                LOG.info("使用ClassPath路径: {}", path);
                return path;
            }
        } catch (Exception e) {
            LOG.warn("无法使用ClassPath路径: {}", e.getMessage());
        }
        
        try {
            // 方法3: 使用项目根目录的相对路径
            String projectDir = System.getProperty("user.dir");
            String uploadDir = projectDir + File.separator + "src" + File.separator + "main" + 
                             File.separator + "resources" + File.separator + "static" + 
                             File.separator + "images" + File.separator + "posts";
            
            LOG.info("使用项目目录路径: {}", uploadDir);
            
            // 尝试创建目录
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                LOG.info("创建项目目录: {} - 结果: {}", uploadDir, created);
            }
            
            return uploadDir;
        } catch (Exception e) {
            LOG.error("无法确定上传目录: {}", e.getMessage());
            // 方法4: 使用临时目录作为后备方案
            String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "posts_images";
            LOG.warn("使用临时目录作为后备: {}", tempDir);
            
            // 确保临时目录存在
            File tempDirFile = new File(tempDir);
            if (!tempDirFile.exists()) {
                tempDirFile.mkdirs();
            }
            
            return tempDir;
        }
    }

    /**
     * 上传帖子图片
     */
    @PostMapping("/upload-image")
    public ResponseResult<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            LOG.info("=== 开始图片上传 ===");
            LOG.info("文件名: {}, 大小: {} bytes ({} MB), 类型: {}", 
                    file.getOriginalFilename(), file.getSize(), 
                    String.format("%.2f", (double)file.getSize() / 1024 / 1024), 
                    file.getContentType());
            
            if (file.isEmpty()) {
                return ResponseResult.error("请选择要上传的图片");
            }
            
            // 检查文件类型 - 明确支持JPG和PNG格式
            String contentType = file.getContentType();
            String[] allowedMimeTypes = {"image/jpeg", "image/jpg", "image/png"};
            boolean validMimeType = false;
            
            if (contentType != null) {
                for (String allowedType : allowedMimeTypes) {
                    if (allowedType.equals(contentType)) {
                        validMimeType = true;
                        break;
                    }
                }
            }
            
            if (!validMimeType) {
                return ResponseResult.error("只支持JPG和PNG格式的图片文件，当前文件类型：" + contentType);
            }
            
            // 获取文件原始名称和扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseResult.error("文件名无效");
            }
            
            String fileExtension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                fileExtension = originalFilename.substring(lastDotIndex).toLowerCase();
            }
            
            // 验证文件扩展名
            String[] allowedExtensions = {".jpg", ".jpeg", ".png"};
            boolean validExtension = false;
            for (String allowedExt : allowedExtensions) {
                if (allowedExt.equals(fileExtension)) {
                    validExtension = true;
                    break;
                }
            }
            
            if (!validExtension) {
                return ResponseResult.error("文件扩展名必须是 .jpg、.jpeg 或 .png，当前扩展名：" + fileExtension);
            }
            
            // 验证文件大小（限制为5MB）
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return ResponseResult.error("图片文件大小不能超过5MB，当前大小：" + (file.getSize() / 1024 / 1024) + "MB");
            }
            
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + fileExtension;
            LOG.info("生成的文件名: {}", fileName);
            
            // 获取上传目录路径
            String uploadDir = getUploadDirectory();
            LOG.info("=== 上传目录信息 ===");
            LOG.info("上传目录: {}", uploadDir);
            
            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            LOG.info("上传路径对象: {}", uploadPath.toAbsolutePath());
            
            if (!Files.exists(uploadPath)) {
                LOG.info("目录不存在，正在创建...");
                try {
                    Files.createDirectories(uploadPath);
                    LOG.info("成功创建目录: {}", uploadPath.toAbsolutePath());
                } catch (Exception e) {
                    LOG.error("创建目录失败: {}", e.getMessage(), e);
                    return ResponseResult.error("创建上传目录失败：" + e.getMessage());
                }
            }
            
            // 检查目录权限
            if (!Files.isWritable(uploadPath)) {
                LOG.error("目录不可写: {}", uploadPath.toAbsolutePath());
                return ResponseResult.error("上传目录没有写入权限");
            }
            
            // 保存文件
            Path targetPath = uploadPath.resolve(fileName);
            LOG.info("=== 开始保存文件 ===");
            LOG.info("目标路径: {}", targetPath.toAbsolutePath());
            
            try {
                file.transferTo(targetPath.toFile());
                LOG.info("文件传输完成");
            } catch (Exception e) {
                LOG.error("文件传输失败: {}", e.getMessage(), e);
                return ResponseResult.error("文件保存失败：" + e.getMessage());
            }
            
            // 验证文件是否成功保存
            if (Files.exists(targetPath)) {
                long fileSize = Files.size(targetPath);
                LOG.info("=== 文件保存成功 ===");
                LOG.info("文件保存到: {}", targetPath.toAbsolutePath());
                LOG.info("文件大小: {} bytes", fileSize);
            } else {
                LOG.error("文件保存验证失败: {}", targetPath.toAbsolutePath());
                return ResponseResult.error("文件保存验证失败");
            }
            
            // 返回相对路径用于数据库存储和前端访问
            String imageUrl = "images/posts/" + fileName;
            
            Map<String, String> result = new HashMap<>();
            result.put("imageUrl", imageUrl);
            result.put("fileName", fileName);
            result.put("fullPath", targetPath.toAbsolutePath().toString());
            result.put("uploadDir", uploadDir);
            
            LOG.info("=== 上传完成 ===");
            LOG.info("图片URL: {}", imageUrl);
            LOG.info("完整路径: {}", targetPath.toAbsolutePath());
            
            return ResponseResult.success(result);
            
        } catch (IOException e) {
            LOG.error("IO异常: {}", e.getMessage(), e);
            return ResponseResult.error("文件IO异常：" + e.getMessage());
        } catch (Exception e) {
            LOG.error("上传异常: {}", e.getMessage(), e);
            return ResponseResult.error("文件上传异常：" + e.getMessage());
        }
    }

    /**
     * 测试上传目录配置（调试用）
     */
    @GetMapping("/test-upload-dir")
    public ResponseResult<Map<String, Object>> testUploadDirectory() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LOG.info("=== 开始测试上传目录 ===");
            
            String uploadDir = getUploadDirectory();
            Path uploadPath = Paths.get(uploadDir);
            
            result.put("uploadDirectory", uploadDir);
            result.put("absolutePath", uploadPath.toAbsolutePath().toString());
            result.put("exists", Files.exists(uploadPath));
            result.put("isDirectory", Files.isDirectory(uploadPath));
            result.put("canWrite", Files.isWritable(uploadPath));
            result.put("canRead", Files.isReadable(uploadPath));
            result.put("projectDir", System.getProperty("user.dir"));
            result.put("tempDir", System.getProperty("java.io.tmpdir"));
            result.put("userHome", System.getProperty("user.home"));
            
            // 测试指定路径
            String specifiedPath = "D:\\dowmload\\ssmnew\\src\\main\\resources\\static\\images\\posts";
            File specifiedDir = new File(specifiedPath);
            Map<String, Object> specifiedInfo = new HashMap<>();
            specifiedInfo.put("path", specifiedPath);
            specifiedInfo.put("exists", specifiedDir.exists());
            specifiedInfo.put("isDirectory", specifiedDir.isDirectory());
            specifiedInfo.put("canWrite", specifiedDir.canWrite());
            specifiedInfo.put("canRead", specifiedDir.canRead());
            specifiedInfo.put("parentExists", specifiedDir.getParentFile().exists());
            result.put("specifiedPathInfo", specifiedInfo);
            
            // 尝试创建目录
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                    result.put("directoryCreated", true);
                    LOG.info("成功创建目录: {}", uploadPath.toAbsolutePath());
                } catch (Exception e) {
                    result.put("directoryCreated", false);
                    result.put("createError", e.getMessage());
                    LOG.error("创建目录失败: {}", e.getMessage());
                }
            } else {
                result.put("directoryAlreadyExists", true);
            }
            
            // 列出目录内容（如果存在）
            if (Files.exists(uploadPath) && Files.isDirectory(uploadPath)) {
                try {
                    long fileCount = Files.list(uploadPath).count();
                    result.put("fileCount", fileCount);
                    
                    // 列出前10个文件
                    List<String> fileList = Files.list(uploadPath)
                        .limit(10)
                        .map(path -> path.getFileName().toString())
                        .collect(java.util.stream.Collectors.toList());
                    result.put("files", fileList);
                } catch (Exception e) {
                    result.put("listError", e.getMessage());
                }
            }
            
            // 测试文件写入权限
            try {
                String testFileName = "test_" + System.currentTimeMillis() + ".txt";
                Path testFile = uploadPath.resolve(testFileName);
                Files.write(testFile, "test content".getBytes());
                
                if (Files.exists(testFile)) {
                    result.put("writeTestSuccess", true);
                    Files.delete(testFile); // 删除测试文件
                    result.put("testFileDeleted", true);
                } else {
                    result.put("writeTestSuccess", false);
                }
            } catch (Exception e) {
                result.put("writeTestSuccess", false);
                result.put("writeTestError", e.getMessage());
            }
            
            LOG.info("上传目录测试结果: {}", result);
            return ResponseResult.success(result);
            
        } catch (Exception e) {
            LOG.error("测试上传目录失败", e);
            result.put("error", e.getMessage());
            result.put("stackTrace", e.getClass().getSimpleName() + ": " + e.getMessage());
            return ResponseResult.error("测试失败：" + e.getMessage());
        }
    }

    /**
     * 创建帖子
     */
    @PostMapping
    public ResponseResult<Post> createPost(@RequestBody Post post, HttpSession session) {
        try {
            LOG.info("收到创建帖子请求: {}", post.getTitle());
            
            // 从session获取当前用户ID
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                // 如果session中没有userId，尝试通过用户名获取
                String username = (String) session.getAttribute("currentUser");
                if (username != null) {
                    // 可以通过UserService根据用户名获取用户ID
                    if (userService != null) {
                        try {
                            User user = userService.getUserByUsername(username);
                            if (user != null) {
                                userId = user.getUserId();
                            }
                        } catch (Exception e) {
                            LOG.warn("无法通过用户名获取用户ID: {}", username);
                        }
                    }
                }
                
                // 如果仍然没有用户ID，使用默认值
                if (userId == null) {
                    userId = 1L; // 默认用户ID
                }
            }
            
            post.setUserId(userId);
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
            
            // 处理图片URL
            if (post.getImageUrl() == null || post.getImageUrl().isEmpty()) {
                // 如果没有上传图片，使用默认图片
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
     * 处理用户点赞/取消点赞操作
     */
    @PostMapping("/{postId}/like")
    public ResponseResult<Map<String, Object>> toggleLike(@PathVariable Long postId, 
                                                         @RequestBody Map<String, String> request,
                                                         HttpSession session) {
        try {
            String action = request.get("action");
            LOG.info("用户操作: {} 帖子ID: {}", action, postId);
            
            // 获取当前用户ID (简化版本，实际应该从session或JWT中获取)
            Long userId = 1L; // 默认用户ID，实际项目中应该从认证系统获取
            
            // 获取当前帖子信息
            Post post = postService.getPostById(postId);
            if (post == null) {
                return ResponseResult.error("帖子不存在");
            }
            
            // 模拟点赞状态检查 (实际项目中应该有专门的用户点赞记录表)
            // 这里简化处理，假设用户只能对每个帖子点一次赞
            boolean isCurrentlyLiked = false; // 可以从session或数据库获取
            String sessionKey = "liked_post_" + postId;
            Boolean sessionLiked = (Boolean) session.getAttribute(sessionKey);
            if (sessionLiked != null) {
                isCurrentlyLiked = sessionLiked;
            }
            
            boolean newLikedState;
            int newLikes = post.getLikes() != null ? post.getLikes() : 0;
            
            if ("like".equals(action)) {
                if (isCurrentlyLiked) {
                    return ResponseResult.error("您已经点过赞了");
                }
                newLikedState = true;
                newLikes++;
                session.setAttribute(sessionKey, true);
            } else if ("unlike".equals(action)) {
                if (!isCurrentlyLiked) {
                    return ResponseResult.error("您还没有点赞");
                }
                newLikedState = false;
                newLikes--;
                session.setAttribute(sessionKey, false);
            } else {
                return ResponseResult.error("无效的操作");
            }
            
            // 更新数据库中的点赞数
            postService.updateLikes(postId, newLikes);
            
            // 返回最新状态
            Map<String, Object> result = new HashMap<>();
            result.put("likes", newLikes);
            result.put("isLiked", newLikedState);
            
            LOG.info("点赞操作完成 - 帖子ID: {}, 新状态: {}, 新点赞数: {}", postId, newLikedState, newLikes);
            return ResponseResult.success(result);
            
        } catch (Exception e) {
            LOG.error("点赞操作失败", e);
            return ResponseResult.error("操作失败：" + e.getMessage());
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
     * 搜索帖子（模糊查询）
     */
    @GetMapping("/search")
    public ResponseResult<List<Post>> searchPosts(@RequestParam("q") String query) {
        try {
            LOG.info("搜索帖子，关键词: {}", query);
            
            if (query == null || query.trim().isEmpty()) {
                return ResponseResult.error("搜索关键词不能为空");
            }
            
            query = query.trim();
            if (query.length() > 100) {
                return ResponseResult.error("搜索关键词过长");
            }
            
            List<Post> posts = postService.searchPosts(query);
            
            // 为每个帖子添加用户信息
            tryAddUserInfoToPosts(posts);
            
            LOG.info("搜索到{}条帖子", posts.size());
            return ResponseResult.success(posts);
        } catch (Exception e) {
            LOG.error("搜索帖子失败", e);
            return ResponseResult.error("搜索失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取热门帖子（按点赞数排序）
     */
    @GetMapping("/hot")
    public ResponseResult<List<Post>> getHotPosts(@RequestParam(value = "limit", defaultValue = "10") Integer limit, 
                                                  HttpSession session) {
        try {
            LOG.info("获取热门帖子，限制数量: {}", limit);
            
            // 限制查询数量，防止过大的请求
            if (limit > 50) {
                limit = 50;
            }
            if (limit < 1) {
                limit = 10;
            }
            
            List<Post> hotPosts = postService.getHotPosts(limit);
            
            // 为每个帖子添加用户信息和点赞状态
            tryAddUserInfoToPosts(hotPosts);
            addLikeStatusToPosts(hotPosts, session);
            
            LOG.info("获取到{}条热门帖子", hotPosts.size());
            return ResponseResult.success(hotPosts);
        } catch (Exception e) {
            LOG.error("获取热门帖子失败", e);
            return ResponseResult.error("获取热门帖子失败：" + e.getMessage());
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
    
    /**
     * 为帖子列表添加用户点赞状态
     */
    private void addLikeStatusToPosts(List<Post> posts, HttpSession session) {
        if (posts == null || session == null) {
            return;
        }
        
        for (Post post : posts) {
            String sessionKey = "liked_post_" + post.getPostId();
            Boolean isLiked = (Boolean) session.getAttribute(sessionKey);
            post.setIsLiked(isLiked != null && isLiked);
        }
    }
} 