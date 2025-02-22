package com.sda.tekalibrary.services;

import com.sda.tekalibrary.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Transactional
    public void removeReviewUserByUserId(Long userId) {
        reviewRepository.deleteByUserId(userId);
    }
}
