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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoanedBookService loanedBookService;

    @GetMapping
    public String getAllBooks(Model model){
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "Books/bookCenter";
    }

    @GetMapping("/add")
    public String goToAddBooks(Model model){
        model.addAttribute("book", new Book());
        return "Books/create_book";
    }

    @PostMapping("/add")
    public String addBook(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("isbn") String isbn,
            @RequestParam("quantity") int quantity,
            @RequestParam("price") double price,
            @RequestParam("image") MultipartFile imageFile,
            Model model, RedirectAttributes redirectAttributes) throws IOException {

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setDescription(description);
        book.setIsbn(isbn);
        book.setQuantity(quantity);
        book.setPrice(price);

        if (bookService.getBookByIsbn(isbn) != null && book.getIsbn().equals(bookService.getBookByIsbn(isbn).getIsbn())) {
            model.addAttribute("errorMessage", "This book already exists in the library");
            return "Books/create_book";
        }
        if (!imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get("src/main/resources/static/images/" + fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            book.setImagePath("/images/" + fileName);
        }
        bookService.addBook(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book added successfully");
        return "redirect:/books/add";
    }

    @GetMapping("/update/{id}")
    public String goToUpdate(Model model, @PathVariable Long id){
        model.addAttribute("book", bookService.getBookById(id));
        return "Books/update_book";
    }

    @PostMapping("/update/{id}")
    public String updateBook(@ModelAttribute("book") Book book, @PathVariable Long id){
        bookService.updateBook(id, book);
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    public String goToDelete(Model model){
        model.addAttribute("book", new Book());
        return "Books/bookCenter";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) throws IOException {
        Book book = bookService.getBookById(id).get();

        if (book.getImagePath() != null && !book.getImagePath().isEmpty()) {
            Path filePath = Paths.get("src/main/resources/static" + book.getImagePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        }
        bookService.deleteBook(id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String goToSearch(@RequestParam("keyword") String keyword,Model model){
        List<Book> books = bookService.searchBookByAuthorOrTitle(keyword);
        if(books.isEmpty()){
            model.addAttribute("errorMessageBookEmpty", "No books or author found with the given keyword");
        }else{
            model.addAttribute("books", books);
        }
        model.addAttribute("keyword", keyword);
        return "Books/bookCenter";
    }

    @GetMapping("/searchMainPage")
    public String searchBooks(@RequestParam("keyword") String keyword, Model model){
        List<Book> books = bookService.searchBookByAuthorOrTitle(keyword);
        if(books.isEmpty()){
            model.addAttribute("errorMessageBookEmpty", "No books or author found with the given keyword");
        }else{
            model.addAttribute("books", books);
        }
        model.addAttribute("keyword", keyword);
        return "MainPage/main_page";
    }

    @GetMapping("/MainPage")
    public String getMainPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            User userWithFavorites = userService.getUserWithFavoriteBooks(user.getUserId());
            model.addAttribute("user", userWithFavorites);
        } else {
            model.addAttribute("user", null);
        }

        List<Book> books = bookService.getAllBooks();
        List<Book> comedyBooks = bookService.getBooksByCategoryComedy();
        model.addAttribute("books", books);
        model.addAttribute("comedyBooks", comedyBooks);

        return "MainPage/main_page";
    }

    @GetMapping("/details/{id}")
    public String getBookDetails(@PathVariable Long id, Model model, HttpSession httpSession) {
        Optional<Book> bookOptional = bookService.getBookById(id);
        User user = (User) httpSession.getAttribute("user");
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            boolean hasLoaned = loanedBookService.hasUserLoanedBook(user, book);
            model.addAttribute("book", book);
            model.addAttribute("user", user);
            model.addAttribute("hasLoaned", hasLoaned);
            return "MainPage/book-details";
        } else {
            return "redirect:/books";
        }
    }

    @PostMapping("/loan/{bookId}")
    public ResponseEntity<String> loanBook(@PathVariable Long bookId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
            }

            Optional<Book> bookOptional = bookService.getBookById(bookId);
            if (bookOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }

            Book book = bookOptional.get();
            if (book.getQuantity() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Book is not available for loan");
            }

            LoanedBook loanedBook = new LoanedBook();
            loanedBook.setUser(user);
            loanedBook.setBook(book);
            loanedBook.setLoanDate(LocalDate.now());
            loanedBook.setReturnDate(LocalDate.now().plusDays(7));

            loanedBookService.saveLoanedBook(loanedBook);

            book.setQuantity(book.getQuantity() - 1);
            bookService.updateBook(bookId, book);

            return ResponseEntity.ok("Book loaned successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}