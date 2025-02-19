package com.sda.tekalibrary.controllers;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

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
        // Merr librin nga databaza
        Book book = bookService.getBookById(id).get();

        // Fshi imazhin nëse ekziston
        if (book.getImagePath() != null && !book.getImagePath().isEmpty()) {
            Path filePath = Paths.get("src/main/resources/static" + book.getImagePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        }

        // Fshi librin nga databaza
        bookService.deleteBook(id);

        // Ridrejto në faqen kryesore
        return "redirect:/books";
    }

    //Admin Page - Search
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

    //Main Page -Search
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
    public String goToCategory(Model model){
        //10 Newest Books
        List<Book> newestBooks = bookService.getNewestBooks();
        model.addAttribute("books", newestBooks);

        //Books by category = Comedy
        List<Book> comedyBooks = bookService.getBooksByCategoryComedy();
        model.addAttribute("comedyBooks", comedyBooks);
        return "MainPage/main_page";
    }
}

