package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.DecisionMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DecisionRepository extends JpaRepository<DecisionMaster, Long> {

    Optional<DecisionMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<DecisionMaster> findAllByIsActive(ActivityEnum isActive);
}