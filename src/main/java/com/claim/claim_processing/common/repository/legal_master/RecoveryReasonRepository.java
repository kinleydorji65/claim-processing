package com.claim.claim_processing.common.repository.legal_master;

import com.claim.claim_processing.common.entities.legal_master.RecoveryReasonMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecoveryReasonRepository extends JpaRepository<RecoveryReasonMaster, Long> {

    boolean existsByCode(String code);

    Optional<RecoveryReasonMaster> findByCode(String code);

    List<RecoveryReasonMaster> findByIsActive(String isActive);

    Optional<RecoveryReasonMaster> findByCodeAndIsActive(String code, String isActive);

    List<RecoveryReasonMaster> findByIsActiveOrderByDisplayOrderAscNameAsc(String isActive);

    Optional<RecoveryReasonMaster> findByIdAndIsActive(Long id, String isActive);
}