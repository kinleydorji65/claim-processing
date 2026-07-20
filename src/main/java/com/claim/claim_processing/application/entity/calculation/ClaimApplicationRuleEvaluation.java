package com.claim.claim_processing.application.entity.calculation;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimMapping;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CLAIM_APPLICATION_RULE_EVALUATION", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationRuleEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALCULATION_SUMMARY_ID", nullable = false)
    private ClaimApplicationCalculationSummary calculationSummary;

    // ADD THIS - Direct subRuleCode field
    @Column(name = "SUB_CLAIM_CODE", length = 50)
    private String subRuleCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUB_RULE_ID")
    private SubClaimMapping subRule;

    @Column(name = "IS_RULE_APPLIED", length = 1)
    @Builder.Default
    private ActivityEnum isRuleApplied = ActivityEnum.Y;

    @Column(name = "RESULT_MESSAGE", length = 2000)
    private String resultMessage;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @Column(name = "EVALUATED_BY", length = 100)
    private String evaluatedBy;

    @Column(name = "EVALUATED_AT")
    private Timestamp evaluatedAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "ruleEvaluation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ClaimApplicationCalculationComponent> components = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Helper method to add component
    public void addComponent(ClaimApplicationCalculationComponent component) {
        components.add(component);
        component.setRuleEvaluation(this);
    }

    public void removeComponent(ClaimApplicationCalculationComponent component) {
        components.remove(component);
        component.setRuleEvaluation(null);
    }
}