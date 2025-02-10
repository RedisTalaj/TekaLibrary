package com.sda.tekalibrary.repositories;

import com.sda.tekalibrary.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    //kerko libra ne baze te autorit
    List<Book> findBooksByAuthor(String author);

    //search-box
    List<Book> searchByTitle(String title);
}
