package com.claim.claim_processing.application.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.payment.ClaimApplicationPayment;

@Repository
public interface ClaimApplicationPaymentRepository extends JpaRepository<ClaimApplicationPayment, Long> {
    
}
