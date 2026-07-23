package com.claim.claim_processing.application.entity.claimDetail;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CLAIM_SPECIAL_CASE", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ClaimSpecialCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_DETAIL_ID", nullable = false)
    private ClaimDetail claimDetail;

    @Column(name = "CASE_REASON_ID")
    private Long caseReasonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIAL_CASE_AUTHORITY_ID")
    private SpecialCaseRefundAuthorityMaster specialCaseAuthority;

    // Approval Information
    @Column(name = "APPROVED_BY", length = 100)
    private String approvedBy;

    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;

    @Column(name = "TOTAL_AMOUNT", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    // ✅ Components Relationship
    @OneToMany(mappedBy = "specialCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<ClaimSpecialCaseComponentDetail> componentDetails = new ArrayList<>();

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
        if (componentDetails == null) {
            componentDetails = new ArrayList<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}