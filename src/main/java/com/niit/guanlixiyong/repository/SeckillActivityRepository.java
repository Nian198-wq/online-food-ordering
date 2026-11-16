package com.niit.guanlixiyong.repository;

import com.niit.guanlixiyong.entity.SeckillActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动数据访问接口
 */
@Repository
public interface SeckillActivityRepository extends JpaRepository<SeckillActivity, Long> {
    
    /**
     * 查询进行中的秒杀活动
     */
    List<SeckillActivity> findByStatusAndIsActiveTrue(String status);
    
    /**
     * 分页查询所有秒杀活动
     */
    Page<SeckillActivity> findAll(Pageable pageable);
    
    /**
     * 查询即将开始的活动
     */
    List<SeckillActivity> findByStatusAndStartTimeAfterAndIsActiveTrue(String status, LocalDateTime time);
    
    /**
     * 查询已结束的活动
     */
    List<SeckillActivity> findByStatus(String status);
}
