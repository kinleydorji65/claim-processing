package com.claim.claim_processing.claim.entity.detail;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.claim.AccountTypeMaster;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.contribution.ContributionTypeMaster;
import com.claim.claim_processing.common.entities.wrongRemittanceMaster.WrongRemittanceErrorTypeMaster;
import com.claim.claim_processing.common.entities.wrongRemittanceMaster.WrongRemittanceReasonMaster;

@Entity
@Table(
        name = "WRONG_REMITTANCE_DETAIL",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongRemittanceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Parent claim application
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_APPLICATION_ID",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "FK_WRD_CLAIM_APP")
    )
    private ClaimApplication claimApplication;

    @Column(name = "AGENCY_CODE", length = 100)
    private String agencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRONG_REMITTANCE_REASON_ID")
    private WrongRemittanceReasonMaster wrongRemittanceReason;

    @Column(name = "REMITTANCE_MONTH", precision = 2)
    private Integer remittanceMonth;

    @Column(name = "REMITTANCE_YEAR", precision = 4)
    private Integer remittanceYear;

    @Column(name = "REMITTANCE_REFERENCE_NUMBER", length = 100)
    private String remittanceReferenceNumber;

    @Column(name = "SCHEDULE_NUMBER", length = 100)
    private String scheduleNumber;

    @Column(name = "RECEIPT_NUMBER", length = 100)
    private String receiptNumber;

    @Column(name = "TRANSACTION_REFERENCE_NUMBER", length = 100)
    private String transactionReferenceNumber;

    @Column(name = "POSTING_DATE")
    private LocalDate postingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CONTRIBUTION_TYPE_ID",
            foreignKey = @ForeignKey(name = "FK_WRD_CONTRIBUTION_TYPE")
    )
    private ContributionTypeMaster contributionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "AFFECTED_ACCOUNT_TYPE_ID",
            foreignKey = @ForeignKey(name = "FK_WRD_AFFECTED_ACCOUNT_TYPE")
    )
    private AccountTypeMaster affectedAccountType;

    @Column(name = "AFFECTED_MEMBER_COUNT")
    private Integer affectedMemberCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ERROR_TYPE_ID",
            foreignKey = @ForeignKey(name = "FK_WRD_ERROR_TYPE")
    )
    private WrongRemittanceErrorTypeMaster errorType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYEE_TYPE_ID", nullable = false)
    private PayeeTypeMaster payeeType;

    @Column(name = "TOTAL_REMITTED_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalRemittedAmount;

    @Column(name = "REFUND_REQUESTED_AMOUNT", precision = 15, scale = 2)
    private BigDecimal refundRequestedAmount;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", updatable = false, insertable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
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