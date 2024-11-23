package com.example.lab5_arafat.Service.Implementation;

import com.example.lab5_arafat.Entity.Category;
import com.example.lab5_arafat.Repository.CategoryRepo;
import com.example.lab5_arafat.Service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;
    public CategoryServiceImpl(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepo.findById(categoryId).orElse(null);
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }

    @Override
    public Optional<Category> findById(Long categoryId) {
        return categoryRepo.findById(categoryId);
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        categoryRepo.deleteById(categoryId);
    }

    @Override
    public Category updateCategory(Long categoryId, Category updatedCategory) {
        Optional<Category> existingCategoryOptional = categoryRepo.findById(categoryId);
        if (existingCategoryOptional.isPresent()) {
            Category existingCategory = existingCategoryOptional.get();
            existingCategory.setName(updatedCategory.getName());
            return categoryRepo.save(existingCategory);
        }
        return null;
    }
}
