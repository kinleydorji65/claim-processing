package com.claim.claim_processing.application.entity.calculation;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(
        name = "CLAIM_APPLICATION_CALCULATION_COMPONENT",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
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
    @JoinColumn(
            name = "RULE_EVALUATION_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CACC_CALC_SUMMARY")
    )
    private ClaimApplicationRuleEvaluation ruleEvaluation;

    @Column(name = "COMPONENT_CODE", length = 100)
    private String componentCode;

    @Column(name = "COMPONENT_NAME", length = 300)
    private String componentName;

    @Column(name = "COMPONENT_TYPE", length = 50)
    private String componentType;

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
}