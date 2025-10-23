package com.example.library_management.penalties.dto;

import com.example.library_management.penalties.model.StateOfPenalty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoPenaltyResponse {

    private Long penaltyId;

    private Long borrowingId;

    private Long userId;

    private Double amount;

    private StateOfPenalty stateOfPenalty;
}
