package com.sda.tekalibrary.repositories;

import com.sda.tekalibrary.entities.FavoriteBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteBookRepository extends JpaRepository<FavoriteBook, Long> {
    @Modifying
    @Query("DELETE FROM FavoriteBook fb WHERE fb.id.bookId = :bookId")
    void deleteByBookId(@Param("bookId") Long bookId);

    @Modifying
    @Query("DELETE FROM FavoriteBook fb WHERE fb.id.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
