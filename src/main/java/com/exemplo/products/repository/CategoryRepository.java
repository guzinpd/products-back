package com.exemplo.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.products.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
