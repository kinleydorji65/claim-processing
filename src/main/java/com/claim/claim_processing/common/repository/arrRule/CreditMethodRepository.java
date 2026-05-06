package com.claim.claim_processing.common.repository.arrRule;

import com.claim.claim_processing.common.entities.arrMaster.CreditMethodMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditMethodRepository extends JpaRepository<CreditMethodMaster, Long> {

    Optional<CreditMethodMaster> findByCode(String code);

    List<CreditMethodMaster> findByIsActive(ActivityEnum isActive);

    boolean existsByCode(String code);
}