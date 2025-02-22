package com.sda.tekalibrary.controllers;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.entities.LoanedBook;
import com.sda.tekalibrary.entities.Review;
import com.sda.tekalibrary.entities.User;
import com.sda.tekalibrary.repositories.BookRepository;
import com.sda.tekalibrary.repositories.ReviewRepository;
import com.sda.tekalibrary.repositories.UserRepository;
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
import java.security.Principal;
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

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // Shfaq të gjithë librat
    @GetMapping
    public String getAllBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "Books/bookCenter";
    }

    // Shfaq formën për të shtuar një libër
    @GetMapping("/add")
    public String goToAddBooks(Model model) {
        model.addAttribute("book", new Book());
        return "Books/create_book";
    }

    // Shto një libër
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

    // Shfaq formën për të përditësuar një libër
    @GetMapping("/update/{id}")
    public String goToUpdate(Model model, @PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
        } else {
            model.addAttribute("errorMessage", "Book not found");
        }
        return "Books/update_book";
    }

    // Përditëso një libër
    @PostMapping("/update/{id}")
    public String updateBook(
            @ModelAttribute("book") Book book,
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) throws IOException {

        Optional<Book> existingBook = bookService.getBookById(id);
        if (existingBook.isPresent()) {
            Book currentBook = existingBook.get();
            if (!book.getIsbn().equals(currentBook.getIsbn())) {
                Book bookWithSameIsbn = bookService.getBookByIsbn(book.getIsbn());
                if (bookWithSameIsbn != null && !(bookWithSameIsbn.getBookId() == id)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "A book with this ISBN already exists.");
                    return "redirect:/books/update/" + id;
                }
            }
            if (!imageFile.isEmpty()) {
                if (currentBook.getImagePath() != null && !currentBook.getImagePath().isEmpty()) {
                    Path oldFilePath = Paths.get("src/main/resources/static" + currentBook.getImagePath());
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                    }
                }

                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path newFilePath = Paths.get("src/main/resources/static/images/" + fileName);
                Files.copy(imageFile.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
                book.setImagePath("/images/" + fileName);
            } else {
                book.setImagePath(currentBook.getImagePath());
            }
            bookService.updateBook(id, book);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found");
        }
        return "redirect:/books";
    }

    // Fshi një libër
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

    // Kërko libra ne book management
    @GetMapping("/search")
    public String goToSearch(@RequestParam("keyword") String keyword, Model model) {
        List<Book> books = bookService.searchBookByAuthorOrTitle(keyword);
        if (books.isEmpty()) {
            model.addAttribute("errorMessageBookEmpty", "No books or author found with the given keyword");
        } else {
            model.addAttribute("books", books);
        }
        model.addAttribute("keyword", keyword);
        return "Books/bookCenter";
    }

    //kerkon libra ne faqen kryesore
    @GetMapping("/searchMainPage")
    public String goToSearchMain(@RequestParam("keyword") String keyword, Model model) {
        List<Book> books = bookService.searchBookByAuthorOrTitle(keyword);
        if (books.isEmpty()) {
            model.addAttribute("errorMessageBookEmpty", "No books or author found with the given keyword");
        } else {
            model.addAttribute("books", books);
        }
        model.addAttribute("keyword", keyword);
        return "MainPage/main_page"; //
    }
    // Shfaq faqen kryesore
    @GetMapping("/MainPage")
    public String getMainPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            User userWithFavorites = userService.getUserWithFavoriteBooks(user.getUserId());
            model.addAttribute("user", userWithFavorites);
        } else {
            model.addAttribute("user", null);
        }

        List<Book> newestBooks = bookService.getNewestBooks();
        List<Book> books = bookService.getAllBooks();
        List<Book> comedyBooks = bookService.getBooksByCategoryComedy();
        List<Book> dramaBooks = bookService.getBooksByCategoryDrama();
        List<Book> fictionBooks = bookService.getBooksByCategoryFiction();
        List<Book> horrorBooks = bookService.getBooksByCategoryHorror();
        List<Book> nonFictionBooks = bookService.getBooksByCategoryNonFiction();


        model.addAttribute("newestBooks", newestBooks);
        model.addAttribute("books", books);
        model.addAttribute("comedyBooks", comedyBooks);
        model.addAttribute("dramaBooks", dramaBooks);
        model.addAttribute("fictionBooks", fictionBooks);
        model.addAttribute("horrorBooks", horrorBooks);
        model.addAttribute("nonFictionBooks", nonFictionBooks);
        return "MainPage/main_page";
    }

    // Shfaq detajet e një libri
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

    // Huazo një libër
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

    @PostMapping("/{id}/review")
    public String addReview(@PathVariable("id") Long bookId,
                            @RequestParam String rating,
                            @RequestParam String reviewTitle,
                            @RequestParam String comment,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        // Merr përdoruesin nga session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login"; // Ridrejto nëse përdoruesi nuk është i kyçur
        }

        // Kontrollo nëse libri ekziston
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (!bookOptional.isPresent()) {
            return "redirect:/books/MainPage"; // Ridrejto nëse libri nuk gjendet
        }

        Book book = bookOptional.get();

        // Kontrollo nëse përdoruesi ka një review ekzistues për këtë libër
        Optional<Review> existingReview = reviewRepository.findByUserAndBook(user, book);
        if (existingReview.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already submitted a review for this book.");
            return "redirect:/books/details/" + bookId; // Ridrejto nëse përdoruesi ka një review ekzistues
        }

        // Krijo një review të ri
        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(rating);
        review.setReviewTitle(reviewTitle);
        review.setComment(comment);

        // Ruaj review-n në bazën e të dhënave
        reviewRepository.save(review);

        redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully!");
        return "redirect:/books/details/" + bookId;
    }

    // Fshi një review
    @DeleteMapping("/review/{id}")
    @ResponseBody
    public String deleteReview(@PathVariable("id") Long reviewId) {
        reviewRepository.deleteById(reviewId);
        return "Review deleted successfully";
    }
}