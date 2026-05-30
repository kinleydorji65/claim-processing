package com.claim.claim_processing.common.repository.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.SchemeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchemeTypeRepository extends JpaRepository<SchemeType, Long> {

    Optional<SchemeType> findByCode(String code);
    boolean existsByCode(String code);
    List<SchemeType> findByIsActiveOrderByNameAsc(ActivityEnum isActive);
    Optional<SchemeType> findByCodeAndIsActive(String code, ActivityEnum isActive);
    List<SchemeType> findByIsActive(ActivityEnum isActive);
}