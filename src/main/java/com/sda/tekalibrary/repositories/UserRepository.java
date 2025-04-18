package com.sda.tekalibrary.repositories;

import com.sda.tekalibrary.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAndPassword(String email, String password);
    User findByEmail(String email);
    @Query(value="select u from User u where u.username like %:keyword%")
    List<User> searchByUsername(String keyword);

    Optional<User> findByUsername(String username);

    User getUserByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.favoriteBooks WHERE u.userId = :userId")
    Optional<User> findByIdWithFavoriteBooks(@Param("userId") Long userId);

}
