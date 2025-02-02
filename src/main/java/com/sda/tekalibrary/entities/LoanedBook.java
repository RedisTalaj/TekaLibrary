package com.sda.tekalibrary.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "loaned_books")
public class LoanedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "loaned_book_id", nullable = false)
    private String loanedId;

    @Column(name = "loan_date", nullable = false)
    private String loanDate;

    @Column(name = "return_date", nullable = false)
    private String returnDate;

    @Column(name = "book_title", nullable = false)
    private String bookTitle;

}
