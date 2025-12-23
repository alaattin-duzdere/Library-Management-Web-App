package com.example.library_management.borrowing.model;

import com.example.library_management.book.model.Book;
import com.example.library_management.book.model.Situation;
import com.example.library_management.common.model.BaseEntity;
import com.example.library_management.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Borrowing extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private Book book;

    private Date borrowedDate;

    private Date lastReturnDate;

    private Date returnDate;

}
