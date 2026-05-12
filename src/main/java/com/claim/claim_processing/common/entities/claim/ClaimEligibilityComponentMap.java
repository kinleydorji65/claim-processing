package com.claim.claim_processing.common.entities.claim;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnumConverter;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLAIM_ELIGIBILITY_COMPONENT_MAP", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimEligibilityComponentMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "RULE_ID",
            referencedColumnName = "ID",
            nullable = false
    )
    private ClaimEligibilityMaster rule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "BENEFIT_COMPONENT_TYPE_ID",
            referencedColumnName = "ID",
            nullable = false
    )
    private BenefitComponentTypeMaster benefitComponentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "CATEGORY_ID",
            referencedColumnName = "ID",
            nullable = false
    )
    private ClaimEligibilityCategoryMap claimEligibilityCategoryMap;

    @Convert(converter = ActivityEnumConverter.class)
    @Column(name = "IS_ACTIVE", length = 1)
    private ActivityEnum isActive;
}