package boot.spring.service;

import java.util.List;
import java.util.Map;

import boot.spring.po.User;

public interface UserService {
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 分页获取用户
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    List<User> getPageUsers(int pageNum, int pageSize);

    /**
     * 获取用户总数
     * @return 用户总数
     */
    int getUserNum();

    /**
     * 根据ID获取用户
     * @param userId 用户ID
     * @return 用户对象
     */
    User getUserById(Long userId);

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户对象
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱获取用户
     * @param email 邮箱
     * @return 用户对象
     */
    User getUserByEmail(String email);

    /**
     * 添加用户
     * @param user 用户对象
     * @return 新增的用户对象（包含用户ID）
     */
    User addUser(User user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User updateUser(User user);

    /**
     * 删除用户
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    void register(User user);

    User login(String username, String password);

    void resetPassword(String email);
}