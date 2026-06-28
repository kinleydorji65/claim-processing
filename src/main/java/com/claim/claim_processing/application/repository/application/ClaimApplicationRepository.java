package com.claim.claim_processing.application.repository.application;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimApplicationRepository extends JpaRepository<ClaimApplication, Long> {

    Optional<ClaimApplication> findByApplicationNumber(String applicationNumber);

    boolean existsByApplicationNumber(String applicationNumber);

    List<ClaimApplication> findByMemberCode(String memberCode);

    List<ClaimApplication> findByNppfNumber(String nppfNumber);

    List<ClaimApplication> findByClaimType_Id(Long claimTypeId);

    List<ClaimApplication> findByStatus_StatusId(Long statusId);

    List<ClaimApplication> findByCurrentStage_Id(Long currentStageId);

    List<ClaimApplication> findByIsActive(ActivityEnum isActive);
    List<ClaimApplication> findByAgencyCode(String agencyCode);
    List<ClaimApplication> findByAgencyCodeAndClaimType_Id(String agencyCode, Long claimTypeId);
    List<ClaimApplication> findByAgencyCodeAndStatus_StatusId(String agencyCode, Long statusId);

    List<ClaimApplication> findByMemberCodeAndIsActive(
            String memberCode,
            ActivityEnum isActive
    );

    List<ClaimApplication> findByClaimType_IdAndIsActive(
            Long claimTypeId,
            ActivityEnum isActive
    );
}