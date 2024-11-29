package com.example.lab5_arafat.Repository;

import com.example.lab5_arafat.Entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByCategoryId(Long categoryId);
    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByTitleContainingIgnoreCaseAndUserId(String title, Long userId, Pageable pageable);
}


