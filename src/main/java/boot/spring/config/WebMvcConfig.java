package boot.spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private static final Logger LOG = LoggerFactory.getLogger(WebMvcConfig.class);
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取上传目录路径，与PostController中的getUploadDirectory()逻辑保持一致
        String uploadDir = getUploadDirectory();
        
        LOG.info("=== 配置静态资源映射 ===");
        LOG.info("URL路径: /images/posts/**");
        LOG.info("文件系统路径: {}", uploadDir);
        
        // 添加静态资源映射：将 /images/posts/ 路径映射到实际的文件系统路径
        registry.addResourceHandler("/images/posts/**")
                .addResourceLocations("file:" + uploadDir + File.separator);
        
        // 确保默认的静态资源处理器仍然工作
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
        
        LOG.info("静态资源映射配置完成");
    }
    
    /**
     * 获取上传目录路径（与PostController保持一致）
     */
    private String getUploadDirectory() {
        try {
            // 方法1: 直接使用指定的绝对路径
            String specifiedPath = "D:\\dowmload\\ssmnew\\src\\main\\resources\\static\\images\\posts";
            File specifiedDir = new File(specifiedPath);
            
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
            // 方法2: 使用项目根目录的相对路径
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
            // 方法3: 使用临时目录作为后备方案
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
} 