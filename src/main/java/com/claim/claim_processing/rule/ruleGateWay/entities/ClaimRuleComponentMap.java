package com.claim.claim_processing.rule.ruleGateWay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.contribution.ComponentMaster;

@Entity
@Table(name = "CLAIM_RULE_COMPONENT_MAP", schema = "PPFMS_MASTER_SERVICE_SCHEMA", uniqueConstraints = {
                @UniqueConstraint(name = "UK_RULE_CATEGORY_COMPONENT", columnNames = { "RULE_CATEGORY_MAP_ID",
                                "COMPONENT_ID" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRuleComponentMap {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "RULE_CATEGORY_MAP_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RULE_COMP_RULE_CATEGORY"))
        private ClaimRuleCategoryMap ruleCategoryMap;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "COMPONENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RULE_COMP_COMPONENT"))
        private ComponentMaster component;

        @Column(name = "IS_ACTIVE", length = 1)
        @Builder.Default
        private String isActive = "Y";

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "UPDATED_AT")
        private LocalDateTime updatedAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

        @PrePersist
        public void prePersist() {
                if (isActive == null) {
                        isActive = "Y";
                }
                updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        public void preUpdate() {
                updatedAt = LocalDateTime.now();
        }
}