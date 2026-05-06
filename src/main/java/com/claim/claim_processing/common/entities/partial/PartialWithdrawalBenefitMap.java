package com.claim.claim_processing.common.entities.partial;

import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PARTIAL_WITHDRAWAL_BENEFIT_MAP", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalBenefitMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -----------------------------
    // FK: ACCUMULATION_ID
    // -----------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCUMULATION_ID", referencedColumnName = "ID",
    foreignKey = @ForeignKey(name = "FK_PARTIAL_WITHDRAWAL_ACCUMULATION"))

    private PartialWithdrawalAccumulationMaster accumulation;

    // -----------------------------
    // FK: BENEFIT_COMPONENT_ID
    // -----------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BENEFIT_COMPONENT_ID", referencedColumnName = "ID",
    foreignKey = @ForeignKey(name = "FK_COMPONENT_BENEFIT_RULE_MAP_RULE"))

    private BenefitComponentTypeMaster benefitComponent;

}