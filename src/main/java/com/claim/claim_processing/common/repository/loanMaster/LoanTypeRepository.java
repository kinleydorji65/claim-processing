package com.claim.claim_processing.common.repository.loanMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.loanMaster.LoanTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanTypeRepository extends JpaRepository<LoanTypeMaster, Long> {

    // 🔹 Find by unique code
    Optional<LoanTypeMaster> findByCode(String code);

    // 🔹 Duplicate check (create)
    boolean existsByCode(String code);

    // 🔹 Duplicate check (update)
    boolean existsByCodeAndIdNot(String code, Long id);

    // 🔹 Active records
    List<LoanTypeMaster> findByIsActive(ActivityEnum isActive);
}