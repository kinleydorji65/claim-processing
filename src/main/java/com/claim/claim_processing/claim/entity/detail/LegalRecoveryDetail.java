package com.claim.claim_processing.claim.entity.detail;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.entities.legalMaster.LegalRecoveryMaster;
import com.claim.claim_processing.common.entities.legalMaster.RecoveryReasonMaster;
import com.claim.claim_processing.common.entities.loanMaster.LoanStatusMaster;
import com.claim.claim_processing.common.entities.loanMaster.LoanTypeMaster;
import com.claim.claim_processing.common.entities.others.StatusMaster;

@Entity
@Table(name = "LEGAL_RECOVERY_DETAIL", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalRecoveryDetail {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, unique = true, foreignKey = @ForeignKey(name = "FK_LRD_CLAIM_APP"))
        private ClaimApplication claimApplication;

        @Column(name = "LEGAL_CASE_REFERENCE_NUMBER", length = 100)
        private String legalCaseReferenceNumber;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "RECOVERY_TYPE_ID", foreignKey = @ForeignKey(name = "FK_LRD_RECOVERY_TYPE"))
        private LegalRecoveryMaster recoveryType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "RECOVERY_REASON_ID", foreignKey = @ForeignKey(name = "FK_LEGAL_RECOVERY_REASON"))
        private RecoveryReasonMaster recoveryReason;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PAYEE_TYPE_ID", nullable = false)
        private PayeeTypeMaster payeeType;
        
        @Column(name = "PF_JOINING_DATE")
        private LocalDate pfJoiningDate;

        @Column(name = "PENSION_JOINING_DATE")
        private LocalDate pensionJoiningDate;

        @Column(name = "CASE_SETTLEMENT_DATE")
        private LocalDate caseSettlementDate;

        @Column(name = "RECOVERY_DATE")
        private LocalDate recoveryDate;

        @Column(name = "RECOVERY_REQUESTED_AMOUNT", precision = 15, scale = 2)
        private BigDecimal recoveryRequestedAmount;

        @Column(name = "REMARKS", length = 1000)
        private String remarks;

        @Column(name = "MEMBER_CODE", length = 100)
        private String memberCode;

        @Column(name = "NPPF_NUMBER", length = 100)
        private String nppfNumber;

        @Column(name = "AGENCY_CODE", length = 100)
        private String agencyCode;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SCHEME_TYPE_ID", foreignKey = @ForeignKey(name = "FK_LRD_SCHEME_TYPE"))
        private SchemeMaster schemeType;

        @Column(name = "EMPLOYMENT_TYPE", length = 50)
        private String employmentType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CURRENT_STATUS_ID", foreignKey = @ForeignKey(name = "FK_LRD_CURRENT_STATUS"))
        private StatusMaster currentStatus;

        @Column(name = "LOAN_ACCOUNT_NUMBER", length = 100)
        private String loanAccountNumber;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "LOAN_TYPE_ID", foreignKey = @ForeignKey(name = "FK_LRD_LOAN_TYPE"))
        private LoanTypeMaster loanType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "LOAN_STATUS_ID", foreignKey = @ForeignKey(name = "FK_LRD_LOAN_STATUS"))
        private LoanStatusMaster loanStatus;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
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