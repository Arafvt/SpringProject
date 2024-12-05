package com.example.lab5_arafat.Service;

import com.example.lab5_arafat.Entity.User;
import com.example.lab5_arafat.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PhotoService {
    @Value("${upload.dir}")
    private String uploadDir;

    private final UserRepo userRepo;

    public PhotoService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User uploadUserPhoto(MultipartFile file, User user) throws IOException {
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());

        user.setPhoto(fileName);
        return userRepo.save(user);
    }
}

