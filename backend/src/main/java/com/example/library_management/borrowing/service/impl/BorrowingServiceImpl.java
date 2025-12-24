package com.example.library_management.borrowing.service.impl;

import com.example.library_management.book.model.Book;
import com.example.library_management.book.repository.BookRepository;
import com.example.library_management.borrowing.mapper.BorrowingMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class BorrowingServiceImpl implements IBorrowingService {

    @Value("${durationDay:#{null}}")
    private Long durationDay;

    @Value("${durationSeconds:#{null}}")
    private Long durationSeconds;

    @Value("${penaltyCostPerDay}")
    private Double penaltyCostPerDay;

    private final BorrowingRepository borrowingRepository;

    private final BorrowingMapper borrowingMapper;

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    private final PenaltyRepository penaltyRepository;

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository, BorrowingMapper borrowingMapper, BookRepository bookRepository, UserRepository userRepository, PenaltyRepository penaltyRepository) {
        this.borrowingRepository = borrowingRepository;
        this.borrowingMapper = borrowingMapper;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.penaltyRepository = penaltyRepository;
    }

    // This
    private long durationInMillis(){
        if (durationSeconds != null) {
            return durationSeconds * 1000;
        } else if (durationDay != null) {
            return durationDay * 24 * 60 * 60 * 1000;
        } else {
            // Default to 14 days if neither is set
            return 14L * 24 * 60 * 60 * 1000;
        }
    }

    @Override
    @Transactional
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
                .lastReturnDate(new Date(System.currentTimeMillis() + durationInMillis()))
                .build();
        borrowing.setCreateTime(new Date());

        book.setSituation(Situation.BORROWED);
        bookRepository.save(book);

        return borrowingMapper.borrowingToDtoBorrowResponse(borrowingRepository.save(borrowing));
    }

    @Transactional(readOnly=true)
    @Override
    public Page<DtoBorrowResponse> getBorrowings(Pageable pageable, Long borrowingId, Long userId, Long bookId) {
        Specification<Borrowing> spec = BorrowingSpecification.findByCriteria(borrowingId, userId, bookId);
        return borrowingRepository.findAll(spec, pageable).map(borrowingMapper::borrowingToDtoBorrowResponse);
    }

    @Transactional
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

        DtoBorrowResponse dtoBorrowResponse = borrowingMapper.borrowingToDtoBorrowResponse(borrowing);

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
