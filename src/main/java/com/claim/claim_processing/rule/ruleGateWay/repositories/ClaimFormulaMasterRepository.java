package com.claim.claim_processing.rule.ruleGateWay.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimFormulaMaster;

@Repository
public interface ClaimFormulaMasterRepository
                extends JpaRepository<ClaimFormulaMaster, Long> {

}
