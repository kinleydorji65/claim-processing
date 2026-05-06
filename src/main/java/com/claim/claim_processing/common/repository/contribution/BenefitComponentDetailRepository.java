package com.claim.claim_processing.common.repository.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenefitComponentDetailRepository extends JpaRepository<BenefitComponentTypeDetail, Long> {

    List<BenefitComponentTypeDetail> findByBenefitComponentType_Id(Long benefitComponentTypeId);

    List<BenefitComponentTypeDetail> findByComponent_Id(Long componentId);

    List<BenefitComponentTypeDetail> findByIsActive(ActivityEnum isActive);
}