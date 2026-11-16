package com.niit.guanlixiyong.service;

import com.niit.guanlixiyong.entity.Users;
import com.niit.guanlixiyong.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户服务类
 */
@Service
public class UsersService {

    @Resource
    private UsersRepository usersRepository;

    /**
     * 获取所有用户
     */
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    /**
     * 按 ID 获取用户
     */
    public Users getById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 按用户名获取用户
     */
    public Users getByUsername(String username) {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 创建用户
     */
    @Transactional
    public Users createUser(Users user) {
        // 检查用户名是否已存在
        if (usersRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (usersRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已被使用");
        }
        
        return usersRepository.save(user);
    }

    /**
     * 更新用户
     */
    @Transactional
    public Users updateUser(Long id, Users userDetails) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            user.setPhone(userDetails.getPhone());
        }
        if (userDetails.getAddress() != null) {
            user.setAddress(userDetails.getAddress());
        }
        if (userDetails.getPassword() != null) {
            user.setPassword(userDetails.getPassword());
        }
        
        return usersRepository.save(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!usersRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        usersRepository.deleteById(id);
    }

    /**
     * 按角色获取用户
     */
    public List<Users> getUsersByRole(String role) {
        return usersRepository.findByRole(role);
    }

    /**
     * 用户登录
     */
    public Users login(String username, String password) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        // 验证密码（实际项目中应该使用加密）
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        return user;
    }

    /**
     * 用户注册
     */
    @Transactional
    public Users register(com.niit.guanlixiyong.controller.UsersController.RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (usersRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (registerRequest.getEmail() != null && usersRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已被使用");
        }
        
        Users user = new Users();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setRole("CUSTOMER"); // 默认角色为普通用户
        
        return usersRepository.save(user);
    }
}
