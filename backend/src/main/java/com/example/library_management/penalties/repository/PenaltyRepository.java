package com.example.library_management.penalties.repository;

import com.example.library_management.penalties.model.Penalty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty,Long>, JpaSpecificationExecutor<Penalty> {

    Page<Penalty> findByUserId(Long userId, Pageable pageable);
}
