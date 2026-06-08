package com.claim.claim_processing.application.repository.payment;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.payment.ClaimApplicationPayment;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimApplicationPaymentRepository extends JpaRepository<ClaimApplicationPayment, Long> {
    List<ClaimApplicationPayment> findByClaimApplication_Id(Long claimApplicationId);

    Optional<ClaimApplicationPayment> findFirstByClaimApplication_IdOrderByIdDesc(
            Long claimApplicationId
    );

    List<ClaimApplicationPayment> findByClaimApplication_IdAndIsActive(
            Long claimApplicationId,
            ActivityEnum isActive
    );

    List<ClaimApplicationPayment> findByPaymentStatus_Id(Long paymentStatusId);

    List<ClaimApplicationPayment> findByPaymentBatchNumber(String paymentBatchNumber);

    Optional<ClaimApplicationPayment> findByPaymentReferenceNumber(
            String paymentReferenceNumber
    );
    boolean existsByPaymentReferenceNumber(String paymentReferenceNumber);
}
