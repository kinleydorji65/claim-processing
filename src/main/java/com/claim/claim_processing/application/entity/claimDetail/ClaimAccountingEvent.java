package com.claim.claim_processing.application.entity.claimDetail;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CLAIM_ACCOUNTING_EVENT", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimAccountingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EVENT_TYPE", nullable = false)
    private String eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_DETAIL_ID", insertable = false, updatable = false)
    private ClaimDetail claimDetail;

    // Member Information
    @Column(name = "NPPF_NUMBER")
    private String nppfNumber;

    @Column(name = "IDENTITY_NUMBER")
    private String identityNumber;

    @Column(name = "MEMBER_NAME")
    private String memberName;

    // Agency Information
    @Column(name = "AGENCY_CATEGORY_ID")
    private String agencyCategoryId;

    @Column(name = "AGENCY_CODE")
    private String agencyCode;

    @Column(name = "AGENCY_NAME")
    private String agencyName;

    // Claim Information
    @Column(name = "CLAIM_TYPE_ID")
    private Long claimTypeId;

    @Column(name = "CLAIM_APPLICATION_NUMBER")
    private String claimApplicationNumber;

    // Period Information
    @Column(name = "MONTH_NAME")
    private String monthName;

    @Column(name = "YEAR")
    private String year;

    @Column(name = "ACCOUNTING_YEAR")
    private String accountingYear;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "NARRATION")
    private String narration;

    // Audit Information
    @Column(name = "POSTED_BY")
    private String postedBy;

    @Column(name = "POSTED_AT")
    private LocalDateTime postedAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

     @OneToMany(mappedBy = "accountingEvent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<ClaimLedgerEntry> ledgerEntries = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
