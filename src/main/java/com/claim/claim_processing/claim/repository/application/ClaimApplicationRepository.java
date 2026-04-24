package com.claim.claim_processing.claim.repository.application;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimApplicationRepository extends JpaRepository<ClaimApplication, Long> {
}