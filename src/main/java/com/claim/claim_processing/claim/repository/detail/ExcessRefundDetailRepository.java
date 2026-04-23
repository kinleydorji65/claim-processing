package com.claim.claim_processing.claim.repository.detail;

import com.claim.claim_processing.claim.entity.detail.ExcessRefundDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcessRefundDetailRepository extends JpaRepository<ExcessRefundDetail, Long> {
}