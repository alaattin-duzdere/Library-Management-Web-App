package com.example.library_management.penalties.model;

import com.example.library_management.common.model.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Penalty extends BaseEntity {

    private Long borrowingId;

    private Long userId;

    private Double amount;

    private StateOfPenalty stateOfPenalty;

}
