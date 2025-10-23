package com.example.library_management.penalties.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class DtoPayPenaltyRequest {
    @NotNull
    private Long penaltyId;

    @NotNull
    @Positive(message = "isbn must be a positive number")
    private Double amount;
}
