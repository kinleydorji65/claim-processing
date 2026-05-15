package com.claim.claim_processing.common.repository.adjustmentMaster;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.common.entities.adjustmentMaster.RentalAdjustmentMaster;

public interface RentalAdjustmentMasterRepository extends JpaRepository<RentalAdjustmentMaster, Long> {
    
}
