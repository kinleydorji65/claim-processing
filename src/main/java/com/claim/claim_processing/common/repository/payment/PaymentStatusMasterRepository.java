package com.claim.claim_processing.common.repository.payment;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.paymentMaster.PaymentStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentStatusMasterRepository extends JpaRepository<PaymentStatusMaster, Long> {

    Optional<PaymentStatusMaster> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<PaymentStatusMaster> findByIsActive(ActivityEnum isActive);
}