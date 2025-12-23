package com.example.library_management.penalties.mapper;

import com.example.library_management.penalties.dto.DtoPenaltyResponse;
import com.example.library_management.penalties.model.Penalty;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class PenaltyMapper {

    public DtoPenaltyResponse penaltyToDtoPenaltyResponse(Penalty penalty){
        DtoPenaltyResponse dtoPenaltyResponse = new DtoPenaltyResponse();
        BeanUtils.copyProperties(penalty, dtoPenaltyResponse);
        dtoPenaltyResponse.setPenaltyId(penalty.getId());
        return dtoPenaltyResponse;
    }
}
