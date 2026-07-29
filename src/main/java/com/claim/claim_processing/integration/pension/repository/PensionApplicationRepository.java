package com.claim.claim_processing.integration.pension.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.integration.pension.entity.PensionApplication;

@Repository
public interface PensionApplicationRepository extends JpaRepository<PensionApplication, Long> {

}
