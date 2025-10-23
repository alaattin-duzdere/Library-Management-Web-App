package com.example.library_management.penalties.repository;

import com.example.library_management.penalties.model.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty,Long> {

    Optional<Set<Penalty>> findByUserId(Long userId);
}
