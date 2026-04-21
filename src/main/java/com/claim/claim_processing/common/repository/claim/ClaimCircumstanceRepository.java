package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimCircumstanceRepository extends JpaRepository<ClaimCircumstanceMaster, Long> {

    Optional<ClaimCircumstanceMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<ClaimCircumstanceMaster> findByIsActiveOrderByNameAsc(String isActive);

    Optional<ClaimCircumstanceMaster> findByCodeAndIsActive(String code, String isActive);
}