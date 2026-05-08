package com.claim.claim_processing.common.repository.statusMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.PaymentLineStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentLineStatusMasterRepository
        extends JpaRepository<PaymentLineStatusMaster, Long> {

    Optional<PaymentLineStatusMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<PaymentLineStatusMaster> findByIsActive(ActivityEnum isActive);
}