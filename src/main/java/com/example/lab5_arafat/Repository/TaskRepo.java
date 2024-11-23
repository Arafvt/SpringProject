package com.example.lab5_arafat.Repository;

import com.example.lab5_arafat.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByCategoryId(Long categoryId);
    List<Task> findByTitleContainingIgnoreCase(String title);
}


