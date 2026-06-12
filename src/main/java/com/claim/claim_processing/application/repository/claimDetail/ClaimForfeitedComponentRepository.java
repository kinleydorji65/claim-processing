package com.claim.claim_processing.application.repository.claimDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.claimDetail.ClaimForfeitedComponent;

@Repository
public interface ClaimForfeitedComponentRepository extends JpaRepository<ClaimForfeitedComponent, Long> {
    
}
