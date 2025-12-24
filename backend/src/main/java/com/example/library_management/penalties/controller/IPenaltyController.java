package com.example.library_management.penalties.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.penalties.dto.DtoPayPenaltyRequest;
import com.example.library_management.penalties.dto.DtoPenaltyResponse;
import com.example.library_management.penalties.model.StateOfPenalty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IPenaltyController {
    ResponseEntity<CustomResponseBody<Page<DtoPenaltyResponse>>> getPenalties(Pageable pageable, Long userId, StateOfPenalty state);

    ResponseEntity<CustomResponseBody<Page<DtoPenaltyResponse>>> getMyPenalties(Pageable pageable, StateOfPenalty state);

    ResponseEntity<CustomResponseBody<DtoPenaltyResponse>> payPenalty(DtoPayPenaltyRequest payRequest);
}
