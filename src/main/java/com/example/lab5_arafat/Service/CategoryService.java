package com.example.lab5_arafat.Service;

import com.example.lab5_arafat.Entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long categoryId);
    Category saveCategory(Category category);
    Optional<Category> findById(Long categoryId);
    void deleteCategoryById(Long categoryId);
    Category updateCategory(Long categoryId, Category updatedCategory);
}
