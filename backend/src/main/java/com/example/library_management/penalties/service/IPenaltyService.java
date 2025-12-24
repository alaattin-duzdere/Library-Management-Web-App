package com.example.library_management.penalties.service;


import com.example.library_management.penalties.dto.DtoPenaltyResponse;
import com.example.library_management.penalties.model.StateOfPenalty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPenaltyService{

    Page<DtoPenaltyResponse> getPenalties(Pageable pageable, Long userId, StateOfPenalty state);

    DtoPenaltyResponse payPenalty(Long penaltyId, Double amount);
}
