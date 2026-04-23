package com.claim.claim_processing.claim.repository.detail;

import com.claim.claim_processing.claim.entity.detail.LegalRecoveryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalRecoveryDetailRepository extends JpaRepository<LegalRecoveryDetail, Long> {
}