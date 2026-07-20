package com.claim.claim_processing.application.entity.claimDetail;

import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_SPECIAL_CASE_COMPONENT_DETAIL", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ClaimSpecialCaseComponentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIAL_CASE_ID", nullable = false)
    private ClaimSpecialCase specialCase;

    @Column(name = "COMPONENT_CODE", length = 50, nullable = false)
    private String componentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPONENT_MASTER_ID")
    private ComponentMaster componentMaster;

    @Column(name = "COMPONENT_NAME", length = 100)
    private String componentName;

    @Column(name = "AMOUNT", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "COMPONENT_TYPE", length = 20)
    private String componentType; // ELIGIBLE, FORFEITED, DEDUCTION, INTEREST

    @Column(name = "PERCENTAGE_AMOUNT", precision = 18, scale = 2)
    private BigDecimal percentageAmount;

    @Column(name = "NOTES", length = 500)
    private String notes;

    @Column(name = "SUB_RULE_CODE", length = 50)
    private String subRuleCode;

    @Column(name = "RULE_CODE", length = 50)
    private String ruleCode;

    @Column(name = "REASON", length = 1000)
    private String reason;

    @Column(name = "IS_ACTIVE", length = 1)
    private String isActive;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (isActive == null) {
            isActive = "Y";
        }
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        if (percentageAmount == null) {
            percentageAmount = BigDecimal.ZERO;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper Methods
    public void activate(String activatedBy) {
        this.isActive = "Y";
        this.updatedBy = activatedBy;
    }

    public void deactivate(String deactivatedBy) {
        this.isActive = "N";
        this.updatedBy = deactivatedBy;
    }

    public boolean isActive() {
        return "Y".equalsIgnoreCase(isActive);
    }

    public boolean isEligible() {
        return "ELIGIBLE".equalsIgnoreCase(componentType);
    }

    public boolean isForfeited() {
        return "FORFEITED".equalsIgnoreCase(componentType);
    }

    public boolean isDeduction() {
        return "DEDUCTION".equalsIgnoreCase(componentType);
    }

    public boolean isInterest() {
        return "INTEREST".equalsIgnoreCase(componentType);
    }

    /**
     * Calculate total amount (amount + percentageAmount if any)
     */
    public BigDecimal getTotalAmount() {
        BigDecimal total = amount != null ? amount : BigDecimal.ZERO;
        if (percentageAmount != null) {
            total = total.add(percentageAmount);
        }
        return total;
    }

    /**
     * Set component as ELIGIBLE
     */
    public void markAsEligible(String updatedBy) {
        this.componentType = "ELIGIBLE";
        this.updatedBy = updatedBy;
    }

    /**
     * Set component as FORFEITED
     */
    public void markAsForfeited(String updatedBy) {
        this.componentType = "FORFEITED";
        this.updatedBy = updatedBy;
    }

    /**
     * Set component as DEDUCTION
     */
    public void markAsDeduction(String updatedBy) {
        this.componentType = "DEDUCTION";
        this.updatedBy = updatedBy;
    }

    /**
     * Set component as INTEREST
     */
    public void markAsInterest(String updatedBy) {
        this.componentType = "INTEREST";
        this.updatedBy = updatedBy;
    }
}
