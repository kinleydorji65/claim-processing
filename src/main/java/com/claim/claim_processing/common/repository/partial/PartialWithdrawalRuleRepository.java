package com.claim.claim_processing.common.repository.partial;

import com.claim.claim_processing.common.entities.partial.PartialWithdrawalRuleMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartialWithdrawalRuleRepository
        extends JpaRepository<PartialWithdrawalRuleMaster, Long> {
    Optional<PartialWithdrawalRuleMaster> findById(Long id);
    List<PartialWithdrawalRuleMaster> findAll();
    List<PartialWithdrawalRuleMaster> findByCategory_CategoryId(String categoryId);
    List<PartialWithdrawalRuleMaster> findByReason_Id(Long reasonId);
    List<PartialWithdrawalRuleMaster> findByIsActive(ActivityEnum isActive);
}