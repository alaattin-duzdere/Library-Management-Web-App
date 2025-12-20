package com.example.library_management.borrowing.service.impl;

import com.example.library_management.book.model.Book;
import com.example.library_management.book.repository.BookRepository;
import com.example.library_management.borrowing.repository.BorrowingSpecification;
import com.example.library_management.borrowing.dto.DtoBorrowResponse;
import com.example.library_management.borrowing.model.Borrowing;
import com.example.library_management.book.model.Situation;
import com.example.library_management.borrowing.repository.BorrowingRepository;
import com.example.library_management.borrowing.service.IBorrowingService;
import com.example.library_management.common.util.SecurityUtils;
import com.example.library_management.exceptions.client.ConflictException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.penalties.model.Penalty;
import com.example.library_management.penalties.model.StateOfPenalty;
import com.example.library_management.penalties.repository.PenaltyRepository;
import com.example.library_management.user.model.User;
import com.example.library_management.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class BorrowingServiceImpl implements IBorrowingService {

    @Value("${durationDay}")
    private Long durationDay;

    @Value("${penaltyCostPerDay}")
    private Double penaltyCostPerDay;

    private final BorrowingRepository borrowingRepository;

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    private final PenaltyRepository penaltyRepository;

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository, BookRepository bookRepository, UserRepository userRepository, PenaltyRepository penaltyRepository) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.penaltyRepository = penaltyRepository;
    }

    private DtoBorrowResponse borrowingToDtoBorrowResponse(Borrowing borrowing) {
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
    @Override
    public DtoBorrowResponse borrowBook(Long bookId) {
        Long userId = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", " id", userId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book"," id", bookId));

        if (book.getSituation() != Situation.AVAILABLE){
            throw new ConflictException("Book is not available for borrowing");
        }

        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowedDate(new Date())
                .lastReturnDate(new Date(System.currentTimeMillis() + durationDay*24*60*60*1000))
                .build();
        borrowing.setCreateTime(new Date());

        book.setSituation(Situation.BORROWED);
        bookRepository.save(book);

        return borrowingToDtoBorrowResponse(borrowingRepository.save(borrowing));
    }

    @Override
    public Page<DtoBorrowResponse> getBorrowings(Pageable pageable, Long borrowingId, Long userId, Long bookId) {
        Specification<Borrowing> spec = BorrowingSpecification.findByCriteria(borrowingId, userId, bookId);
        return borrowingRepository.findAll(spec, pageable).map(this::borrowingToDtoBorrowResponse);
    }

    @Override
    public DtoBorrowResponse returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId).orElseThrow(() -> new ResourceNotFoundException("Borrowing", " id", borrowingId));

        checkOwnership(borrowing);

        if (borrowing.getReturnDate() != null){
            throw new ConflictException("Book has already been returned");
        }
        borrowing.setReturnDate(new Date());
        borrowingRepository.save(borrowing);

        Book book = bookRepository.findById(borrowing.getBook().getId()).orElseThrow(() -> new ResourceNotFoundException("Book", " id", borrowing.getBook().getId()));
        book.setSituation(Situation.AVAILABLE);
        bookRepository.save(book);

        DtoBorrowResponse dtoBorrowResponse = borrowingToDtoBorrowResponse(borrowing);

        if (borrowing.getReturnDate().after(borrowing.getLastReturnDate())){
            dtoBorrowResponse.setPenaltyCost(createPenalty(borrowing).getAmount());
        }

        return dtoBorrowResponse;
    }

    private Penalty createPenalty(Borrowing borrowing){
        long days = ChronoUnit.DAYS.between(
                borrowing.getLastReturnDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                borrowing.getReturnDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        );

        Double cost = days * penaltyCostPerDay;
        Penalty penalty = new Penalty(borrowing.getId(), borrowing.getUser().getId(),cost, StateOfPenalty.UNPAID);
        penalty.setCreateTime(new Date());
        return penaltyRepository.save(penalty);
    }

    private void checkOwnership(Borrowing borrowing) {      //TODO: look at this exceptions
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.warn("Authentication: {}", auth.toString());
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Access Denied.");
        }

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return;
        }

        Long currentUserId = Long.parseLong(auth.getPrincipal().toString());
        if (!borrowing.getUser().getId().equals(currentUserId)) {
            log.warn("buraya girmemesi lazımdı: currentUserId: {}, borrowingUserId: {}", currentUserId, borrowing.getUser().getId());
            throw new AccessDeniedException("Acces Denied. You dont have access this entity.");
        }
    }
}
