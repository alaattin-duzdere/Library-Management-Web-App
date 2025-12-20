package com.example.library_management.penalties.controller.impl;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.common.util.SecurityUtils;
import com.example.library_management.penalties.controller.IPenaltyController;
import com.example.library_management.penalties.dto.DtoPayPenaltyRequest;
import com.example.library_management.penalties.dto.DtoPenaltyResponse;
import com.example.library_management.penalties.model.StateOfPenalty;
import com.example.library_management.penalties.service.IPenaltyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PenaltyControllerImpl implements IPenaltyController {

    private final IPenaltyService penaltyService;

    public PenaltyControllerImpl(IPenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }

    @GetMapping("/api/penalties/me")
    @Override
    public ResponseEntity<CustomResponseBody<Page<DtoPenaltyResponse>>> getMyPenalties(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) StateOfPenalty state) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<DtoPenaltyResponse> results = penaltyService.getPenalties(pageable, userId, state);
        CustomResponseBody<Page<DtoPenaltyResponse>> body = CustomResponseBody.ok(results, "Penalties retrieved successfully");
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/api/admin/penalties")
    @Override
    public ResponseEntity<CustomResponseBody<Page<DtoPenaltyResponse>>> getPenalties(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "state", required = false) StateOfPenalty state
    ) {
        Page<DtoPenaltyResponse> results = penaltyService.getPenalties(pageable, userId, state);
        CustomResponseBody<Page<DtoPenaltyResponse>> body = CustomResponseBody.ok(results, "Penalties retrieved successfully");
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/api/penalties/pay")
    @Override
    public ResponseEntity<CustomResponseBody<DtoPenaltyResponse>> payPenalty(@RequestBody DtoPayPenaltyRequest payPenaltyRequest) {
        CustomResponseBody<DtoPenaltyResponse> body = CustomResponseBody.ok(penaltyService.payPenalty(payPenaltyRequest.getPenaltyId(),payPenaltyRequest.getAmount()), "Penalty paid successfully");
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
