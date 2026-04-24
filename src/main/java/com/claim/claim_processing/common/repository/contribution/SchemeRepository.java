package com.claim.claim_processing.common.repository.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchemeRepository extends JpaRepository<SchemeMaster, Long> {

    Optional<SchemeMaster> findByCode(String code);
    boolean existsByCode(String code);
    List<SchemeMaster> findByIsActiveOrderByNameAsc(ActivityEnum isActive);
    Optional<SchemeMaster> findByCodeAndIsActive(String code, ActivityEnum isActive);
}