package com.example.lab5_arafat.Service.Implementation;

import com.example.lab5_arafat.Entity.User;
import com.example.lab5_arafat.Repository.UserRepo;
import com.example.lab5_arafat.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userRepo.findById(userId);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepo.deleteById(userId);
    }

    public void savePasswordResetCode(User user, String code) {
        user.setResetCode(code);
        userRepo.save(user);
    }

    public boolean isResetCodeValid(User user, String code) {
        return user.getResetCode() != null && user.getResetCode().equals(code);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetCode(null);
        userRepo.save(user);
    }
}


