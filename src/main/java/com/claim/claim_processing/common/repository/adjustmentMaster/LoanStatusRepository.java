package com.claim.claim_processing.common.repository.adjustmentMaster;

import com.claim.claim_processing.common.entities.adjustmentMaster.LoanStatusMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanStatusRepository extends JpaRepository<LoanStatusMaster, Long> {

    // 🔹 Find by unique code
    Optional<LoanStatusMaster> findByCode(String code);

    // 🔹 Duplicate check (create)
    boolean existsByCode(String code);

    // 🔹 Duplicate check (update)
    boolean existsByCodeAndIdNot(String code, Long id);

    // 🔹 Fetch active records
    List<LoanStatusMaster> findByIsActive(ActivityEnum isActive);
}