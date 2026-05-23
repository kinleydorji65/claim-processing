package com.claim.claim_processing.rule.ruleGateWay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.others.RefundTypeMaster;

@Entity
@Table(name = "CLAIM_RULE_REFUND_TYPE_MAP", schema = "PPFMS_MASTER_SERVICE_SCHEMA", uniqueConstraints = {
        @UniqueConstraint(name = "UK_RULE_CATEGORY_REFUND", columnNames = { "RULE_CATEGORY_MAP_ID", "REFUND_TYPE_ID" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRuleRefundTypeMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RULE_CATEGORY_MAP_ID", referencedColumnName = "ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RULE_REFUND_CATEGORY_MAP"))
    private ClaimRuleCategoryMap ruleCategoryMap;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REFUND_TYPE_ID", referencedColumnName = "ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RULE_REFUND_TYPE"))
    private RefundTypeMaster refundType;

    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private String isActive = "Y";

    @Column(name = "IS_ELIGIBLE", length = 1)
    private String isEligible;

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
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}