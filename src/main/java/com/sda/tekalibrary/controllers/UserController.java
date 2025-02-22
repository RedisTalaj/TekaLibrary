package com.sda.tekalibrary.controllers;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.entities.LoanedBook;
import com.sda.tekalibrary.entities.User;
import com.sda.tekalibrary.services.BookService;
import com.sda.tekalibrary.services.LoanedBookService;
import com.sda.tekalibrary.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanedBookService loanedBookService;

    @GetMapping
    public String getAllUsers(Model model){
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "AdminPage/index";
    }

    @GetMapping("/signup")
    public String goToAddUsers(Model model){
        model.addAttribute("user", new User());
        return "signup/signup";
    }

    @PostMapping("/signup")
    public String addUser(@ModelAttribute("user")User user, Model model){
        User userExists = userService.getUserByEmail(user.getEmail());

        if (userExists != null && userExists.getEmail().equals(user.getEmail())) {
            model.addAttribute("errorMessageEmail", "Email already in use");
            return "signup/signup";
        }
        if (user.getPassword().length() < 7) {
            model.addAttribute("errorMessagePassword", "Password must be at least 7 characters long.");
            return "signup/signup";
        }
        if (!user.getPassword().matches(".*[A-Z].*")) {
            model.addAttribute("errorMessagePassword", "Password must contain at least one uppercase letter.");
            return "signup/signup";
        }
        userService.addUser(user);
        return "redirect:/users/login";
    }


    @GetMapping("/login")
    public String goToLogin(Model model){
        model.addAttribute("user", new User());
        return "Login/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user")User user,RedirectAttributes redirectAttributes, HttpSession session) {
        User logedInUser = userService.getUserByEmail(user.getEmail());
        //kontrollon ne qofte se perdoruesi ka vendosur te dhenat e sakta
        Boolean passwordAndEmailIncorrect = userService.getUserByEmailAndPassword(user.getEmail(), user.getPassword());
        if(!passwordAndEmailIncorrect){
            redirectAttributes.addFlashAttribute("errorMessageEmailOrPassword", "Email or password incorrect");
            return "redirect:/users/login";
        }
        else{
            if (logedInUser.getStatus().equals("Inactive")) {
                redirectAttributes.addFlashAttribute("errorMessageEmailOrPassword", "User is inactive");
                return "redirect:/users/login";
            }
            else if (logedInUser.getStatus().equals("Active")) {
                if (logedInUser.getPassword().equals(user.getPassword())) {
                    user.setRole(logedInUser.getRole());
                    if (logedInUser.getRole().equals("Admin")) {
                        session.setAttribute("user", logedInUser);
                        return "redirect:/users/user-profile";
                    } else {
                        //do dergohet te dashboard, por per placeholder vendosim login page
                        redirectAttributes.addFlashAttribute("errorMessageEmailOrPassword",
                                "Logged in successfully");
                        session.setAttribute("user", logedInUser);
                        return "redirect:/users/user-profile";
                    }
                }
            }
        }
        return "";
    }

    @GetMapping("/create")
    public String goToCreateUser(Model model){
        model.addAttribute("user", new User());
        return "AdminPage/create_user";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user, Model model) {
        User userExists = userService.getUserByEmail(user.getEmail());

        if (userExists != null && userExists.getEmail().equals(user.getEmail())) {
            model.addAttribute("errorMessage", "Email already in use");
            return "AdminPage/create_user";
        }
        if(user.getPassword().length() < 7){
            model.addAttribute("errorMessage", "Password must be at least 7 characters long.");
            return "AdminPage/create_user";
        }
        if (!user.getPassword().matches(".*[A-Z].*")) {
            model.addAttribute("errorMessage", "Password must contain at least one uppercase letter.");
            return "AdminPage/create_user";
        }
        else {
            userService.createUser(user);
            return "redirect:/users";
        }
    }

    @GetMapping("/delete/{id}")
    public String goToDeleteUser(Model model){
        model.addAttribute("user", new User());
        return "AdminPage/index";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@ModelAttribute("user") User user, @PathVariable Long id, RedirectAttributes redirectAttributes){
        User userAdmin = userService.getUserById(id);
        if (!"Admin".equals(userAdmin.getRole())) {
            userService.deleteUser(id);
            return "redirect:/users";
        } else {
            redirectAttributes.addFlashAttribute("errorMessageAdmin",
                    "This user is Admin and can't be deleted");
            return "redirect:/users";
        }
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam("keyword") String keyword, Model model) {
        List<User> users = userService.searchByUsername(keyword);
        if (users.isEmpty()) {
            model.addAttribute("errorMessageSearch", "No users found with the given keyword.");
        } else {
            model.addAttribute("users", users);
        }
        model.addAttribute("keyword", keyword);
        return "AdminPage/index";
    }

    @GetMapping("/update/{id}")
    public String goToUpdateUser(Model model, @PathVariable Long id){
        model.addAttribute("user", userService.getUserById(id));
        return "AdminPage/update_user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@ModelAttribute User user, @PathVariable Long id, RedirectAttributes redirectAttributes){
        User userAdmin = userService.getUserById(id);
        if("Admin".equals(userAdmin.getRole())){
            redirectAttributes.addFlashAttribute("errorMessageAdmin", "This user is admin and can't change its data.");
            return "redirect:/users/update/{id}";
        }else{
            userService.updateUser(id, user);
            return "redirect:/users";
        }
    }

    @GetMapping("/user-profile")
    public String getUserProfile(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user == null){
            return "redirect:/users/login";
        }
        model.addAttribute("user", user);
        return "MainPage/userAccount";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/users/login";
    }

    @GetMapping("/favourite")
    public String getFavouriteBooks(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }

        User userWithFavorites = userService.getUserWithFavoriteBooks(user.getUserId());
        model.addAttribute("user", userWithFavorites);

        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);

        return "MainPage/favourite_books";
    }

    @PostMapping("/favourite/toggle/{bookId}")
    @ResponseBody
    public ResponseEntity<String> toggleFavorite(@PathVariable Long bookId, @RequestParam Long userId) {
        try {
            User user = userService.getUserWithFavoriteBooks(userId);
            boolean isFavorite = user.getFavoriteBooks().stream()
                    .anyMatch(book -> book.getBookId() == bookId);

            if (isFavorite) {
                userService.removeBookFromFavorites(userId, bookId);
                return ResponseEntity.ok("Book removed from favorites");
            } else {
                userService.addBookToFavorites(userId, bookId);
                return ResponseEntity.ok("Book added to favorites");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/favourite/add/{bookId}")
    @ResponseBody
    public ResponseEntity<String> addToFavorites(@PathVariable Long bookId, @RequestParam Long userId) {
        try {
            userService.addBookToFavorites(userId, bookId);
            return ResponseEntity.ok("Book added to favorites");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/favourite/remove/{bookId}")
    @ResponseBody
    public ResponseEntity<String> removeFromFavorites(@PathVariable Long bookId, @RequestParam Long userId) {
        try {
            userService.removeBookFromFavorites(userId, bookId);
            return ResponseEntity.ok("Book removed from favorites");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/order-history")
    public String getOrderHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }

        List<LoanedBook> loanedBooks = loanedBookService.getLoanedBooksByUser(user.getUserId());
        model.addAttribute("loanedBooks", loanedBooks);

        return "MainPage/order_history";
    }

    //User profile edit
    @GetMapping("/edit/{id}")
    public String goEditUser(Model model, @PathVariable Long id){
        model.addAttribute("user", userService.getUserById(id));
        return "MainPage/userAccountEdit";
    }

    @PostMapping("/edit/{id}")
    public String editUser(@ModelAttribute User user, @PathVariable Long id, Model model, HttpSession httpSession){
        User userExists = userService.getUserByEmail(user.getEmail());
        User userLoggedIn = (User) httpSession.getAttribute("user");
        if (userExists != null && userExists.getEmail().equals(user.getEmail()) && !userLoggedIn.getEmail().equals(user.getEmail())) {
            model.addAttribute("errorMessage", "Email already in use");
            return "MainPage/userAccountEdit";
        }
        if(user.getPassword().length() < 7){
            model.addAttribute("errorMessage", "Password must be at least 7 characters long.");
            return "MainPage/userAccountEdit";
        }
        if (!user.getPassword().matches(".*[A-Z].*")) {
            model.addAttribute("errorMessage", "Password must contain at least one uppercase letter.");
            return "MainPage/userAccountEdit";
        }
        if(userLoggedIn.getEmail().equals(user.getEmail())){
            model.addAttribute("successMessage", "User updated successfully");
            userService.editUser(user, httpSession);
            return "redirect:/users/user-profile";
        }
        return "redirect:/users/user-profile";
    }
}
