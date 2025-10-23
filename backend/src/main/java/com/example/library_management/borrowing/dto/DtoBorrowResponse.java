package com.example.library_management.borrowing.dto;

import com.example.library_management.book.model.Situation;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoBorrowResponse {

    private Long borrowingId;

    private Long userId;

    private Long bookId;

    private Date borrowedDate;

    private Date lastReturnDate;

    private Situation situation;

    private Double penaltyCost;
}
