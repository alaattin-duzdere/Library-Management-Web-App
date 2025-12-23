package com.example.library_management.borrowing.repository;

import com.example.library_management.borrowing.model.Borrowing;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BorrowingSpecification {

    public static Specification<Borrowing> findByCriteria(Long borrowingId, Long userId, Long bookId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (borrowingId != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), borrowingId));
            }
            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }
            if (bookId != null) {
                predicates.add(criteriaBuilder.equal(root.get("book").get("id"), bookId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
