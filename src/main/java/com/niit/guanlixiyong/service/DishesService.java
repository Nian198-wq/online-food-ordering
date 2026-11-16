package com.niit.guanlixiyong.service;

import com.niit.guanlixiyong.dto.DishesDTO;
import com.niit.guanlixiyong.entity.Dishes;
import com.niit.guanlixiyong.repository.DishesRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品服务类
 */
@Service
public class DishesService {

    @Resource
    private DishesRepository dishesRepository;

    /**
     * 获取所有菜品
     */
    public List<DishesDTO> getAllDishes() {
        return dishesRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页获取可用菜品
     */
    public Page<DishesDTO> getAvailableDishes(Pageable pageable) {
        return dishesRepository.findByAvailableTrue(pageable)
                .map(this::convertToDTO);
    }

    /**
     * 按类别获取菜品（只返回上架的）
     */
    public List<DishesDTO> getDishesByCategory(String category) {
        List<Dishes> dishes = dishesRepository.findByCategoryAndAvailableTrue(category);
        System.out.println(">>> 查询分类: " + category + ", 找到菜品数量: " + dishes.size());
        return dishes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 按 ID 获取菜品
     */
    public DishesDTO getDishesById(Long id) {
        Dishes dishes = dishesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));
        return convertToDTO(dishes);
    }

    /**
     * 搜索菜品（只返回上架的）
     * 优化搜索逻辑：优先精确匹配，然后前缀匹配，最后模糊匹配
     * 只在菜名中搜索，不在描述中搜索，确保搜索结果精准
     */
    public List<DishesDTO> searchDishes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        String trimmedKeyword = keyword.trim();
        
        // 1. 精确匹配（完全等于菜品名称）- 最高优先级
        List<Dishes> exactMatches = dishesRepository.findByNameAndAvailableTrue(trimmedKeyword);
        if (!exactMatches.isEmpty()) {
            System.out.println(">>> 精确匹配成功: " + trimmedKeyword + ", 找到 " + exactMatches.size() + " 个结果");
            return exactMatches.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        
        // 2. 前缀匹配（以关键词开头）- 中等优先级
        List<Dishes> prefixMatches = dishesRepository.findByNameStartingWithAndAvailableTrue(trimmedKeyword);
        if (!prefixMatches.isEmpty()) {
            System.out.println(">>> 前缀匹配成功: " + trimmedKeyword + ", 找到 " + prefixMatches.size() + " 个结果");
            return prefixMatches.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        
        // 3. 模糊匹配（菜名包含关键词）- 最低优先级
        List<Dishes> fuzzyMatches = dishesRepository.findByNameContainingAndAvailableTrue(trimmedKeyword);
        if (!fuzzyMatches.isEmpty()) {
            System.out.println(">>> 模糊匹配成功: " + trimmedKeyword + ", 找到 " + fuzzyMatches.size() + " 个结果");
            return fuzzyMatches.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        
        // 4. 如果以上都没找到，返回空列表
        System.out.println(">>> 未找到匹配: " + trimmedKeyword);
        return List.of();
    }

    /**
     * 创建菜品
     */
    @Transactional
    public DishesDTO createDishes(DishesDTO dto) {
        Dishes dishes = new Dishes();
        BeanUtils.copyProperties(dto, dishes);
        if (dishes.getStock() == null) {
            dishes.setStock(100);
        }
        if (dishes.getStock() < 0) {
            throw new RuntimeException("库存不能小于 0");
        }
        dishes.setCreateTime(LocalDateTime.now());
        dishes.setUpdateTime(LocalDateTime.now());
        return convertToDTO(dishesRepository.save(dishes));
    }

    /**
     * 更新菜品
     */
    @Transactional
    public DishesDTO updateDishes(Long id, DishesDTO dto) {
        Dishes dishes = dishesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));
        Integer oldStock = dishes.getStock();
        BeanUtils.copyProperties(dto, dishes, "id", "createTime");
        if (dishes.getStock() == null) {
            dishes.setStock(oldStock);
        }
        if (dishes.getStock() < 0) {
            throw new RuntimeException("库存不能小于 0");
        }
        dishes.setUpdateTime(LocalDateTime.now());
        
        return convertToDTO(dishesRepository.save(dishes));
    }

    /**
     * 删除菜品
     */
    @Transactional
    public void deleteDishes(Long id) {
        if (!dishesRepository.existsById(id)) {
            throw new RuntimeException("菜品不存在");
        }
        dishesRepository.deleteById(id);
    }

    /**
     * 设置菜品可用状态
     */
    @Transactional
    public DishesDTO setAvailability(Long id, Boolean available) {
        Dishes dishes = dishesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));
        dishes.setAvailable(available);
        dishes.setUpdateTime(LocalDateTime.now());
        return convertToDTO(dishesRepository.save(dishes));
    }

    /**
     * 转换为 DTO
     */
    private DishesDTO convertToDTO(Dishes dishes) {
        DishesDTO dto = new DishesDTO();
        BeanUtils.copyProperties(dishes, dto);
        return dto;
    }
}
