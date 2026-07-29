package com.claim.claim_processing.integration.pension.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PENSION_APPLICATION", schema = "PPFMS_PENSION_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PENSION_APPLICATION_SEQ")
    @SequenceGenerator(
        name = "PENSION_APPLICATION_SEQ",
        sequenceName = "PPFMS_PENSION_SERVICE_SCHEMA.ISEQ$$_100002",
        allocationSize = 1
    )
    @Column(name = "PENSION_APPLICATION_ID", nullable = false)
    private Long pensionApplicationId;

    @Column(name = "APPLICATION_NO", nullable = false, length = 30, unique = true)
    private String applicationNo;

    @Column(name = "PENSION_ID", length = 20, unique = true)
    private String pensionId;

    @Column(name = "MEMBER_ID", nullable = false)
    private Long memberId;

    @Column(name = "MEMBER_NPPF_NO", nullable = false, length = 20)
    private String memberNppfNo;

    @Column(name = "MEMBER_CID_NO", nullable = false, length = 20)
    private String memberCidNo;

    @Column(name = "MEMBER_NAME", nullable = false, length = 200)
    private String memberName;

    @Column(name = "AGENCY_ID", nullable = false)
    private Long agencyId;

    @Column(name = "AGENCY_NAME", length = 200)
    private String agencyName;

    @Column(name = "AGENCY_CATEGORY_CODE", nullable = false, length = 20)
    private String agencyCategoryCode;

    @Column(name = "DATE_OF_BIRTH", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "SERVICE_JOIN_DATE", nullable = false)
    private LocalDate serviceJoinDate;

    @Column(name = "TOTAL_SERVICE_MONTHS_SNAPSHOT")
    private Integer totalServiceMonthsSnapshot;

    @Column(name = "PENSION_TYPE", nullable = false, length = 30)
    private String pensionType;

    @Column(name = "APPLICATION_STATUS", nullable = false, length = 30)
    private String applicationStatus;

    @Column(name = "APPLICATION_DATE", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "EXIT_DATE", nullable = false)
    private LocalDate exitDate;

    @Column(name = "EXIT_REASON", length = 100)
    private String exitReason;

    @Column(name = "FORM_STEP_COMPLETED", nullable = false)
    private Integer formStepCompleted;

    @Column(name = "SUBMITTED_AT")
    private LocalDateTime submittedAt;

    @Column(name = "SUBMITTED_BY", length = 100)
    private String submittedBy;

    @Column(name = "REJECTION_REASON", length = 500)
    private String rejectionReason;

    @Column(name = "BANK_NAME", length = 100)
    private String bankName;

    @Column(name = "BANK_ACCOUNT_NO", length = 50)
    private String bankAccountNo;

    @Column(name = "BANK_ACCOUNT_HOLDER", length = 200)
    private String bankAccountHolder;

    @Column(name = "INITIATION_SOURCE_CODE", length = 30)
    private String initiationSourceCode;

    @Column(name = "CONSENT_CAPTURED", nullable = false, length = 1)
    private String consentCaptured;

    @Column(name = "CONSENT_TYPE", length = 20)
    private String consentType;

    @Column(name = "CONSENT_DATE")
    private LocalDateTime consentDate;

    @Column(name = "CONSENT_REFERENCE", length = 100)
    private String consentReference;

    @Column(name = "EARLY_CONSENT_DMS_REF", length = 100)
    private String earlyConsentDmsRef;

    @Column(name = "IS_EMERGENCY_PROCESSING", nullable = false, length = 1)
    private String isEmergencyProcessing;

    @Column(name = "EMERGENCY_REASON", length = 1000)
    private String emergencyReason;

    @Column(name = "EMERGENCY_AUTHORIZED_BY", length = 100)
    private String emergencyAuthorizedBy;

    @Column(name = "EMERGENCY_AUTHORIZED_AT")
    private LocalDateTime emergencyAuthorizedAt;

    @Column(name = "PF_SETTLEMENT_CLAIM_ID")
    private Long pfSettlementClaimId;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "RETIREMENT_DATE_SNAPSHOT")
    private LocalDate retirementDateSnapshot;

    @Column(name = "MEMBER_RANK_SNAPSHOT", length = 100)
    private String memberRankSnapshot;

    @Column(name = "FINAL_BASIC_SALARY_SNAPSHOT", precision = 12, scale = 2)
    private BigDecimal finalBasicSalarySnapshot;

    @Column(name = "PIS_EVENT_REFERENCE", length = 255)
    private String pisEventReference;

    @Column(name = "MEMBER_EMAIL", length = 100)
    private String memberEmail;

    @Column(name = "MEMBER_CONTACT_NO")
    private Long memberContactNo;

    @Column(name = "MEMBER_CODE", length = 30)
    private String memberCode;
}
