package com.niit.guanlixiyong.repository;

import com.niit.guanlixiyong.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    
    /**
     * 按用户名查找
     */
    Optional<Users> findByUsername(String username);
    
    /**
     * 按邮箱查找
     */
    Optional<Users> findByEmail(String email);
    
    /**
     * 按角色查找用户
     */
    List<Users> findByRole(String role);
    
    /**
     * 统计各角色用户数量
     */
    @Query("SELECT u.role, COUNT(u) FROM Users u GROUP BY u.role")
    List<Object[]> countByRole();
}
