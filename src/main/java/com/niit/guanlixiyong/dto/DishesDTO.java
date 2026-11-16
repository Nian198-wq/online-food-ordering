package com.niit.guanlixiyong.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 菜品 DTO
 */
@Data
public class DishesDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Boolean available;
    private Integer stock;
}
