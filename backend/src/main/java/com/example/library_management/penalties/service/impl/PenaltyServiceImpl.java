package com.example.library_management.penalties.service.impl;

import com.example.library_management.borrowing.model.Borrowing;
import com.example.library_management.borrowing.repository.BorrowingRepository;
import com.example.library_management.exceptions.client.ConflictException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.exceptions.server.EmailServiceException;
import com.example.library_management.penalties.mapper.PenaltyMapper;
import com.example.library_management.penalties.repository.PenaltySpecification;
import com.example.library_management.penalties.dto.DtoPenaltyResponse;
import com.example.library_management.penalties.model.Penalty;
import com.example.library_management.penalties.model.StateOfPenalty;
import com.example.library_management.penalties.repository.PenaltyRepository;
import com.example.library_management.penalties.service.IPenaltyService;
import com.example.library_management.penalties.service.reminder.IReminderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PenaltyServiceImpl implements IPenaltyService {

    private final PenaltyRepository penaltyRepository;

    private final PenaltyMapper penaltyMapper;

    private final BorrowingRepository borrowingRepository;

    private final IReminderStrategy reminderStrategy;

    public PenaltyServiceImpl(PenaltyRepository penaltyRepository, PenaltyMapper penaltyMapper, BorrowingRepository borrowingRepository, @Qualifier("emailReminder") IReminderStrategy reminderStrategy) {
        this.penaltyRepository = penaltyRepository;
        this.penaltyMapper = penaltyMapper;
        this.borrowingRepository = borrowingRepository;
        this.reminderStrategy = reminderStrategy;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<DtoPenaltyResponse> getPenalties(Pageable pageable, Long userId, StateOfPenalty state) {
        Specification<Penalty> spec = PenaltySpecification.findByCriteria(userId, state);
        return penaltyRepository.findAll(spec, pageable).map(penaltyMapper::penaltyToDtoPenaltyResponse);
    }

    @Transactional
    @Override
    public DtoPenaltyResponse payPenalty(Long penaltyId, Double amount) {
        log.warn("Penalty Id: " +penaltyId);
        Penalty penalty = penaltyRepository.findById(penaltyId).orElseThrow(() -> new ResourceNotFoundException("Penalty", "Penalty ID", penaltyId));
        if (penalty.getStateOfPenalty()== StateOfPenalty.PAID){
            throw new ConflictException("This penalty is already paid.");
        }
        if (amount < penalty.getAmount()){
            throw new ConflictException("The amount paid is less than the penalty amount.");
        }
        penalty.setStateOfPenalty(StateOfPenalty.PAID);
        penaltyRepository.save(penalty);

        return penaltyMapper.penaltyToDtoPenaltyResponse(penalty);
    }

    @Scheduled(cron = "0 0 2 * * ?") // 02:00
    public void processOverdueBorrowings() {
        List<Borrowing> overdueBorrowings = borrowingRepository.findOverdueAndNotReturned();
        log.warn("Found " + overdueBorrowings.size() + " overdue borrowings.");

        for (Borrowing borrowing : overdueBorrowings) {
            try {
                reminderStrategy.sendOverdueReminders(borrowing);
            } catch (Exception e) {
                log.error("Failed to send reminder for borrowing ID {}: {}", borrowing.getId(), e.getMessage());
                throw new EmailServiceException("There is a problem with email sending",e);
            }
        }
    }
}
