package com.claim.claim_processing.rule.ruleProcessing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartialWithdrawalReasonRepository
        extends JpaRepository<PartialWithdrawalReasonMaster, Long> {

    Optional<PartialWithdrawalReasonMaster> findByCode(String code);

    List<PartialWithdrawalReasonMaster> findByIsActive(String isActive);

    boolean existsByCode(String code);

}