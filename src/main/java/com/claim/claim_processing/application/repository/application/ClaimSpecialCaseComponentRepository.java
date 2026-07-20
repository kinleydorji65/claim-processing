package com.claim.claim_processing.application.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseComponent;

public interface ClaimSpecialCaseComponentRepository extends JpaRepository<ClaimSpecialCaseComponent, Long> {
    
}
