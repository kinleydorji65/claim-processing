package com.claim.claim_processing.common.repository.refund_master;

import com.claim.claim_processing.common.entities.refund_master.ExcessRefundReasonMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExcessRefundReasonRepository extends JpaRepository<ExcessRefundReasonMaster, Long> {

    boolean existsByCode(String code);

    Optional<ExcessRefundReasonMaster> findByCode(String code);

    List<ExcessRefundReasonMaster> findByIsActive(Character isActive);

    Optional<ExcessRefundReasonMaster> findByCodeAndIsActive(String code, Character isActive);

    List<ExcessRefundReasonMaster> findByIsActiveOrderByDisplayOrderAscNameAsc(Character isActive);

    Optional<ExcessRefundReasonMaster> findByIdAndIsActive(Long id, Character isActive);
}