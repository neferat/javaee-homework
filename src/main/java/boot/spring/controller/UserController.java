package boot.spring.controller;

import boot.spring.pagemodel.AjaxResult;
import boot.spring.po.User;
import boot.spring.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
