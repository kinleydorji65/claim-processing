package com.claim.claim_processing.common.repository.contribution;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeDetail;

public interface BenefitComponentTypeDetailRepository extends JpaRepository<BenefitComponentTypeDetail, Long> {
    List<BenefitComponentTypeDetail> findByBenefitComponentTypeId(Long benefitComponentTypeId);
}
