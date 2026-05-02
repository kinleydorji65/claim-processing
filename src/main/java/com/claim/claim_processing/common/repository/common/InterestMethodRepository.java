// Repository
package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.InterestMethodMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestMethodRepository extends JpaRepository<InterestMethodMaster, Long> {

    Optional<InterestMethodMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<InterestMethodMaster> findByIsActive(ActivityEnum isActive);

    Optional<InterestMethodMaster> findByCodeAndIsActive(String code, ActivityEnum isActive);
}