package com.claim.claim_processing.common.repository.partial;

import com.claim.claim_processing.common.entities.partial.PartialWithdrawalCauseMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartialCauseRepository extends JpaRepository<PartialWithdrawalCauseMaster, Long> {

    // Duplicate check
    boolean existsByCode(String code);

    // Fetch by code
    Optional<PartialWithdrawalCauseMaster> findByCode(String code);

    // Active records
    List<PartialWithdrawalCauseMaster> findByIsActive(String isActive);

    // Active + by code
    Optional<PartialWithdrawalCauseMaster> findByCodeAndIsActive(String code, String isActive);

    // Sorted active list (for dropdown)
    List<PartialWithdrawalCauseMaster> findByIsActiveOrderByNameAsc(String isActive);
}