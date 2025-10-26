package com.example.library_management.penalties.service.impl;

import com.example.library_management.exceptions.client.ConflictException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.penalties.dto.DtoPenaltyResponse;
import com.example.library_management.penalties.model.Penalty;
import com.example.library_management.penalties.model.StateOfPenalty;
import com.example.library_management.penalties.repository.PenaltyRepository;
import com.example.library_management.penalties.service.IPenaltyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class PenaltyServiceImpl implements IPenaltyService {

    private final PenaltyRepository penaltyRepository;

    public PenaltyServiceImpl(PenaltyRepository penaltyRepository) {
        this.penaltyRepository = penaltyRepository;
    }

    private DtoPenaltyResponse penaltyToDtoPenaltyResponse(Penalty penalty){
        DtoPenaltyResponse dtoPenaltyResponse = new DtoPenaltyResponse();
        BeanUtils.copyProperties(penalty, dtoPenaltyResponse);
        dtoPenaltyResponse.setPenaltyId(penalty.getId());
        return dtoPenaltyResponse;
    }

    @PreAuthorize("hasRole('ADMIN') or @customPermissionEvaluator.isOwner(authentication, #userId)")
    @Override
    public List<DtoPenaltyResponse> getUserPenalties(Long userId) {
        Set<Penalty> penalties = penaltyRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Penalty", "User ID", userId));
        if (penalties.isEmpty()){
            throw new ResourceNotFoundException("Penalty", "User ID", userId);
        }
        List<DtoPenaltyResponse> listOfDtoPenalties = penalties.stream()
                .map(this::penaltyToDtoPenaltyResponse)
                .toList();
        log.warn(listOfDtoPenalties.toString());

        return listOfDtoPenalties;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<DtoPenaltyResponse> getAllPenalties() {

        return penaltyRepository.findAll().stream().map(this::penaltyToDtoPenaltyResponse).toList();
    }

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

        return penaltyToDtoPenaltyResponse(penalty);
    }
}
