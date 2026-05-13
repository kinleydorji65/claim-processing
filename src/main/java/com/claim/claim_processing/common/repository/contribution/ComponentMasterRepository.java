package com.claim.claim_processing.common.repository.contribution;

import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentMasterRepository extends JpaRepository<ComponentMaster, Long> {

    Optional<ComponentMaster> findByCode(String code);

    List<ComponentMaster> findByIsActive(ActivityEnum isActive);

    boolean existsByCode(String code);
}