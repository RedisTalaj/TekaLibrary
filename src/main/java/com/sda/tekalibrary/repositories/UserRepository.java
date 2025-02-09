package com.sda.tekalibrary.repositories;

import com.sda.tekalibrary.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select u from User u where u.username = :username and u.password = :password and u.role = 'User'")
    List<User> login(@Param("username") String username, @Param("password") String password, @Param("role") String role);

    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    User findByEmailAndPasswordAndRole(String email, String password, String role);
}
