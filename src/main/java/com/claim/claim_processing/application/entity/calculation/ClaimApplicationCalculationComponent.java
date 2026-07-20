package com.claim.claim_processing.application.entity.calculation;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "CLAIM_APPLICATION_CALCULATION_COMPONENT", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationCalculationComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_EVALUATION_ID", nullable = false)
    private ClaimApplicationRuleEvaluation ruleEvaluation;

    // ADD THIS FIELD - Direct component code for easy access
    @Column(name = "COMPONENT_CODE", length = 50, nullable = false)
    private String componentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPONENT_MASTER_ID", referencedColumnName = "ID", nullable = false)
    private ComponentMaster componentMaster;

    @Column(name = "AMOUNT", precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_DEDUCTION", length = 1)
    @Builder.Default
    private ActivityEnum isDeduction = ActivityEnum.N;

    @Column(name = "NOTES", length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private Timestamp updatedAt;

    @PrePersist
    public void prePersist() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        createdAt = now;
        updatedAt = now;
        // Auto-set componentCode from componentMaster if not set
        if (componentCode == null && componentMaster != null) {
            componentCode = componentMaster.getCode();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
        // Keep componentCode in sync with componentMaster
        if (componentMaster != null && (componentCode == null || !componentCode.equals(componentMaster.getCode()))) {
            componentCode = componentMaster.getCode();
        }
    }
}