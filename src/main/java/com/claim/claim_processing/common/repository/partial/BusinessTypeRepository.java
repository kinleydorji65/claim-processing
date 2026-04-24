package com.claim.claim_processing.common.repository.partial;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.BusinessTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessTypeRepository extends JpaRepository<BusinessTypeMaster, Long> {

    boolean existsByCode(String code);

    Optional<BusinessTypeMaster> findByCode(String code);

    List<BusinessTypeMaster> findByIsActive(ActivityEnum isActive);

    Optional<BusinessTypeMaster> findByCodeAndIsActive(String code, ActivityEnum isActive);

    List<BusinessTypeMaster> findByIsActiveOrderByDisplayOrderAscNameAsc(ActivityEnum isActive);
}