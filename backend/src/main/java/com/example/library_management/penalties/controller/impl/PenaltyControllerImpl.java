package com.example.library_management.penalties.controller.impl;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.penalties.controller.IPenaltyController;
import com.example.library_management.penalties.dto.DtoPayPenaltyRequest;
import com.example.library_management.penalties.dto.DtoPenaltyResponse;
import com.example.library_management.penalties.service.IPenaltyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class PenaltyControllerImpl implements IPenaltyController {

    private final IPenaltyService penaltyService;

    public PenaltyControllerImpl(IPenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }

    private Long getUserIdFromSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Long.parseLong(securityContext.getAuthentication().getName());
    }

    @GetMapping("/api/penalties/my-penalties")
    @Override
    public ResponseEntity<CustomResponseBody<List<DtoPenaltyResponse>>> getUserPenalties() {
        CustomResponseBody<List<DtoPenaltyResponse>> body = CustomResponseBody.ok(penaltyService.getUserPenalties(getUserIdFromSecurityContext()), "Penalty details retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/penalties")
    @Override
    public ResponseEntity<CustomResponseBody<List<DtoPenaltyResponse>>> getAllPenalties() {
        CustomResponseBody<List<DtoPenaltyResponse>> body = CustomResponseBody.ok(penaltyService.getAllPenalties(), "All penalties retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PostMapping("api/penalties/pay")
    @Override
    public ResponseEntity<CustomResponseBody<DtoPenaltyResponse>> payPenalty(@RequestBody DtoPayPenaltyRequest payPenaltyRequest) {
        CustomResponseBody<DtoPenaltyResponse> body = CustomResponseBody.ok(penaltyService.payPenalty(payPenaltyRequest.getPenaltyId(),payPenaltyRequest.getAmount()), "Penalty paid successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }
}
