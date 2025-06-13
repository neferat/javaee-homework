package boot.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import boot.spring.po.User;
import boot.spring.service.UserService;
import boot.spring.tools.ResponseResult;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/test")
public class TestController {
    
    @Autowired(required = false)
    private UserService userService;
    
    /**
     * 测试页面 - 快速登录和数据初始化
     */
    @GetMapping("/init")
    @ResponseBody
    public ResponseResult<String> initTestData(HttpSession session) {
        try {
            // 创建或获取测试用户
            User testUser = null;
            if (userService != null) {
                testUser = userService.getUserByUsername("testuser");
                
                if (testUser == null) {
                    // 创建测试用户
                    testUser = new User();
                    testUser.setUsername("testuser");
                    testUser.setPassword("123456");
                    testUser.setEmail("test@example.com");
                    testUser.setBio("测试用户");
                    testUser.setAvatarUrl("https://via.placeholder.com/40");
                    testUser.setRegistrationDate(new Date());
                    testUser.setPoints(0);
                    testUser.setUserLevel(1);
                    testUser.setStatus("active");
                    
                    testUser = userService.addUser(testUser);
                }
            } else {
                // 如果UserService不可用，创建一个临时用户对象
                testUser = new User();
                testUser.setUserId(1L);
                testUser.setUsername("testuser");
                testUser.setEmail("test@example.com");
                testUser.setBio("测试用户");
                testUser.setAvatarUrl("https://via.placeholder.com/40");
            }
            
            // 设置session
            session.setAttribute("currentUser", testUser.getUsername());
            session.setAttribute("userId", testUser.getUserId());
            session.setAttribute("userInfo", testUser);
            
            return ResponseResult.success("测试用户登录成功！用户名: " + testUser.getUsername() + ", ID: " + testUser.getUserId());
            
        } catch (Exception e) {
            return ResponseResult.error("初始化测试数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查当前登录状态
     */
    @GetMapping("/status")
    @ResponseBody
    public ResponseResult<String> checkStatus(HttpSession session) {
        String username = (String) session.getAttribute("currentUser");
        Long userId = (Long) session.getAttribute("userId");
        
        if (username != null) {
            return ResponseResult.success("已登录 - 用户名: " + username + ", ID: " + userId);
        } else {
            return ResponseResult.error("未登录");
        }
    }
    
    /**
     * 清除登录状态
     */
    @GetMapping("/logout")
    @ResponseBody
    public ResponseResult<String> logout(HttpSession session) {
        session.removeAttribute("currentUser");
        session.removeAttribute("userId");
        session.removeAttribute("userInfo");
        
        return ResponseResult.success("已退出登录");
    }
} 