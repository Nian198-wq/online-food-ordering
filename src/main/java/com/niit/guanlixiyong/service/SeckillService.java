package com.niit.guanlixiyong.service;

import com.niit.guanlixiyong.dto.SeckillActivityDTO;
import com.niit.guanlixiyong.dto.SeckillItemDTO;
import com.niit.guanlixiyong.entity.SeckillActivity;
import com.niit.guanlixiyong.entity.SeckillItem;
import com.niit.guanlixiyong.repository.SeckillActivityRepository;
import com.niit.guanlixiyong.repository.SeckillItemRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 秒杀服务类
 */
@Service
public class SeckillService {

    @Resource
    private SeckillActivityRepository seckillActivityRepository;

    @Resource
    private SeckillItemRepository seckillItemRepository;

    /**
     * 创建秒杀活动
     */
    @Transactional
    public SeckillActivityDTO createActivity(SeckillActivityDTO dto) {
        // 验证时间
        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("开始时间不能早于当前时间");
        }
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new RuntimeException("结束时间不能早于开始时间");
        }
        
        // 设置默认值
        if (dto.getDiscount() == null) {
            dto.setDiscount(BigDecimal.ONE); // 默认无折扣，具体折扣由商品秒杀价决定
        }
        
        // 验证折扣（如果有提供）
        if (dto.getDiscount().compareTo(BigDecimal.valueOf(0.1)) < 0 || 
            dto.getDiscount().compareTo(BigDecimal.ONE) > 0) {
            throw new RuntimeException("折扣必须在0.1-1.0之间");
        }

        SeckillActivity activity = new SeckillActivity();
        BeanUtils.copyProperties(dto, activity);
        activity.setStatus("UPCOMING");
        activity.setSoldStock(0);
        activity.setTotalStock(0); // 初始总库存为0，添加商品时累加
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());

        SeckillActivity saved = seckillActivityRepository.save(activity);
        return convertToActivityDTO(saved);
    }

    /**
     * 更新秒杀活动
     */
    @Transactional
    public SeckillActivityDTO updateActivity(Long id, SeckillActivityDTO dto) {
        SeckillActivity activity = seckillActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        // 进行中的活动不允许修改
        if ("ONGOING".equals(activity.getStatus())) {
            throw new RuntimeException("进行中的活动不允许修改");
        }

        BeanUtils.copyProperties(dto, activity, "id", "createTime");
        activity.setUpdateTime(LocalDateTime.now());

        // 重新计算状态
        updateActivityStatus(activity);

        SeckillActivity saved = seckillActivityRepository.save(activity);
        return convertToActivityDTO(saved);
    }

    /**
     * 删除秒杀活动
     */
    @Transactional
    public void deleteActivity(Long id) {
        SeckillActivity activity = seckillActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));
        
        if ("ONGOING".equals(activity.getStatus())) {
            throw new RuntimeException("进行中的活动不允许删除");
        }

        activity.setIsActive(false);
        activity.setUpdateTime(LocalDateTime.now());
        seckillActivityRepository.save(activity);
    }

    /**
     * 添加秒杀商品
     */
    @Transactional
    public SeckillItemDTO addSeckillItem(Long activityId, SeckillItemDTO dto) {
        SeckillActivity activity = seckillActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        if ("ONGOING".equals(activity.getStatus())) {
            throw new RuntimeException("进行中的活动不允许添加商品");
        }

        // 检查该菜品是否已添加到活动中
        SeckillItem existingItem = seckillItemRepository.findByActivityIdAndDishes_Id(activityId, dto.getDishesId());
        if (existingItem != null) {
            throw new RuntimeException("该菜品已添加到秒杀活动中");
        }

        SeckillItem item = new SeckillItem();
        item.setActivity(activity);
        item.setDishesId(dto.getDishesId());
        item.setSeckillPrice(dto.getSeckillPrice());
        item.setLimitPerUser(dto.getLimitPerUser() != null ? dto.getLimitPerUser() : 1);
        item.setStock(dto.getStock());
        item.setSoldStock(0);
        item.setIsActive(true);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        // 更新活动总库存
        activity.setTotalStock(activity.getTotalStock() + dto.getStock());
        seckillActivityRepository.save(activity);

        SeckillItem saved = seckillItemRepository.save(item);
        return convertToItemDTO(saved);
    }

    /**
     * 获取进行中的秒杀活动列表
     */
    public List<SeckillActivityDTO> getOngoingActivities() {
        return seckillActivityRepository.findByStatusAndIsActiveTrue("ONGOING")
                .stream()
                .map(this::convertToActivityDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有秒杀活动(分页)
     */
    public Page<SeckillActivityDTO> getAllActivities(Pageable pageable) {
        return seckillActivityRepository.findAll(pageable)
                .map(this::convertToActivityDTO);
    }

    /**
     * 获取秒杀活动详情
     */
    public SeckillActivityDTO getActivityDetail(Long id) {
        SeckillActivity activity = seckillActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));
        return convertToActivityDTO(activity);
    }

    /**
     * 获取秒杀活动的所有商品
     */
    public List<SeckillItemDTO> getActivityItems(Long activityId) {
        return seckillItemRepository.findByActivityIdAndIsActiveTrue(activityId)
                .stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有进行中的秒杀商品(用户端使用)
     */
    public List<SeckillItemDTO> getOngoingSeckillItems() {
        return seckillItemRepository.findOngoingSeckillItems()
                .stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * 启动秒杀活动
     */
    @Transactional
    public void startActivity(Long id) {
        SeckillActivity activity = seckillActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        if (!"UPCOMING".equals(activity.getStatus())) {
            throw new RuntimeException("只有即将开始的活动可以启动");
        }

        activity.setStatus("ONGOING");
        activity.setUpdateTime(LocalDateTime.now());
        seckillActivityRepository.save(activity);
    }

    /**
     * 结束秒杀活动
     */
    @Transactional
    public void endActivity(Long id) {
        SeckillActivity activity = seckillActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        activity.setStatus("ENDED");
        activity.setUpdateTime(LocalDateTime.now());
        seckillActivityRepository.save(activity);
    }

    /**
     * 检查并更新活动状态
     */
    private void updateActivityStatus(SeckillActivity activity) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime())) {
            activity.setStatus("UPCOMING");
        } else if (now.isAfter(activity.getEndTime())) {
            activity.setStatus("ENDED");
        } else {
            activity.setStatus("ONGOING");
        }
    }

    /**
     * 转换为活动DTO
     */
    private SeckillActivityDTO convertToActivityDTO(SeckillActivity activity) {
        SeckillActivityDTO dto = new SeckillActivityDTO();
        BeanUtils.copyProperties(activity, dto);
        return dto;
    }

    /**
     * 转换为商品DTO
     */
    private SeckillItemDTO convertToItemDTO(SeckillItem item) {
        SeckillItemDTO dto = new SeckillItemDTO();
        dto.setId(item.getId());
        dto.setActivityId(item.getActivity().getId());
        dto.setDishesId(item.getDishes().getId());
        dto.setDishesName(item.getDishes().getName());
        dto.setDishesImageUrl(item.getDishes().getImageUrl());
        dto.setOriginalPrice(item.getDishes().getPrice());
        dto.setSeckillPrice(item.getSeckillPrice());
        dto.setLimitPerUser(item.getLimitPerUser());
        dto.setStock(item.getStock());
        dto.setSoldStock(item.getSoldStock());
        dto.setIsActive(item.getIsActive());
        dto.setCreateTime(item.getCreateTime());
        dto.setUpdateTime(item.getUpdateTime());
        return dto;
    }
}
