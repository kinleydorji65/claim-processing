package com.claim.claim_processing.common.repository.contribution;

import java.util.List;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeDetail;

public interface BenefitComponentTypeDetailRepository extends JpaRepository<BenefitComponentTypeDetail, Long> {

    List<BenefitComponentTypeDetail> findByBenefitComponentType_Id(Long benefitComponentTypeId);

    List<BenefitComponentTypeDetail> findByComponent_Id(Long componentId);

    List<BenefitComponentTypeDetail> findByIsActive(ActivityEnum isActive);
}
