package com.sda.tekalibrary.services;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.repositories.BookRepository;
import com.sda.tekalibrary.repositories.LoanedBookRepository;
import com.sda.tekalibrary.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private ReviewRepository reviewRepository;

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
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isPresent()) {
            Book book = existingBook.get();
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setCategory(updatedBook.getCategory());
            book.setDescription(updatedBook.getDescription());
            book.setIsbn(updatedBook.getIsbn());
            book.setQuantity(updatedBook.getQuantity());
            book.setPrice(updatedBook.getPrice());
            book.setImagePath(updatedBook.getImagePath()); // Update the image path
            bookRepository.save(book);
        }
    }

    public void deleteBook(Long id) {
        reviewRepository.deleteById(id);
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

    public List<Book> searchBookByAuthorOrTitle(String keyword) {
        return bookRepository.searchByAuthorOrTitle(keyword);
    }

    public List<Book> getNewestBooks() {
        return bookRepository.findTop10ByOrderByBookIdDesc();
    }

    public List<Book> getBooksByCategoryComedy(){
        return bookRepository.findBooksByCategoryComedy();
    }

    public List<Book> getBooksByCategoryDrama(){
        return bookRepository.findBooksByCategoryDrama();
    }

    public List<Book> getBooksByCategoryFiction(){
        return bookRepository.findBooksByCategoryFiction();
    }

    public List<Book> getBooksByCategoryHorror(){
        return bookRepository.findBooksByCategoryHorror();
    }

    public List<Book> getBooksByCategoryNonFiction(){
        return bookRepository.findBooksByCategoryNonFiction();
    }
}
