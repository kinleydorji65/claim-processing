package com.claim.claim_processing.common.repository.beneficiary;

import com.claim.claim_processing.common.entities.beneficiary_master.ClaimantTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimantTypeRepository extends JpaRepository<ClaimantTypeMaster, Long> {

    Optional<ClaimantTypeMaster> findByCode(String code);
    boolean existsByCode(String code);
    List<ClaimantTypeMaster> findByIsActiveOrderByDisplayOrderAsc(String isActive);
    Optional<ClaimantTypeMaster> findByCodeAndIsActive(String code, String isActive);
}