package com.example.lab5_arafat.Controller;

import com.example.lab5_arafat.Entity.Category;
import com.example.lab5_arafat.Entity.Task;
import com.example.lab5_arafat.Entity.User;
import com.example.lab5_arafat.Repository.TaskRepo;
import com.example.lab5_arafat.Repository.UserRepo;
import com.example.lab5_arafat.Service.Implementation.CategoryServiceImpl;
import com.example.lab5_arafat.Service.Implementation.TaskServiceImpl;
import com.example.lab5_arafat.Service.Implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class MainController {
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;
    private final UserServiceImpl userService;
    private final TaskServiceImpl taskService;
    private final CategoryServiceImpl categoryService;
    @Autowired
    public MainController(TaskRepo taskRepo, UserRepo userRepo, UserServiceImpl userService,
                          TaskServiceImpl taskService, CategoryServiceImpl categoryService) {
        this.userRepo = userRepo;
        this.taskRepo = taskRepo;
        this.userService = userService;
        this.taskService = taskService;
        this.categoryService = categoryService;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        String username = currentUser.getUsername();
        User user = userService.findByUsername(username);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", user);
        try {
            List<Task> tasks = taskService.findTasksByUser(user.getId());
            model.addAttribute("tasks", tasks);
        } catch (Exception e) {
            System.err.println("Ошибка при чтении задач: " + e.getMessage());
            model.addAttribute("errorMessage",
                    "Не удалось загрузить задачи. Обратитесь к администратору.");
            return "error-page";
        }
        return "homePage";
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {

        String username = authentication.getName();
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/search")
    public String searchTasks(@RequestParam("query") String query, Model model) {
        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("tasks", taskRepo.findAll());
        } else {
            model.addAttribute("tasks", taskRepo.findByTitleContainingIgnoreCase(query.trim()));
        }
        return "homePage";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "category";
    }

    @GetMapping("/tasks/{categoryId}")
    public String tasks(@PathVariable Long categoryId, Model model) {
        List<Task> tasks = taskService.findTasksByCategory(categoryId);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @GetMapping("/task/new")
    public String newTask(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", Task.Status.values());
        model.addAttribute("priorities", Task.Priority.values());
        return "newTask";
    }

    @PostMapping("/task")
    public String addTask(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("dueDate") String dueDate,
            @RequestParam(value = "status", required = false, defaultValue = "PENDING") String status,
            @RequestParam("priority") String priority,
            @RequestParam("categoryId") Long categoryId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        User user = userService.findByUsername(currentUser.getUsername());
        Category category = categoryService.getCategoryById(categoryId);

        if (user != null && category != null) {
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate.isEmpty() ? null : LocalDate.parse(dueDate));
            task.setStatus(Task.Status.valueOf(status.toUpperCase()));
            task.setPriority(Task.Priority.valueOf(priority.toUpperCase()));
            task.setCategory(category);
            task.setUser(user);

            taskService.addTask(task);
        }

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

    @GetMapping("/task/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return "redirect:/home";
    }

    @GetMapping("/task/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return "redirect:/home";
        }
        model.addAttribute("task", task);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", Task.Status.values());
        model.addAttribute("priorities", Task.Priority.values());
        return "editTask";
    }

    @PostMapping("/task/update/{id}")
    public String updateTask(@PathVariable Long id,
                             @RequestParam("title") String title,
                             @RequestParam("description") String description,
                             @RequestParam("dueDate") String dueDate,
                             @RequestParam("status") String status,
                             @RequestParam("priority") String priority,
                             @RequestParam("categoryId") Long categoryId) {
        Task task = taskService.getTaskById(id);
        if (task != null) {
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate.isEmpty() ? null : LocalDate.parse(dueDate));
            task.setStatus(Task.Status.valueOf(status.toUpperCase()));
            task.setPriority(Task.Priority.valueOf(priority.toUpperCase()));
            task.setCategory(categoryService.getCategoryById(categoryId));

            taskService.updateTask(task);
        }
        return "redirect:/home";
    }

//    @GetMapping("/home")
//    public String home(@AuthenticationPrincipal UserDetails currentUser,
//                       @RequestParam(defaultValue = "0") int page, // Параметр для страницы
//                       Model model) {
//        if (currentUser == null) {
//            return "redirect:/login";
//        }
//
//        String username = currentUser.getUsername();
//        User user = userService.findByUsername(username);
//
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("currentUser", user);
//
//        Pageable pageable = PageRequest.of(page, 5);
//
//        Page<Task> taskPage = taskService.findTasksByUser(user.getId(), pageable);
//        model.addAttribute("tasks", taskPage.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", taskPage.getTotalPages());
//
//        return "homePage";
//    }
}
