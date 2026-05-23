package com.claim.claim_processing.rule.ruleGateWay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_RULE_FORMULA_MAP", schema = "PPFMS_MASTER_SERVICE_SCHEMA", uniqueConstraints = {
        @UniqueConstraint(name = "UK_RULE_CATEGORY_FORMULA", columnNames = { "RULE_CATEGORY_MAP_ID", "FORMULA_ID" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRuleFormulaMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_CATEGORY_MAP_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RULE_FORMULA_CATEGORY_MAP"))
    private ClaimRuleCategoryMap ruleCategoryMap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FORMULA_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RULE_FORMULA_FORMULA"))
    private ClaimFormulaMaster formula;

    @Column(name = "EXECUTION_ORDER")
    @Builder.Default
    private Long executionOrder = 1L;

    @Column(name = "IS_REQUIRED", length = 1)
    @Builder.Default
    private String isRequired = "Y";

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
        if (this.executionOrder == null) {
            this.executionOrder = 1L;
        }
        if (this.isRequired == null) {
            this.isRequired = "Y";
        }
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
