package com.claim.claim_processing.common.repository.calculationMaster;

import com.claim.claim_processing.common.entities.calculationMaster.CalculationTriggerTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalculationTriggerTypeRepository
        extends JpaRepository<CalculationTriggerTypeMaster, Long> {

    Optional<CalculationTriggerTypeMaster> findByCode(String code);

    List<CalculationTriggerTypeMaster> findByIsActive(ActivityEnum isActive);

    boolean existsByCode(String code);
}