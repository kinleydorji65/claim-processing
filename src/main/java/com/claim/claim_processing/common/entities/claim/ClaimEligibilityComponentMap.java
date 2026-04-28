package com.claim.claim_processing.common.entities.claim;

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
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_RULE")
    )
    private ClaimEligibilityMaster rule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "BENEFIT_COMPONENT_TYPE_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_BENEFIT_TYPE_MAP")
    )
    private BenefitComponentTypeMaster benefitComponentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "CATEGORY_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CATEGORY_MAP")
    )
    private ClaimEligibilityCategoryMap claimEligibilityCategoryMap;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
        @Builder.Default
    private String isActive = "Y";
}