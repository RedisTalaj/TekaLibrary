package com.sda.tekalibrary.repositories;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.entities.LoanedBook;
import com.sda.tekalibrary.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanedBookRepository extends JpaRepository<LoanedBook, Long> {
    List<LoanedBook> findByUserUserId(Long userId);

    boolean existsByUserAndBook(User user, Book book);
    void deleteByBook(Book book);
    void deleteByUser(User user);
}
