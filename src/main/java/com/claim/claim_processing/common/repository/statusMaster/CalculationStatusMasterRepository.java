package com.claim.claim_processing.common.repository.statusMaster;

import com.claim.claim_processing.common.entities.statusMaster.CalculationStatusMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalculationStatusMasterRepository extends JpaRepository<CalculationStatusMaster, Long> {

    Optional<CalculationStatusMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<CalculationStatusMaster> findByIsActive(ActivityEnum isActive);
}