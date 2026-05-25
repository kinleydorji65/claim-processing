package com.claim.claim_processing.document.repository;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.document.entity.DocumentTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTypeMasterRepository extends JpaRepository<DocumentTypeMaster, Long> {

    Optional<DocumentTypeMaster> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<DocumentTypeMaster> findByIsActive(ActivityEnum isActive);

    List<DocumentTypeMaster> findByNameContainingIgnoreCase(String name);

    Optional<DocumentTypeMaster> findByCodeAndIsActive(
            String code,
            ActivityEnum isActive
    );
}
