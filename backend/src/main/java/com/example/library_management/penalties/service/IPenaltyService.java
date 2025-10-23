package com.example.library_management.penalties.service;


import com.example.library_management.penalties.dto.DtoPenaltyResponse;

import java.util.List;

public interface IPenaltyService{

    List<DtoPenaltyResponse> getUserPenalties(Long userId);

    List<DtoPenaltyResponse> getAllPenalties();

    DtoPenaltyResponse payPenalty(Long penaltyId, Double amount);
}
