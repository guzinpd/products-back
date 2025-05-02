package com.exemplo.products.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private String description;
    private double price;
    private boolean available;
    private Long categoryId;
}
