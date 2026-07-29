package com.claim.claim_processing.integration.pension.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PENSION_CONTRIBUTION_COMPONENT", schema = "PPFMS_PENSION_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensionContributionComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PENSION_CONTRIBUTION_COMPONENT_SEQ")
    @SequenceGenerator(
        name = "PENSION_CONTRIBUTION_COMPONENT_SEQ",
        sequenceName = "PPFMS_PENSION_SERVICE_SCHEMA.ISEQ$$_102277",
        allocationSize = 1
    )
    @Column(name = "COMPONENT_ID", nullable = false)
    private Long componentId;

    @Column(name = "PENSION_APPLICATION_ID", nullable = false)
    private Long pensionApplicationId;

    @Column(name = "SOURCE_COMPONENT_ID")
    private Long sourceComponentId;

    @Column(name = "COMPONENT_CODE", nullable = false, length = 50)
    private String componentCode;

    @Column(name = "COMPONENT_NAME", length = 200)
    private String componentName;

    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "PF_SETTLEMENT_CLAIM_ID")
    private Long pfSettlementClaimId;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "IS_ACTIVE", length = 100)
    private String isActive;
    // ================================================================
    // RELATIONSHIPS
    // ================================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PENSION_APPLICATION_ID", referencedColumnName = "PENSION_APPLICATION_ID", insertable = false, updatable = false)
    private PensionApplication pensionApplication;
}
