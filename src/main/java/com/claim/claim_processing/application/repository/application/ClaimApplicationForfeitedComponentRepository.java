package com.claim.claim_processing.application.repository.application;

import com.claim.claim_processing.application.entity.application.ClaimApplicationForfeitedComponent;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimApplicationForfeitedComponentRepository
        extends JpaRepository<ClaimApplicationForfeitedComponent, Long> {

    List<ClaimApplicationForfeitedComponent> findByClaimApplication_Id(Long claimApplicationId);

    List<ClaimApplicationForfeitedComponent> findByClaimApplication_IdAndIsActive(
            Long claimApplicationId,
            ActivityEnum isActive
    );

    boolean existsByClaimApplication_IdAndComponentCodeAndIsActive(
            Long claimApplicationId,
            String componentCode,
            ActivityEnum isActive
    );
}
