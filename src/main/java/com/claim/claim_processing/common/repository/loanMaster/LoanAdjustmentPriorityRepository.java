package com.claim.claim_processing.common.repository.loanMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.loanMaster.LoanAdjustmentPriorityMaster;
import com.claim.claim_processing.common.entities.loanMaster.LoanTypeMaster;
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