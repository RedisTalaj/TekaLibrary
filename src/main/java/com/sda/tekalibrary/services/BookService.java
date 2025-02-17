package com.sda.tekalibrary.services;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

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
            book.setImage(updatedBook.getImage());
            book.setQuantity(updatedBook.getQuantity());
            book.setPrice(updatedBook.getPrice());
            bookRepository.save(book);
        } else {
            throw new RuntimeException("Book with ID " + id + " not found.");
        }
    }

    public void deleteBook(Long id) {
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

    public List<Book> searchBookByAuthorOrTitle(String author, String title){
        return bookRepository.findBooksByAuthorOrTitle(author, title);
    }
}
