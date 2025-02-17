package com.sda.tekalibrary.controllers;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*") // Lejon kërkesat nga frontend-i (ndrysho sipas nevojës)
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String getAllBooks(Model model){
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "Book/bookCenter";
    }

    @GetMapping("/add")
    public String goToAddBooks(Model model){
        model.addAttribute("book", new Book());
        return "Books/create_book";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("book") Book book, Model model, Long id){
        Book bookExists = bookService.getBookByIsbn(book.getIsbn());

        if(bookExists != null && bookExists.getIsbn().equals(book.getIsbn())){
            model.addAttribute("errorMessage", "Book already exists!");
            return "Books/create_books";
        }else{
            bookService.addBook(book);
            return "redirect:/books";
        }
    }

    @GetMapping("/update/{id}")
    public String goToUpdate(Model model, @PathVariable Long id){
        model.addAttribute("book", bookService.getBookById(id));
        return "Books/update_book";
    }

    @PostMapping("/update/{id}")
    public String updateBook(@ModelAttribute("book") Book book, Model model, @PathVariable Long id){
        bookService.updateBook(id, book);
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    public String goToDelete(Model model, @PathVariable Long id){
        model.addAttribute("book", bookService.getBookById(id));
        return "Books/bookCenter";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@ModelAttribute("book")Book book, Model model, @PathVariable Long id){
        bookService.deleteBook(id);
        return "redirect:/book";
    }

    @GetMapping("/search")
    public String goToSearch(@RequestParam("author") String author, @RequestParam("title") String title,Model model){
        List<Book> books = bookService.searchBookByAuthorOrTitle(author, title);
        if(books.isEmpty()){
            model.addAttribute("errorMessageBookEmpty", "No books or author found with the given keyword");
        }else{
            model.addAttribute("books", books);
        }
        model.addAttribute("author", author);
        model.addAttribute("title", title);
        return "Books/bookCenter";
    }
}

