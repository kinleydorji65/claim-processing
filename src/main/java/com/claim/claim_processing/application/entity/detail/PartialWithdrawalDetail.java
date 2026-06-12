package com.claim.claim_processing.application.entity.detail;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.partial.BusinessTypeMaster;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import com.claim.claim_processing.common.entities.partial.UnemploymentCauseMaster;

@Entity
@Table(
        name = "PARTIAL_WITHDRAWAL_DETAIL",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * One claim application has one partial withdrawal detail
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, unique = true)
    private ClaimApplication claimApplication;

    @OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "CLAIM_DETAIL_ID", nullable = false, unique = true)
private ClaimDetail claimDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYEE_TYPE_ID", nullable = false)
    private PayeeTypeMaster payeeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WITHDRAWAL_REASON_ID", nullable = false)
    private PartialWithdrawalReasonMaster withdrawalReason;

    @Column(name = "REQUESTED_WITHDRAWAL_AMOUNT", precision = 15, scale = 2)
    private BigDecimal requestedWithdrawalAmount;

    @Column(name = "ACTUAL_WITHDRAWAL_AMOUNT", precision = 15, scale = 2)
    private BigDecimal actualWithdrawalAmount;

    /**
     * Unemployment details
     */
    @Column(name = "UNEMPLOYMENT_START_DATE")
    private LocalDate unemploymentStartDate;

    /**
     * Disability details
     */
    @Column(name = "DISABILITY_DATE")
    private LocalDate disabilityDate;

    /**
     * Disaster details
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNEMPLOYMENT_CAUSE_ID")
    private UnemploymentCauseMaster unemploymentCauseMaster;

    @Column(name = "INCIDENT_DATE")
    private LocalDate incidentDate;

    @Column(name = "PLACE_OF_INCIDENT", length = 255)
    private String placeOfIncident;

    /**
     * Business details
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUSINESS_TYPE_ID")
    private BusinessTypeMaster businessType;

    @Column(name = "BUSINESS_NAME", length = 255)
    private String businessName;

    @Column(name = "PROPOSED_INVESTMENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal proposedInvestmentAmount;

    /**
     * Housing details
     */
    @Column(name = "HOUSE_PURCHASE_TYPE", length = 100)
    private String housePurchaseType;

    @Column(name = "PROPERTY_LOCATION", length = 255)
    private String propertyLocation;

    @Column(name = "ESTIMATED_COST", precision = 15, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT", insertable = false)
    private Timestamp updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
