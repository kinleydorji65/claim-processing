package com.claim.claim_processing.claim.repository.detail;

import com.claim.claim_processing.claim.entity.detail.NormalClaimDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalClaimDetailRepository extends JpaRepository<NormalClaimDetail, Long> {
}