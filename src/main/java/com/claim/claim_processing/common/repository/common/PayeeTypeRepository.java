package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayeeTypeRepository extends JpaRepository<PayeeTypeMaster, Long> {

    Optional<PayeeTypeMaster> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<PayeeTypeMaster> findByIsActive(ActivityEnum isActive);

    Optional<PayeeTypeMaster> findByCodeAndIsActive(String code, ActivityEnum isActive);
}