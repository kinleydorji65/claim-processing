package com.claim.claim_processing.claim.entity.application;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.claim.TerminationReasonMaster;

@Entity
@Table(
        name = "NORMAL_CLAIM_DETAIL",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NormalClaimDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Parent Claim Application
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false)
    private ClaimApplication claimApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CESSATION_TYPE_ID")
    private CessationTypeMaster cessationType;

    @Column(name = "DATE_OF_TERMINATION")
    private LocalDate dateOfTermination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TERMINATION_REASON_TYPE_ID")
    private TerminationReasonMaster terminationReasonType;

    @Column(name = "TERMINATED_BY", length = 150)
    private String terminatedBy;

    @Column(name = "TERMINATION_REMARKS", length = 1000)
    private String terminationRemarks;

    @Column(name = "RELIEVING_ORDER_NUMBER", length = 100)
    private String relievingOrderNumber;

    @Column(name = "RELIEVING_ORDER_DATE")
    private LocalDate relievingOrderDate;

    @Column(name = "CESSATION_EFFECTIVE_DATE")
    private LocalDate cessationEffectiveDate;

    @Column(name = "EXIT_DATE")
    private LocalDate exitDate;

    @Column(name = "LAST_PAY_CERTIFICATE_NUMBER", length = 100)
    private String lastPayCertificateNumber;

    @Column(name = "LAST_PAY_CERTIFICATE_DATE")
    private LocalDate lastPayCertificateDate;

    @Column(name = "AUDIT_CLEARANCE_NUMBER", length = 100)
    private String auditClearanceNumber;

    @Column(name = "AUDIT_CLEARANCE_DATE")
    private LocalDate auditClearanceDate;

    @Column(name = "DATE_OF_SERVICE_JOINING")
    private LocalDate dateOfServiceJoining;

    @Column(name = "FINAL_BASIC_SALARY")
    private BigDecimal finalBasicSalary;

    @Column(name = "NON_CONTRIBUTION_MONTHS")
    private Integer nonContributionMonths;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
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
