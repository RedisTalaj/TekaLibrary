package com.sda.tekalibrary.services;

import com.sda.tekalibrary.entities.User;
import com.sda.tekalibrary.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void addUser(User user){
        user.setRole("User");
        user.setStatus("Active");
        userRepository.save(user);
    }

    public void createUser(User user){
        user.setStatus("Active");
        userRepository.save(user);
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElse(null);
    }

    public void updateUser(Long userId, User updateUser){
        User user = getUserById(userId);
        user.setUsername(updateUser.getUsername());
        user.setStatus(updateUser.getStatus());
        userRepository.save(user);
    }

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }

    public Boolean getUserByEmailAndPassword(String email, String password){
        return userRepository.existsByEmailAndPassword(email, password);

    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public List<User> searchByUsername(String keyword){
        return userRepository.searchByUsername(keyword);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User getUserProfile(String email){
        User user = userRepository.findByEmail(email);
        return user;
    }
}
