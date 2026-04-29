package com.claim.claim_processing.common.repository.calculationMaster;

import com.claim.claim_processing.common.entities.calculationMaster.CalculationStageMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalculationStageMasterRepository extends JpaRepository<CalculationStageMaster, Long> {

    Optional<CalculationStageMaster> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<CalculationStageMaster> findByIsActive(ActivityEnum isActive);
}