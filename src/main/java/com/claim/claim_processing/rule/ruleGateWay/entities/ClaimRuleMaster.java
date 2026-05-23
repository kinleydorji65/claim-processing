package com.claim.claim_processing.rule.ruleGateWay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_RULE_MASTER", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRuleMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RULE_CODE", nullable = false, unique = true, length = 100)
    private String ruleCode;

    @Column(name = "RULE_NAME", nullable = false, length = 255)
    private String ruleName;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "STOP_ON_SUCCESS", length = 1)
    private String stopOnSuccess;

    @Column(name = "EFFECTIVE_FROM")
    private LocalDate effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    private LocalDate effectiveTo;

    @Column(name = "IS_ACTIVE", length = 1)
    private String isActive;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "RULE_TYPE_ID")
    private Long ruleTypeId;

    @Column(name = "LOAN_TYPE_ID")
    private Long loanTypeId;

    @Column(name = "PARTIAL_REASON_ID")
    private Long partialReasonId;

    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = "Y";
        }
        if (this.stopOnSuccess == null) {
            this.stopOnSuccess = "Y";
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
