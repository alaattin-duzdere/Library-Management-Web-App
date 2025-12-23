package com.example.library_management.penalties.repository;

import com.example.library_management.penalties.model.Penalty;
import com.example.library_management.penalties.model.StateOfPenalty;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PenaltySpecification {

    public static Specification<Penalty> findByCriteria(Long userId, StateOfPenalty state) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            }
            if (state != null) {
                predicates.add(criteriaBuilder.equal(root.get("stateOfPenalty"), state));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
