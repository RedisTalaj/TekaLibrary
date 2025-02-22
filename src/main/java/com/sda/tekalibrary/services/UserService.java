package com.sda.tekalibrary.services;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.entities.LoanedBook;
import com.sda.tekalibrary.entities.User;
import com.sda.tekalibrary.repositories.BookRepository;
import com.sda.tekalibrary.repositories.LoanedBookRepository;
import com.sda.tekalibrary.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanedBookRepository loanedBookRepository;

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

    public User getUserByUsername(String username){
        return userRepository.getUserByUsername(username);
    }

    @Transactional
    public void addBookToFavorites(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        user.getFavoriteBooks().add(book);
        userRepository.save(user);
    }

    @Transactional
    public void removeBookFromFavorites(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        user.getFavoriteBooks().remove(book);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserWithFavoriteBooks(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<LoanedBook> getLoanedBooksByUser(Long userId) {
        return loanedBookRepository.findByUserUserId(userId);
    }
}
