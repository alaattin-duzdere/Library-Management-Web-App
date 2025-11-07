package com.example.library_management.borrowing.model;

import com.example.library_management.book.model.Book;
import com.example.library_management.book.model.Situation;
import com.example.library_management.common.model.BaseEntity;
import com.example.library_management.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Borrowing extends BaseEntity {

    @ManyToOne
    private User user;

    @ManyToOne
    private Book book;

    private Date borrowedDate;

    private Date lastReturnDate;

    private Date returnDate;

}
