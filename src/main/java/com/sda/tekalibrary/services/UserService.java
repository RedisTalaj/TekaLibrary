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
        userRepository.save(user);
    }

    public User getUserById(Long id){
        return userRepository.findById(id).get();
    }

    public void updateUser(Long id, User updateUser){
        User user = getUserById(id);
        user.setUsername(updateUser.getUsername());
        user.setLastname(updateUser.getLastname());
        user.setEmail(updateUser.getEmail());
        user.setAddress(updateUser.getAddress());
        user.setPassword(updateUser.getPassword());
        user.setAge(updateUser.getAge());
        userRepository.save(user);
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
