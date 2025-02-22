package com.sda.tekalibrary.repositories;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    //kerko libra ne baze te autorit
    List<Book> findBooksByAuthor(String author);

    //search-box
    List<Book> searchByTitle(String title);

    Book findByIsbn(String isbn);

    @Query("SELECT b FROM Book b ORDER BY b.bookId DESC LIMIT 10")
    List<Book> findTop10ByOrderByBookIdDesc();

    //kerko libra ne baze te autori ose titullit
    @Query(value = "select b from Book b where b.author like %:keyword% or b.title like %:keyword%")
    List<Book> searchByAuthorOrTitle(@Param("keyword") String keyword);

    //afisho te gjithe librat
    @Query(value = "select b from Book b")
    List<Book> findAllBooks();

    //afisho te gjitha librat sipas kategorise Comedy
    @Query(value = "select b from Book b where b.category = 'Comedy'")
    List<Book> findBooksByCategoryComedy();

    //afisho te gjitha librat sipas kategorise Drama
    @Query(value = "select b from Book b where b.category = 'Drama'")
    List<Book> findBooksByCategoryDrama();

    //afisho te gjitha librat sipas kategorise Fiction
    @Query(value = "select b from Book b where b.category = 'Fiction'")
    List<Book> findBooksByCategoryFiction();

    //afisho te gjitha librat sipas kategorise Horror
    @Query(value = "select b from Book b where b.category = 'Horror'")
    List<Book> findBooksByCategoryHorror();

    //afisho te gjithe librat sipas kategorise Non-Fiction
    @Query(value = "select b from Book b where b.category = 'Non-Fiction'")
    List<Book> findBooksByCategoryNonFiction();

    Optional<Book> findById(Long bookId);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.reviews WHERE b.bookId = :bookId")
    Optional<Book> findByIdWithReviews(@Param("bookId") Long bookId);
}
