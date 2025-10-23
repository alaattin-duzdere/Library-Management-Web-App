package com.example.library_management.penalties.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.penalties.dto.DtoPayPenaltyRequest;
import com.example.library_management.penalties.dto.DtoPenaltyResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPenaltyController {
    ResponseEntity<CustomResponseBody<List<DtoPenaltyResponse>>> getUserPenalties();

    ResponseEntity<CustomResponseBody<List<DtoPenaltyResponse>>> getAllPenalties();

    ResponseEntity<CustomResponseBody<DtoPenaltyResponse>> payPenalty(DtoPayPenaltyRequest payRequest);
}
