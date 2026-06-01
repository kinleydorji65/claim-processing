package com.claim.claim_processing.common.repository.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.ContributionTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContributionTypeRepository
        extends JpaRepository<ContributionTypeMaster, Long> {

    Optional<ContributionTypeMaster> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<ContributionTypeMaster> findByIsActive(ActivityEnum isActive);
}