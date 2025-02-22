package com.sda.tekalibrary.services;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.repositories.BookRepository;
import com.sda.tekalibrary.repositories.LoanedBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    String UPLOAD_DIR = "src/main/resources/static/images/";

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    private LoanedBookRepository loanedBookRepository;

    @Autowired
    private FavouriteBookService favouriteBookService;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBook(Long id, Book updatedBook) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setDescription(updatedBook.getDescription());
            book.setIsbn(updatedBook.getIsbn());
            book.setCategory(updatedBook.getCategory());
            book.setImagePath(updatedBook.getImagePath());
            book.setQuantity(updatedBook.getQuantity());
            book.setPrice(updatedBook.getPrice());
            bookRepository.save(book);
        } else {
            throw new RuntimeException("Book with ID " + id + " not found.");
        }
    }

    public void deleteBook(Long id) {
        loanedBookRepository.deleteById(id);
        favouriteBookService.removeFavoriteBookByBookId(id);
        bookRepository.deleteById(id);
    }

    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findBooksByAuthor(author);
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.searchByTitle(title);
    }

    public Book getBookByIsbn(String isbn){
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> searchBookByAuthorOrTitle(String keyword){
        return bookRepository.searchByAuthorOrTitle(keyword);
    }

    public List<Book> getNewestBooks() {
        return bookRepository.findAll().stream()
                .sorted(Comparator.comparing(Book::getBookId).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Book> getBooksByCategoryComedy(){
        return bookRepository.findBooksByCategoryComedy();
    }

}
