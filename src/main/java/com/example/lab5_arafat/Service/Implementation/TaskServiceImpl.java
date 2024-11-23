package com.example.lab5_arafat.Service.Implementation;

import com.example.lab5_arafat.Entity.Task;
import com.example.lab5_arafat.Repository.TaskRepo;
import com.example.lab5_arafat.Service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepo taskRepo;
    public TaskServiceImpl(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    @Override
    public List<Task> findTasksByUser(Long userId) {
        return taskRepo.findByUserId(userId);
    }

    @Override
    public void addTask(Task task) {
        taskRepo.save(task);
    }

    @Override
    public List<Task> findTasksByCategory(Long categoryId) {
        return taskRepo.findByCategoryId(categoryId);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteTaskById(Long taskId) {
        taskRepo.deleteById(taskId);
    }

    @Override
    public Task updateTask(Long taskId, Task updatedTask) {
        Optional<Task> existingTaskOptional = taskRepo.findById(taskId);
        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setCategory(updatedTask.getCategory());
            return taskRepo.save(existingTask);
        }
        return null;
    }

    @Override
    public Optional<Task> findTaskById(Long taskId) {
        return taskRepo.findById(taskId);
    }

    @Override
    public List<Task> findAllTasks() {
        return taskRepo.findAll();
    }
}

