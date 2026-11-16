package com.niit.guanlixiyong.controller;

import com.niit.guanlixiyong.dto.DishesDTO;
import com.niit.guanlixiyong.service.DishesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜品管理控制器
 */
@RestController
@RequestMapping("/api/dishes")
@CrossOrigin(origins = "*")
public class DishesController {

    @Resource
    private DishesService dishesService;

    /**
     * 获取所有菜品
     */
    @GetMapping
    public ResponseEntity<List<DishesDTO>> getAllDishes() {
        return ResponseEntity.ok(dishesService.getAllDishes());
    }

    /**
     * 分页获取可用菜品
     */
    @GetMapping("/available")
    public ResponseEntity<Page<DishesDTO>> getAvailableDishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(dishesService.getAvailableDishes(pageable));
    }

    /**
     * 按类别获取菜品
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<DishesDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(dishesService.getDishesByCategory(category));
    }

    /**
     * 按 ID 获取菜品
     */
    @GetMapping("/{id}")
    public ResponseEntity<DishesDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dishesService.getDishesById(id));
    }

    /**
     * 搜索菜品
     */
    @GetMapping("/search")
    public ResponseEntity<List<DishesDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(dishesService.searchDishes(keyword));
    }

    /**
     * 创建菜品
     */
    @PostMapping
    public ResponseEntity<DishesDTO> create(@RequestBody DishesDTO dto) {
        return ResponseEntity.ok(dishesService.createDishes(dto));
    }

    /**
     * 更新菜品
     */
    @PutMapping("/{id}")
    public ResponseEntity<DishesDTO> update(@PathVariable Long id, @RequestBody DishesDTO dto) {
        return ResponseEntity.ok(dishesService.updateDishes(id, dto));
    }

    /**
     * 删除菜品
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dishesService.deleteDishes(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 设置菜品可用状态
     */
    @PatchMapping("/{id}/availability")
    public ResponseEntity<DishesDTO> setAvailability(
            @PathVariable Long id, 
            @RequestParam Boolean available) {
        return ResponseEntity.ok(dishesService.setAvailability(id, available));
    }
}
