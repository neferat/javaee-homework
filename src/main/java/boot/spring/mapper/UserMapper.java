package boot.spring.mapper;

import boot.spring.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    
    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户对象
     */
    User getUserById(@Param("userId") Long userId);
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    User getUserByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象
     */
    User getUserByEmail(@Param("email") String email);
    
    /**
     * 插入新用户
     * @param user 用户对象
     * @return 影响的行数
     */
    int insertUser(User user);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响的行数
     */
    int updateUser(User user);
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteUser(@Param("userId") Long userId);
}