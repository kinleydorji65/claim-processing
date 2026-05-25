package com.claim.claim_processing.common.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.common.entities.common.RuleTypeMaster;

import java.util.List;
import java.util.Optional;

public interface RuleTypeMasterRepository extends JpaRepository<RuleTypeMaster, Long> {

    Optional<RuleTypeMaster> findByCode(String code);
    Optional<RuleTypeMaster> findByIdAndCode(Long id, String code);

    List<RuleTypeMaster> findByIsActive(String isActive);

    boolean existsByCode(String code);
}
