package com.claim.claim_processing.common.repository.partial;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.UnemploymentCauseMaster;

@Repository
public interface UnemploymentCauseMasterRepository
        extends JpaRepository<UnemploymentCauseMaster, Long> {

    Optional<UnemploymentCauseMaster> findByCode(String code);

    List<UnemploymentCauseMaster> findByIsActive(ActivityEnum isActive);

    boolean existsByCode(String code);
}
