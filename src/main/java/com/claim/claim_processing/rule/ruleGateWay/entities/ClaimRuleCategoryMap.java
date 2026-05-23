package com.claim.claim_processing.rule.ruleGateWay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;

@Entity
@Table(name = "CLAIM_RULE_CATEGORY_MAP", schema = "PPFMS_MASTER_SERVICE_SCHEMA", uniqueConstraints = {
                @UniqueConstraint(name = "UK_RULE_CONDITION_CATEGORY", columnNames = {
                                "RULE_ID",
                                "CONDITION_ID",
                                "CATEGORY_ID"
                })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRuleCategoryMap {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "RULE_ID", nullable = false)
        private ClaimRuleMaster rule;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CONDITION_ID", nullable = false)
        private ClaimRuleCondition condition;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = false)
        private AgencyCategory category;

        @Column(name = "IS_ACTIVE", length = 1)
        @Builder.Default
        private String isActive = "Y";

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

        @Column(name = "UPDATED_AT")
        private LocalDateTime updatedAt;

        @PrePersist
        public void prePersist() {
                if (this.isActive == null) {
                        this.isActive = "Y";
                }
                this.updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        public void preUpdate() {
                this.updatedAt = LocalDateTime.now();
        }
}