package boot.spring.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import boot.spring.mapper.UserMapper;
import boot.spring.po.User;
import boot.spring.service.UserService;

import com.github.pagehelper.PageHelper;

@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 5)
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    @Override
    public List<User> getPageUsers(int pageNum, int pageSize) {
        // 这里的实现可能需要根据实际分页库或查询方式进行调整
        // 如果使用MyBatis分页插件，可以直接使用该插件的方法
        // 这里简单实现，实际项目中可能需要更复杂的逻辑
        int offset = (pageNum - 1) * pageSize;
        // 假设这里有一个getPageUsers方法可以接收offset和pageSize参数
        // 实际可能需要添加这个方法到UserMapper接口和XML中
        return userMapper.getAllUsers().subList(offset, Math.min(offset + pageSize, userMapper.getAllUsers().size()));
    }

    @Override
    public int getUserNum() {
        return userMapper.getAllUsers().size();
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Override
    @Transactional
    public User addUser(User user) {
        // 设置创建时间
        if (user.getRegistrationDate() == null) {
            user.setRegistrationDate(new Date());
        }
        
        // 设置默认头像
        if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl("images/avatars/default.jpg");
        }
        
        // 插入用户并返回新用户
        userMapper.insertUser(user);
        return user; // insertUser会自动设置生成的主键
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        userMapper.updateUser(user);
        return userMapper.getUserById(user.getUserId());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userMapper.deleteUser(userId);
    }

    @Override
    @Transactional
    public void register(User user) {
        // 设置默认值
        if (user.getRegistrationDate() == null) {
            user.setRegistrationDate(new Date());
        }
        if (user.getPoints() == null) {
            user.setPoints(0);
        }
        if (user.getUserLevel() == null) {
            user.setUserLevel(1);
        }
        if (user.getStatus() == null) {
            user.setStatus("active");
        }
        // 设置默认头像
        if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl("images/avatars/default.jpg");
        }
        
        // 插入用户
        userMapper.insertUser(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public void resetPassword(String email) {
        User user = userMapper.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("该邮箱未注册");
        }
        // 这里可以实现发送重置密码邮件的逻辑
        LOG.info("发送重置密码邮件到: " + email);
    }
}