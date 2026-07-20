package com.claim.claim_processing.rule.ruleProcessing.entities.rule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SUB_CLAIM_CONDITION", schema = "PPFMS_MASTER_SERVICE_SCHEMA", uniqueConstraints = {
                @UniqueConstraint(name = "UK_SUB_CLAIM_CONDITION_CODE", columnNames = "CONDITION_CODE")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubClaimCondition {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @Column(name = "CONDITION_CODE", nullable = false, length = 100)
        private String conditionCode;

        @Column(name = "CONDITION_CHECK", nullable = false, length = 150)
        private String conditionCheck;

        @Column(name = "IS_ACTIVE", nullable = true, length = 150)
        private String isActive;

        @Column(name = "EXPRESSION", length = 500)
        private String expression;

        @Column(name = "DURATION")
        private Long duration;

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

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SUB_CLAIM_CODE", referencedColumnName = "SUB_CLAIM_CODE", nullable = false)
        private SubClaimMapping subClaimMapping;

        @PreUpdate
        public void preUpdate() {
                this.updatedAt = LocalDateTime.now();
        }
}
