package com.example.lab5_arafat.CategoryData;

import com.example.lab5_arafat.Entity.Category;
import com.example.lab5_arafat.Repository.CategoryRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryDataInitia implements CommandLineRunner {

    private final CategoryRepo categoryRepo;
    public CategoryDataInitia(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepo.count() == 0) {
            Category category1 = new Category();
            category1.setName("Work");
            categoryRepo.save(category1);

            Category category2 = new Category();
            category2.setName("Study");
            categoryRepo.save(category2);

            Category category3 = new Category();
            category3.setName("For Myself");
            categoryRepo.save(category3);

            System.out.println("Categories added to the database.");
        }
    }
}

