package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewStatusRepository extends JpaRepository<ReviewStatusMaster, Long> {

    Optional<ReviewStatusMaster> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<ReviewStatusMaster> findByIsActive(ActivityEnum isActive);
}