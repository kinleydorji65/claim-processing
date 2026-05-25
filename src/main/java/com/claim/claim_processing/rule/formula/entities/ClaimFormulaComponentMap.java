package com.claim.claim_processing.rule.formula.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleComponentMap;

@Entity
@Table(name = "CLAIM_FORMULA_COMPONENT_MAP", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimFormulaComponentMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FORMULA_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_FORMULA_COMP_FORMULA"))
    private ClaimFormulaMaster formula;

    @Column(name = "VARIABLE_CODE", nullable = false, length = 100)
    private String variableCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_COMPONENT_MAP_ID", foreignKey = @ForeignKey(name = "FK_FORMULA_COMP_RULE_COMP"))
    private ClaimRuleComponentMap ruleComponentMap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPONENT_ID")
    private ComponentMaster component;

    @Column(name = "SOURCE_TYPE", nullable = false, length = 50)
    private String sourceType;

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
