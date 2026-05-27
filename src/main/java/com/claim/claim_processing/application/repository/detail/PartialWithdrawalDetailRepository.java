package com.claim.claim_processing.application.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;

@Repository
public interface PartialWithdrawalDetailRepository extends JpaRepository<PartialWithdrawalDetail, Long> {
}