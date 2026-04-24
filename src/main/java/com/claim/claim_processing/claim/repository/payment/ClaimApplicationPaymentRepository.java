package com.claim.claim_processing.claim.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.claim.entity.payment.ClaimApplicationPayment;

@Repository
public interface ClaimApplicationPaymentRepository extends JpaRepository<ClaimApplicationPayment, Long> {
    
}
