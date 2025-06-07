package boot.spring.controller;

import boot.spring.pagemodel.AjaxResult;
import boot.spring.po.User;
import boot.spring.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    
    /**
     * 获取所有用户
     */
    @GetMapping
    public AjaxResult getAllUsers() {
        List<User> users = userService.getAllUsers();
        LOG.info("获取所有用户列表");
        return AjaxResult.success(users);
    }
    
    /**
     * 分页获取用户
     */
    @GetMapping("/page")
    public AjaxResult getPageUsers(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        List<User> users = userService.getPageUsers(pageNum, pageSize);
        int total = userService.getUserNum();
        
        // 构建分页结果
        boot.spring.pagemodel.DataGrid<User> grid = new boot.spring.pagemodel.DataGrid<>();
        grid.setCurrent(pageNum);
        grid.setRowCount(pageSize);
        grid.setTotal(total);
        grid.setRows(users);
        
        LOG.info("分页获取用户列表，页码：{}，每页条数：{}", pageNum, pageSize);
        return AjaxResult.success(grid);
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{userId}")
    public AjaxResult getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            LOG.warn("未找到ID为{}的用户", userId);
            return AjaxResult.error("用户不存在");
        }
        LOG.info("获取ID为{}的用户", userId);
        return AjaxResult.success(user);
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public AjaxResult createUser(@RequestBody User user) {
        try {
            User newUser = userService.addUser(user);
            LOG.info("创建用户成功，ID：{}", newUser.getUserId());
            return AjaxResult.success(newUser);
        } catch (Exception e) {
            LOG.error("创建用户失败", e);
            return AjaxResult.error("创建用户失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{userId}")
    public AjaxResult updateUser(@PathVariable Long userId, @RequestBody User user) {
        // 确保ID一致
        user.setUserId(userId);
        try {
            User updatedUser = userService.updateUser(user);
            LOG.info("更新ID为{}的用户", userId);
            return AjaxResult.success(updatedUser);
        } catch (Exception e) {
            LOG.error("更新用户失败", e);
            return AjaxResult.error("更新用户失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public AjaxResult deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            LOG.info("删除ID为{}的用户", userId);
            return AjaxResult.success();
        } catch (Exception e) {
            LOG.error("删除用户失败", e);
            return AjaxResult.error("删除用户失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传用户头像
     */
    @PostMapping("/upload-avatar")
    public AjaxResult uploadAvatar(@RequestParam("file") MultipartFile file, HttpSession session) {
        try {
            LOG.info("=== 开始头像上传 ===");
            LOG.info("文件名: {}, 大小: {} bytes, 类型: {}", 
                    file.getOriginalFilename(), file.getSize(), file.getContentType());
            
            if (file.isEmpty()) {
                return AjaxResult.error("请选择要上传的头像");
            }
            
            // 获取当前用户ID
            Long userId = (Long) session.getAttribute("userId");
            String username = (String) session.getAttribute("currentUser");
            
            if (userId == null && username != null) {
                // 尝试通过用户名获取用户ID
                User user = userService.getUserByUsername(username);
                if (user != null) {
                    userId = user.getUserId();
                    session.setAttribute("userId", userId);
                }
            }
            
            if (userId == null) {
                return AjaxResult.error("用户未登录");
            }
            
            // 检查文件类型
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
                return AjaxResult.error("只支持JPG和PNG格式的图片文件");
            }
            
            // 验证文件大小（限制为5MB）
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return AjaxResult.error("图片文件大小不能超过5MB");
            }
            
            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.lastIndexOf('.') > 0) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            
            // 生成唯一文件名
            String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString() + fileExtension;
            LOG.info("生成的头像文件名: {}", fileName);
            
            // 获取头像上传目录
            String uploadDir = getAvatarUploadDirectory();
            LOG.info("头像上传目录: {}", uploadDir);
            
            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                LOG.info("创建头像上传目录: {}", uploadPath.toAbsolutePath());
            }
            
            // 保存文件
            Path targetPath = uploadPath.resolve(fileName);
            file.transferTo(targetPath.toFile());
            LOG.info("头像文件保存到: {}", targetPath.toAbsolutePath());
            
            // 返回相对路径用于数据库存储和前端访问
            String avatarUrl = "/images/avatars/" + fileName;
            
            // 更新用户头像URL到数据库
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setAvatarUrl(avatarUrl);
                userService.updateUser(user);
                
                // 更新session中的用户信息
                session.setAttribute("userInfo", user);
            }
            
            Map<String, String> result = new HashMap<>();
            result.put("avatarUrl", avatarUrl);
            result.put("fileName", fileName);
            result.put("fullPath", targetPath.toAbsolutePath().toString());
            
            LOG.info("=== 头像上传完成 ===");
            LOG.info("头像URL: {}", avatarUrl);
            
            return AjaxResult.success(result);
            
        } catch (IOException e) {
            LOG.error("头像上传IO异常", e);
            return AjaxResult.error("文件IO异常：" + e.getMessage());
        } catch (Exception e) {
            LOG.error("头像上传异常", e);
            return AjaxResult.error("头像上传异常：" + e.getMessage());
        }
    }
    
    /**
     * 更新用户资料（用户名和个性签名）
     */
    @PutMapping("/update-profile")
    public AjaxResult updateProfile(@RequestBody Map<String, String> profileData, HttpSession session) {
        try {
            LOG.info("开始更新用户资料");
            
            // 获取当前用户ID
            Long userId = (Long) session.getAttribute("userId");
            String currentUsername = (String) session.getAttribute("currentUser");
            
            if (userId == null && currentUsername != null) {
                // 尝试通过用户名获取用户ID
                User user = userService.getUserByUsername(currentUsername);
                if (user != null) {
                    userId = user.getUserId();
                    session.setAttribute("userId", userId);
                }
            }
            
            if (userId == null) {
                return AjaxResult.error("用户未登录");
            }
            
            // 获取要更新的数据
            String newUsername = profileData.get("username");
            String newBio = profileData.get("bio");
            
            if (newUsername == null || newUsername.trim().isEmpty()) {
                return AjaxResult.error("用户名不能为空");
            }
            
            newUsername = newUsername.trim();
            if (newBio != null) {
                newBio = newBio.trim();
            }
            
            // 验证用户名长度
            if (newUsername.length() < 2 || newUsername.length() > 20) {
                return AjaxResult.error("用户名长度应在2-20个字符之间");
            }
            
            // 验证个性签名长度
            if (newBio != null && newBio.length() > 100) {
                return AjaxResult.error("个性签名不能超过100个字符");
            }
            
            // 检查用户名是否已被其他用户使用
            if (!newUsername.equals(currentUsername)) {
                User existingUser = userService.getUserByUsername(newUsername);
                if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                    return AjaxResult.error("用户名已被使用，请选择其他用户名");
                }
            }
            
            // 获取当前用户信息并更新
            User user = userService.getUserById(userId);
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }
            
            user.setUsername(newUsername);
            if (newBio != null) {
                user.setBio(newBio);
            }
            
            // 更新到数据库
            User updatedUser = userService.updateUser(user);
            
            // 更新session信息
            session.setAttribute("currentUser", newUsername);
            session.setAttribute("userInfo", updatedUser);
            
            LOG.info("用户资料更新成功，用户ID: {}, 新用户名: {}", userId, newUsername);
            
            return AjaxResult.success(updatedUser);
            
        } catch (Exception e) {
            LOG.error("更新用户资料失败", e);
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取头像上传目录路径
     */
    private String getAvatarUploadDirectory() {
        try {
            // 使用指定的绝对路径
            String specifiedPath = "D:\\dowmload\\ssmnew\\src\\main\\resources\\static\\images\\avatars";
            File specifiedDir = new File(specifiedPath);
            
            LOG.info("尝试使用指定的头像路径: {}", specifiedPath);
            
            // 检查路径是否存在，如果不存在则创建
            if (!specifiedDir.exists()) {
                boolean created = specifiedDir.mkdirs();
                LOG.info("创建头像目录: {} - 结果: {}", specifiedPath, created);
                if (created) {
                    return specifiedPath;
                }
            } else if (specifiedDir.isDirectory() && specifiedDir.canWrite()) {
                LOG.info("使用现有的头像路径: {}", specifiedPath);
                return specifiedPath;
            }
            
        } catch (Exception e) {
            LOG.error("无法使用指定的头像路径: {}", e.getMessage());
        }
        
        try {
            // 备用方案：使用项目根目录的相对路径
            String projectDir = System.getProperty("user.dir");
            String uploadDir = projectDir + File.separator + "src" + File.separator + "main" + 
                             File.separator + "resources" + File.separator + "static" + 
                             File.separator + "images" + File.separator + "avatars";
            
            LOG.info("使用项目目录头像路径: {}", uploadDir);
            
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                LOG.info("创建项目头像目录: {} - 结果: {}", uploadDir, created);
            }
            
            return uploadDir;
        } catch (Exception e) {
            LOG.error("无法确定头像上传目录: {}", e.getMessage());
            // 最后的备用方案
            String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "avatars";
            LOG.warn("使用临时目录作为头像上传路径: {}", tempDir);
            
            File tempDirFile = new File(tempDir);
            if (!tempDirFile.exists()) {
                tempDirFile.mkdirs();
            }
            
            return tempDir;
        }
    }
}
