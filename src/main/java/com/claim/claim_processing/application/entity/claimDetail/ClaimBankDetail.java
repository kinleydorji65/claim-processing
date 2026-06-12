package com.claim.claim_processing.application.entity.claimDetail;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.BankType;

@Entity
@Table(name = "CLAIM_BANK_DETAIL", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimBankDetail {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_DETAIL_ID", nullable = false)
        private ClaimDetail claimDetail;

        @Column(name = "BENEFICIARY_IDENTIFIER", length = 100)
        private String beneficiaryIdentifier;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIMANT_TYPE_ID")
        private ClaimantTypeMaster claimantType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "BANK_TYPE_ID")
        private BankType bankType;

        @Column(name = "ACCOUNT_NUMBER", length = 100)
        private String accountNumber;

        @Column(name = "ACCOUNT_HOLDER_NAME", length = 200)
        private String accountHolderName;

        @Column(name = "IFSC_OR_ROUTING_CODE", length = 100)
        private String ifscOrRoutingCode;

        @Enumerated(EnumType.STRING)
        @Column(name = "IS_DEFAULT_BANK", length = 1)
        @Builder.Default
        private ActivityEnum isDefaultBank = ActivityEnum.N;

        @Column(name = "REMARKS", length = 1000)
        private String remarks;

        @Column(name = "VERIFIED_BY", length = 100)
        private String verifiedBy;

        @Column(name = "VERIFIED_AT")
        private Timestamp verifiedAt;

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