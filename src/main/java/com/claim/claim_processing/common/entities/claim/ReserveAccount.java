package com.claim.claim_processing.common.entities.claim;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESERVE_ACCOUNT", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MEMBER_CODE")
    private String memberCode;

    @Column(name = "NPPF_NUMBER")
    private String nppfNumber;

    @Column(name = "IDENTITY_NUMBER", nullable = false)
    private String identityNumber;

    @Column(name = "AGENCY_CATEGORY_ID")
    private String agencyCategoryId;

    @Column(name = "AGENCY_CODE")
    private String agencyCode;

    @Column(name = "RESERVE_TYPE", nullable = false)
    private String reserveType;

    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;

    @Column(name = "FORFEITED_AMOUNT")
    private BigDecimal forfeitedAmount;

    @Column(name = "COMPONENT_CODES")
    private String componentCodes;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "RELEASE_DATE")
    private LocalDateTime releaseDate;

    @Column(name = "RELEASED_BY")
    private String releasedBy;

    @Column(name = "RELEASE_REFERENCE")
    private String releaseReference;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = "Y";
        }
        if (status == null) {
            status = "ACTIVE";
        }
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
        if (forfeitedAmount == null) {
            forfeitedAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper Methods
    public void addForfeitedAmount(BigDecimal amount) {
        this.forfeitedAmount = this.forfeitedAmount.add(amount);
        this.totalAmount = this.totalAmount.add(amount);
    }

    public void releaseAmount(BigDecimal amount, String releasedBy, String reference) {
        this.totalAmount = this.totalAmount.subtract(amount);
        this.releaseDate = LocalDateTime.now();
        this.releasedBy = releasedBy;
        this.releaseReference = reference;
        if (this.totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = "RELEASED";
        } else {
            this.status = "PARTIALLY_RELEASED";
        }
        this.updatedAt = LocalDateTime.now();
    }
}