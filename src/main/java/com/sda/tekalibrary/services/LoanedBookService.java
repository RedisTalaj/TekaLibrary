package com.sda.tekalibrary.services;

import com.sda.tekalibrary.entities.Book;
import com.sda.tekalibrary.entities.LoanedBook;
import com.sda.tekalibrary.entities.User;
import com.sda.tekalibrary.repositories.LoanedBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LoanedBookService {
    @Autowired
    private LoanedBookRepository loanedBookRepository;

    @Transactional
    public void saveLoanedBook(LoanedBook loanedBook) {
        loanedBookRepository.save(loanedBook);
    }

    @Transactional(readOnly = true)
    public List<LoanedBook> getLoanedBooksByUser(Long userId) {
        return loanedBookRepository.findByUserUserId(userId);
    }

    public boolean hasUserLoanedBook(User user, Book book) {
        return loanedBookRepository.existsByUserAndBook(user, book);
    }

    public boolean existsByUserAndBook(User user, Book book) {
        return loanedBookRepository.existsByUserAndBook(user, book);
    }
}
