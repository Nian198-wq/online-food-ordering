package com.niit.guanlixiyong.controller;

import com.niit.guanlixiyong.dto.SeckillActivityDTO;
import com.niit.guanlixiyong.dto.SeckillItemDTO;
import com.niit.guanlixiyong.service.SeckillService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 秒杀活动控制器
 */
@RestController
@RequestMapping("/api/seckill")
public class SeckillController {

    @Resource
    private SeckillService seckillService;

    /**
     * 创建秒杀活动
     */
    @PostMapping("/activity")
    public ResponseEntity<SeckillActivityDTO> createActivity(@RequestBody SeckillActivityDTO dto) {
        return ResponseEntity.ok(seckillService.createActivity(dto));
    }

    /**
     * 更新秒杀活动
     */
    @PutMapping("/activity/{id}")
    public ResponseEntity<SeckillActivityDTO> updateActivity(
            @PathVariable Long id,
            @RequestBody SeckillActivityDTO dto) {
        return ResponseEntity.ok(seckillService.updateActivity(id, dto));
    }

    /**
     * 删除秒杀活动
     */
    @DeleteMapping("/activity/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        seckillService.deleteActivity(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 添加秒杀商品
     */
    @PostMapping("/activity/{activityId}/item")
    public ResponseEntity<SeckillItemDTO> addSeckillItem(
            @PathVariable Long activityId,
            @RequestBody SeckillItemDTO dto) {
        return ResponseEntity.ok(seckillService.addSeckillItem(activityId, dto));
    }

    /**
     * 获取进行中的秒杀活动列表
     */
    @GetMapping("/activity/ongoing")
    public ResponseEntity<List<SeckillActivityDTO>> getOngoingActivities() {
        return ResponseEntity.ok(seckillService.getOngoingActivities());
    }

    /**
     * 获取所有秒杀活动(分页)
     */
    @GetMapping("/activity")
    public ResponseEntity<Page<SeckillActivityDTO>> getAllActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(seckillService.getAllActivities(
                PageRequest.of(page, size, Sort.by("createTime").descending())));
    }

    /**
     * 获取秒杀活动详情
     */
    @GetMapping("/activity/{id}")
    public ResponseEntity<SeckillActivityDTO> getActivityDetail(@PathVariable Long id) {
        return ResponseEntity.ok(seckillService.getActivityDetail(id));
    }

    /**
     * 获取秒杀活动的所有商品
     */
    @GetMapping("/activity/{id}/items")
    public ResponseEntity<List<SeckillItemDTO>> getActivityItems(@PathVariable Long id) {
        return ResponseEntity.ok(seckillService.getActivityItems(id));
    }

    /**
     * 获取所有进行中的秒杀商品(用户端使用)
     */
    @GetMapping("/items/ongoing")
    public ResponseEntity<List<SeckillItemDTO>> getOngoingSeckillItems() {
        return ResponseEntity.ok(seckillService.getOngoingSeckillItems());
    }

    /**
     * 启动秒杀活动
     */
    @PostMapping("/activity/{id}/start")
    public ResponseEntity<Void> startActivity(@PathVariable Long id) {
        seckillService.startActivity(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 结束秒杀活动
     */
    @PostMapping("/activity/{id}/end")
    public ResponseEntity<Void> endActivity(@PathVariable Long id) {
        seckillService.endActivity(id);
        return ResponseEntity.ok().build();
    }
}
