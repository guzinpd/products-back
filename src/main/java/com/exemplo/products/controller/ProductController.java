package com.exemplo.products.controller;

import com.exemplo.products.dtos.ProductRequest;
import com.exemplo.products.model.Category;
import com.exemplo.products.model.Product;
import com.exemplo.products.repository.CategoryRepository;
import com.exemplo.products.repository.ProductRepository;
import com.exemplo.products.utils.ProductSpecification;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<Product> getAll(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean available,
        @RequestParam(required = false) String categoryName,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String orderBy
    ) {
        Specification<Product> spec = Specification
            .where(ProductSpecification.nameContains(name))
            .and(ProductSpecification.availableEquals(available))
            .and(ProductSpecification.categoryNameContains(categoryName));

        Sort sort = orderBy.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        return productRepository.findAll(spec, sort);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id){
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(product.get());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductRequest request) {
        Optional<Product> dbProduct = productRepository.findByName(request.getName());
        if (!dbProduct.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Product already exists."));
        }

        Optional<Category> category = categoryRepository.findById(request.getCategoryId());
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Category doesn't exist."));
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAvailable(request.isAvailable());
        product.setCategoryPath(category.get());

        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody ProductRequest request) {
        Optional<Category> category = categoryRepository.findById(request.getCategoryId());
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Category doesn't exist."));
        }

        Optional<Product> dbProduct = productRepository.findByName(request.getName());
        if (!dbProduct.isEmpty() && dbProduct.get().getId() != id) {
            return ResponseEntity.badRequest().body(Map.of("error", "Product already exists."));
        }

        return productRepository.findById(id)
                .map(p -> {
                    p.setName(request.getName());
                    p.setDescription(request.getDescription());
                    p.setPrice(request.getPrice());
                    p.setAvailable(request.isAvailable());
                    p.setCategoryPath(category.get());
                    return ResponseEntity.ok(productRepository.save(p));
                }).orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            productRepository.delete(productOpt.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}