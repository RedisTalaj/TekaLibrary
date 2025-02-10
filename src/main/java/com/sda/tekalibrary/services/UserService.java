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

    public void createUser(User user){
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

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }

    public Boolean getUserByEmailAndPassword(String email, String password){
        return userRepository.existsByEmailAndPassword(email, password);

    }


    public User getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User login(String email, String password, String role) {
        User user = userRepository.findByEmailAndPasswordAndRole(email, password, role);
        if (email.isEmpty()) {
            throw new RuntimeException("Invalid email, password");
        }
        return user;
    }

    public User getUserByRole(String role){
        return userRepository.findByRole(role);
    }
}
