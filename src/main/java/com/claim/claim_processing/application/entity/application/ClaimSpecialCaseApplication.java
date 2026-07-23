package com.claim.claim_processing.application.entity.application;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundReasonMaster;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_SPECIAL_CASE_APPLICATION", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSpecialCaseApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // Reference to the claim
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, unique = true)
    private ClaimApplication claimApplication;

    // ADD THIS RELATIONSHIP
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CASE_REASON_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SpecialCaseRefundReasonMaster specialCaseRefundReasonMaster;

    // Approval Information
    @Column(name = "APPROVED_BY", length = 100)
    private String approvedBy;

    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;

    @Column(name = "TOTAL_AMOUNT", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIAL_CASE_AUTHORITY_ID")
    private SpecialCaseRefundAuthorityMaster specialCaseAuthority;

    // Audit Information
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
        
    }

    // Helper Methods
    public void approve(String approvedBy, String reference) {
        this.approvedBy = approvedBy;
        this.approvedDate = LocalDateTime.now();
        this.updatedBy = approvedBy;
    }

    public void process(String processedBy) {
        this.updatedBy = processedBy;
    }

    /**
     * Check if application is active
     */
    public boolean isActive() {
        return "Y".equalsIgnoreCase(isActive);
    }

    /**
     * Deactivate the application
     */
    public void deactivate(String deactivatedBy) {
        this.isActive = "N";
        this.updatedBy = deactivatedBy;
    }
}