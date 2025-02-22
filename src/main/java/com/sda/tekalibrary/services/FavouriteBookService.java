package com.sda.tekalibrary.services;

import com.sda.tekalibrary.repositories.FavouriteBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavouriteBookService {
    @Autowired
    private FavouriteBookRepository favouriteBookRepository;

    @Transactional
    public void removeFavoriteBookByBookId(Long bookId) {
        favouriteBookRepository.deleteByBookId(bookId);
    }

    @Transactional
    public void removeFavoriteBookByUserId(Long userId) {
        favouriteBookRepository.deleteByUserId(userId);
    }
}
