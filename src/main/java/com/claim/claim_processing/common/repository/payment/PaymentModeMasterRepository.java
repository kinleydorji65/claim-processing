package com.claim.claim_processing.common.repository.payment;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.paymentMaster.PaymentModeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentModeMasterRepository extends JpaRepository<PaymentModeMaster, Long> {

    // -------------------------
    // GET BY CODE
    // -------------------------
    Optional<PaymentModeMaster> findByCode(String code);

    // -------------------------
    // CHECK DUPLICATE CODE
    // -------------------------
    boolean existsByCode(String code);

    // -------------------------
    // GET BY NAME
    // -------------------------
    Optional<PaymentModeMaster> findByName(String name);

    // -------------------------
    // CHECK DUPLICATE NAME
    // -------------------------
    boolean existsByName(String name);

    // -------------------------
    // GET ACTIVE RECORDS
    // -------------------------
    List<PaymentModeMaster> findByIsActive(ActivityEnum isActive);
}