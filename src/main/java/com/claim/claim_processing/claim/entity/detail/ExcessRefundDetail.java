package com.claim.claim_processing.claim.entity.detail;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.claim.entity.applicationEnum.MemberRefundScope;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.refund_master.ExcessRefundReasonMaster;
import com.claim.claim_processing.common.entities.refund_master.RefundScopeMaster;

@Entity
@Table(name = "EXCESS_REFUND_DETAIL", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcessRefundDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, unique = true)
    private ClaimApplication claimApplication;

    @Column(name = "WALK_IN_RECEIVED_BY", length = 100)
    private String walkInReceivedBy;

    @Column(name = "WALK_IN_RECEIVED_DATE")
    private LocalDate walkInReceivedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFUND_SCOPE_ID")
    private RefundScopeMaster refundScope;

    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBER_REFUND_SCOPE", length = 50)
    private MemberRefundScope memberRefundScope;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYEE_TYPE_ID", nullable = false)
    private PayeeTypeMaster payeeType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXCESS_REFUND_REASON_ID")
    private ExcessRefundReasonMaster excessRefundReason;

    @Column(name = "REMARKS_JUSTIFICATION", length = 1000)
    private String remarksJustification;

    @Column(name = "PAYMENT_MONTH")
    private Integer paymentMonth;

    @Column(name = "PAYMENT_YEAR")
    private Integer paymentYear;

    @Column(name = "RECEIPT_NUMBER", length = 100)
    private String receiptNumber;

    @Column(name = "PAYMENT_REFERENCE_NUMBER", length = 100)
    private String paymentReferenceNumber;

    @Column(name = "PAYMENT_DATE")
    private LocalDate paymentDate;

    @Column(name = "TRANSACTION_NUMBER", length = 100)
    private String transactionNumber;

    @Column(name = "BANK_REFERENCE", length = 100)
    private String bankReference;

    @Column(name = "CONTRIBUTION_SCHEDULE_REFERENCE", length = 100)
    private String contributionScheduleReference;

    @Column(name = "UPLOADED_REMITTANCE_MONTH")
    private Integer uploadedRemittanceMonth;

    @Column(name = "UPLOADED_REMITTANCE_YEAR")
    private Integer uploadedRemittanceYear;

    @Column(name = "APPLIED_BY", length = 100)
    private String appliedBy;

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
