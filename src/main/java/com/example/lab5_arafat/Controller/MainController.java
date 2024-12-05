package com.example.lab5_arafat.Controller;

import com.example.lab5_arafat.Entity.Category;
import com.example.lab5_arafat.Entity.Task;
import com.example.lab5_arafat.Entity.User;
import com.example.lab5_arafat.Service.Implementation.CategoryServiceImpl;
import com.example.lab5_arafat.Service.Implementation.TaskServiceImpl;
import com.example.lab5_arafat.Service.Implementation.UserServiceImpl;
import com.example.lab5_arafat.Service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class MainController {

    private final UserServiceImpl userService;
    private final TaskServiceImpl taskService;
    private final CategoryServiceImpl categoryService;
    private final PhotoService photoService;

    @Autowired
    public MainController(UserServiceImpl userService, TaskServiceImpl taskService,
                          CategoryServiceImpl categoryService, PhotoService photoService) {
        this.userService = userService;
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.photoService = photoService;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails currentUser,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(value = "categoryId", required = false) Long categoryId,
                       Model model) {
        return loadTasks(currentUser, page, null, categoryId, model);
    }

    @GetMapping("/search")
    public String searchTasks(@AuthenticationPrincipal UserDetails currentUser,
                              @RequestParam("query") String query,
                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        return loadTasks(currentUser, page, query, categoryId, model);
    }

    private String loadTasks(UserDetails currentUser, int page, String query, Long categoryId, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }
        Pageable pageable = PageRequest.of(page, 3);
        Page<Task> taskPage;
        if (query == null || query.trim().isEmpty()) {
            if (categoryId != null && categoryId != 0) {
                taskPage = taskService.findTasksByCategoryAndUser(categoryId, user.getId(), pageable);
            } else {
                taskPage = taskService.findTasksByUser(user.getId(), pageable);
            }
        } else {
            if (categoryId != null && categoryId != 0) {
                taskPage = taskService.searchTasksByTitleAndCategoryAndUser(query.trim(), categoryId, user.getId(), pageable);
            } else {
                taskPage = taskService.searchTasksByTitleAndUser(query.trim(), user.getId(), pageable);
            }
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("query", query != null ? query : "");
        model.addAttribute("categories", categoryService.getAllCategories());

        return "homePage";
    }

    @GetMapping("/task/new")
    public String newTask(Model model) {
        populateTaskFormAttributes(new Task(), model);
        return "newTask";
    }

    @PostMapping("/task")
    public String addTask(Task task,
                          @RequestParam("categoryId") Long categoryId,
                          @AuthenticationPrincipal UserDetails currentUser) {
        populateAndSaveTask(task, categoryId, currentUser);
        return "redirect:/home";
    }

    @GetMapping("/task/{id}")
    public String viewTaskDetails(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return "redirect:/home";
        }
        model.addAttribute("task", task);
        return "aboutTask";
    }

    @GetMapping("/task/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return "redirect:/home";
        }
        populateTaskFormAttributes(task, model);
        return "editTask";
    }

    @PostMapping("/task/update/{id}")
    public String updateTask(@PathVariable Long id,
                             Task task,
                             @RequestParam("categoryId") Long categoryId,
                             @AuthenticationPrincipal UserDetails currentUser) {
        Task existTask = taskService.getTaskById(id);
        if (existTask != null) {
            task.setId(id);
            if (currentUser != null) {
                User user = userService.findByUsername(currentUser.getUsername());
                if (user == null) {
                    throw new RuntimeException("User not found");
                }
                task.setUser(user);
            }
            populateAndSaveTask(task, categoryId, null);
        }
        return "redirect:/home";
    }

    @GetMapping("/task/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return "redirect:/home";
    }

    private void populateTaskFormAttributes(Task task, Model model) {
        model.addAttribute("task", task);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", Task.Status.values());
        model.addAttribute("priorities", Task.Priority.values());
    }

    private void populateAndSaveTask(Task task, Long categoryId, UserDetails currentUser) {
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        if (currentUser != null) {
            User user = userService.findByUsername(currentUser.getUsername());
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            task.setUser(user);
        }

        task.setCategory(category);
        taskService.addTask(task);
    }

    @PostMapping("/user/photoUpload")
    public String uploadUserPhoto(@AuthenticationPrincipal UserDetails currentUser,
                                  @RequestParam("file") MultipartFile file,
                                  Model model) {
        try {
            User user = userService.findByUsername(currentUser.getUsername());
            if (user == null) {
                return "redirect:/login";
            }
            photoService.uploadUserPhoto(file, user);
            return "redirect:/home";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload photo");
            return "errorPage";
        }
    }
}
