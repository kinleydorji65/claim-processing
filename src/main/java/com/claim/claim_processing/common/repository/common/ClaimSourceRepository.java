package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.ClaimSourceMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimSourceRepository extends JpaRepository<ClaimSourceMaster, Long> {

    boolean existsByCode(String code);

    Optional<ClaimSourceMaster> findByCode(String code);

    List<ClaimSourceMaster> findByIsActive(String isActive);

    Optional<ClaimSourceMaster> findByCodeAndIsActive(String code, String isActive);

    List<ClaimSourceMaster> findByIsActiveOrderByNameAsc(String isActive);

    Optional<ClaimSourceMaster> findByIdAndIsActive(Long id, String isActive);
}