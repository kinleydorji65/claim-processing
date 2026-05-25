package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleTypeRepository extends JpaRepository<RuleTypeMaster, Long> {

}