package com.claim.claim_processing.rule.ruleProcessing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.others.RefundTypeMaster;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundTypeRepository
        extends JpaRepository<RefundTypeMaster, Long> {

    Optional<RefundTypeMaster> findByCode(String code);

    List<RefundTypeMaster> findByIsActive(String isActive);

    boolean existsByCode(String code);
}