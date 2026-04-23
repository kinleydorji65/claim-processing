package com.claim.claim_processing.claim.entity.application;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.common.entities.beneficiary_master.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.BankType;
import com.claim.claim_processing.common.entities.status_master.VerificationStatusMaster;

@Entity
@Table(name = "CLAIM_APPLICATION_BANK_DETAIL", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationBankDetail {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CABD_CLAIM_APP"))
        private ClaimApplication claimApplication;

        @Column(name = "BENEFICIARY_IDENTIFIER", length = 100)
        private String beneficiaryIdentifier;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIMANT_TYPE_ID", foreignKey = @ForeignKey(name = "FK_CABD_CLAIMANT_TYPE"))
        private ClaimantTypeMaster claimantType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "BANK_TYPE_ID", foreignKey = @ForeignKey(name = "FK_CABD_BANK_TYPE"))
        private BankType bankType;

        @Column(name = "ACCOUNT_NUMBER", length = 100)
        private String accountNumber;

        @Column(name = "ACCOUNT_HOLDER_NAME", length = 200)
        private String accountHolderName;

        @Column(name = "IFSC_OR_ROUTING_CODE", length = 100)
        private String ifscOrRoutingCode;

        @Column(name = "IS_DEFAULT_BANK", length = 1)
        @Builder.Default
        private String isDefaultBank = "N";

        @Column(name = "IS_SELECTED_BANK", length = 1)
        @Builder.Default
        private String isSelectedBank = "N";

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "BANK_VERIFICATION_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CABD_VER_STATUS"))
        private VerificationStatusMaster bankVerificationStatus;

        @Column(name = "REMARKS", length = 1000)
        private String remarks;

        @Column(name = "VERIFIED_BY", length = 100)
        private String verifiedBy;

        @Column(name = "VERIFIED_AT")
        private Timestamp verifiedAt;

        @Enumerated(EnumType.STRING)
        @Column(name = "IS_ACTIVE", length = 1)
        @Builder.Default
        private ActivityEnum isActive = ActivityEnum.Y;

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