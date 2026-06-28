package com.claim.claim_processing.application.entity.claimDetail;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "CLAIM_CALCULATION_COMPONENT", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimCalculationComponent {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "RULE_EVALUATION_ID", nullable = false)
        private ClaimRuleEvaluation ruleEvaluation;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "COMPONENT_CODE", referencedColumnName = "CODE", nullable = false)
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
}