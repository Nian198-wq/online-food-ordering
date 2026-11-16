package com.niit.guanlixiyong.controller;

import com.niit.guanlixiyong.entity.Users;
import com.niit.guanlixiyong.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UsersController {

    @Resource
    private UsersService usersService;

    /**
     * 获取所有用户
     */
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    /**
     * 按 ID 获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<Users> getById(@PathVariable Long id) {
        return ResponseEntity.ok(usersService.getById(id));
    }

    /**
     * 按用户名获取用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Users> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(usersService.getByUsername(username));
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        return ResponseEntity.ok(usersService.createUser(user));
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users user) {
        return ResponseEntity.ok(usersService.updateUser(id, user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 按角色获取用户
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Users>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(usersService.getUsersByRole(role));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Users> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(usersService.login(loginRequest.getUsername(), loginRequest.getPassword()));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(usersService.register(registerRequest));
    }

    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /**
     * 注册请求DTO
     */
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String phone;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}
