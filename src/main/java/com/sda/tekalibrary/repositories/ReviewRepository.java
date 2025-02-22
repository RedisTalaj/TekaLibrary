package com.sda.tekalibrary.repositories;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.entities.Review;
import com.sda.tekalibrary.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndBook(User user, Book book);
}
