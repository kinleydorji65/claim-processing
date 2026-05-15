package com.claim.claim_processing.common.repository.adjustmentMaster;

import com.claim.claim_processing.common.entities.adjustmentMaster.LoanAdjustmentPriorityMaster;
import com.claim.claim_processing.common.entities.adjustmentMaster.LoanTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanAdjustmentPriorityRepository
        extends JpaRepository<LoanAdjustmentPriorityMaster, Long> {

    // 🔹 Unique check (1 priority config per loan type)
    boolean existsByLoanType(LoanTypeMaster loanType);

    // 🔹 Unique check for update
    boolean existsByLoanTypeAndIdNot(LoanTypeMaster loanType, Long id);

    // 🔹 Active records
    List<LoanAdjustmentPriorityMaster> findByIsActive(ActivityEnum isActive);

    // 🔹 Find by FK
    List<LoanAdjustmentPriorityMaster> findByLoanType(LoanTypeMaster loanType);

    List<LoanAdjustmentPriorityMaster> findByLoanTypeAndIsActive(
            LoanTypeMaster loanType,
            ActivityEnum isActive
    );
}