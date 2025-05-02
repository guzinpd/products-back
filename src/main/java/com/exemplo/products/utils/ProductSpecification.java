package com.exemplo.products.utils;

import org.springframework.data.jpa.domain.Specification;

import com.exemplo.products.model.Category;
import com.exemplo.products.model.Product;

import jakarta.persistence.criteria.Join;

public class ProductSpecification {
    public static Specification<Product> nameContains(String name) {
        return (root, query, cb) ->
            name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> availableEquals(Boolean available) {
        return (root, query, cb) ->
            available == null ? null : cb.equal(root.get("available"), available);
    }

    public static Specification<Product> categoryNameContains(String categoryName) {
        return (root, query, cb) -> {
            if (categoryName == null) return null;
            Join<Product, Category> category = root.join("categoryPath");
            return cb.like(cb.lower(category.get("name")), "%" + categoryName.toLowerCase() + "%");
        };
    }
}