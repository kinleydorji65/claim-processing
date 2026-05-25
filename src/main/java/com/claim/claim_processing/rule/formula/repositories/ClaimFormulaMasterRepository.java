package com.claim.claim_processing.rule.formula.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.formula.entities.ClaimFormulaMaster;

@Repository
public interface ClaimFormulaMasterRepository
                extends JpaRepository<ClaimFormulaMaster, Long> {

}
