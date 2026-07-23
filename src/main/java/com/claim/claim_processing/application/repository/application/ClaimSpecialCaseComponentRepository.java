package com.claim.claim_processing.application.repository.application;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseComponent;

public interface ClaimSpecialCaseComponentRepository extends JpaRepository<ClaimSpecialCaseComponent, Long> {
    List<ClaimSpecialCaseComponent> findBySpecialCaseApplication_Id(Long specialCaseApplicationId);
}
