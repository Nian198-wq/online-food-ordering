package com.niit.guanlixiyong.repository;

import com.niit.guanlixiyong.entity.Dishes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜品数据访问接口
 */
@Repository
public interface DishesRepository extends JpaRepository<Dishes, Long> {
    
    /**
     * 按类别查找菜品
     */
    List<Dishes> findByCategory(String category);
    
    /**
     * 按类别查找菜品（只返回上架的）
     */
    List<Dishes> findByCategoryAndAvailableTrue(String category);
    
    /**
     * 查找可用的菜品
     */
    List<Dishes> findByAvailableTrue();
    
    /**
     * 分页查找可用菜品
     */
    Page<Dishes> findByAvailableTrue(Pageable pageable);
    
    /**
     * 按名称精确查询
     */
    List<Dishes> findByName(String name);
    
    /**
     * 按名称精确查询（只返回上架的）
     */
    List<Dishes> findByNameAndAvailableTrue(String name);
    
    /**
     * 按名称前缀查询
     */
    List<Dishes> findByNameStartingWith(String name);
    
    /**
     * 按名称前缀查询（只返回上架的）
     */
    List<Dishes> findByNameStartingWithAndAvailableTrue(String name);
    
    /**
     * 按名称模糊查询
     */
    List<Dishes> findByNameContaining(String name);
    
    /**
     * 按名称模糊查询（只返回上架的）
     */
    List<Dishes> findByNameContainingAndAvailableTrue(String name);
    
    /**
     * 统计各类别菜品数量
     */
    @Query("SELECT d.category, COUNT(d) FROM Dishes d GROUP BY d.category")
    List<Object[]> countByCategory();

    /**
     * 原子扣减库存。数据库条件更新可以避免并发下单超卖。
     */
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Dishes d SET d.stock = d.stock - :quantity, d.updateTime = CURRENT_TIMESTAMP " +
            "WHERE d.id = :id AND d.available = true AND d.stock >= :quantity")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 取消订单时回补库存。
     */
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Dishes d SET d.stock = d.stock + :quantity, d.updateTime = CURRENT_TIMESTAMP " +
            "WHERE d.id = :id")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
