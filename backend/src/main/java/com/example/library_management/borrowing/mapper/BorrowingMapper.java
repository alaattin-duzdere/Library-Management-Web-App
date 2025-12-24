package com.example.library_management.borrowing.mapper;

import com.example.library_management.borrowing.dto.DtoBorrowResponse;
import com.example.library_management.borrowing.model.Borrowing;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class BorrowingMapper {

    public DtoBorrowResponse borrowingToDtoBorrowResponse(Borrowing borrowing) {
        if (borrowing == null) {
            return null;
        }
        DtoBorrowResponse dto = new DtoBorrowResponse();

        BeanUtils.copyProperties(borrowing, dto);
        dto.setBorrowingId(borrowing.getId());
        dto.setUserId(borrowing.getUser().getId());
        dto.setUserName(borrowing.getUser().getUsername());
        if (borrowing.getBook()==null){
            dto.setBookId(null);
            dto.setBookTitle("Book has been deleted");
        }
        else {
            dto.setBookId(borrowing.getBook().getId());
            dto.setBookTitle(borrowing.getBook().getTitle());
        }

        return dto;
    }
}
