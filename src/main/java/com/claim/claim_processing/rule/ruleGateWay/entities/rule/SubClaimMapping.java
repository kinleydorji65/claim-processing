package com.claim.claim_processing.rule.ruleGateWay.entities.rule;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.common.RuleTypeMaster;

@Entity
@Table(name = "SUB_CLAIM_MAPPING", schema = "PPFMS_MASTER_SERVICE_SCHEMA", uniqueConstraints = {
                @UniqueConstraint(name = "UK_SUB_CLAIM_MAPPING_CODE", columnNames = "SUB_CLAIM_CODE")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubClaimMapping {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @Column(name = "SUB_CLAIM_CODE", nullable = false, length = 100)
        private String subClaimCode;

        @Column(name = "SUB_CLAIM_TYPE", nullable = false, length = 150)
        private String subClaimType;

        @Column(name = "SUB_CLAIM_DESC", length = 300)
        private String subClaimDesc;

        @Column(name = "PARTIAL_REASON_ID")
        private Long partialReasonId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "RULE_CODE", referencedColumnName = "CODE", nullable = false)
        private RuleTypeMaster ruleType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TIME_INDICATION_CODE", referencedColumnName = "TIME_INDICATION_CODE", nullable = false)
        private SubClaimTimeIndication timeIndication;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CATEGORY_SCHEME_CODE", referencedColumnName = "CATEGORY_SCHEME_CODE", nullable = false)
        private CategorySchemeMapping categorySchemeMapping;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "COMPONENT_MAPPING_CODE", referencedColumnName = "COMPONENT_MAPPING_CODE", nullable = false)
        private ClaimComponentMapping componentMapping;

        @Column(name = "WITHDRAWAL_PERCENTAGE", precision = 5, scale = 2)
        private BigDecimal withdrawalPercentage;

        @Column(name = "EFFECTIVE_FROM", nullable = false)
        private LocalDate effectiveFrom;

        @Column(name = "EFFECTIVE_TO")
        private LocalDate effectiveTo;

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "UPDATED_AT")
        private LocalDateTime updatedAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

        @Column(name = "REFUND_TYPE_ID", length = 100)
        private Long refundTypeId;
}
