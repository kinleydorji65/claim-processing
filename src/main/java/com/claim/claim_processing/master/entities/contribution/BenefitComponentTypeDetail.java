package com.claim.claim_processing.master.entities.contribution;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BENEFIT_COMPONENT_TYPE_DETAIL", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitComponentTypeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // 🔥 Relation to Benefit Component Type
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "BENEFIT_COMPONENT_TYPE_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_BENEFIT_TYPE")
    )
    private BenefitComponentTypeMaster benefitComponentType;

    // 🔥 Relation to Claim Component
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "COMPONENT_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_COMPONENT")
    )
    private ComponentMaster component;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive = "Y";
}