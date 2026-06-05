package com.claim.claim_processing.rule.ruleProcessing.entities.rule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.adjustmentMaster.LoanTypeMaster;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;

@Entity
@Table(name = "LOAN_DEDUCTION_MAPPING", schema = "PPFMS_MASTER_SERVICE_SCHEMA", uniqueConstraints = {
                @UniqueConstraint(name = "UK_LOAN_DEDUCTION_MAPPING", columnNames = { "RULE_TYPE_ID", "LOAN_TYPE_ID",
                                "EFFECTIVE_FROM" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDeductionMapping {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "RULE_TYPE_ID", referencedColumnName = "ID", nullable = false)
        private RuleTypeMaster ruleType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "LOAN_TYPE_ID", referencedColumnName = "ID", nullable = false)
        private LoanTypeMaster loanType;

        @Column(name = "PRIORITY_ORDER")
        private Integer priorityOrder;

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

        @PreUpdate
        public void preUpdate() {
                this.updatedAt = LocalDateTime.now();
        }
}
