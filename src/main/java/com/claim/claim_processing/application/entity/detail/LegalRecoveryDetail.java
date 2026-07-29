package com.claim.claim_processing.application.entity.detail;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.others.Dzongkhag;

@Entity
@Table(name = "LEGAL_RECOVERY_DETAIL", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
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
        @JoinColumn(name = "CLAIM_APPLICATION_ID")
        private ClaimApplication claimApplication;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_DETAIL_ID")
        private ClaimDetail claimDetail;

        @Column(name = "JUDGEMENT_NUMBER", length = 100)
        private String judgementNumber;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PAYEE_TYPE_ID", nullable = false)
        private PayeeTypeMaster payeeType;

        @Column(name = "JUDGEMENT_DATE")
        private LocalDate judgementDate;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "DZONGKHAG_ID")
        private Dzongkhag dzongkhag;

        @Column(name = "CONVICTED_ORDER", length = 100)
        private String convictedOrder;

        @Column(name = "IS_CONVICTED", length = 100)
        @Builder.Default
        private String isConvicted = "Y";

        @Column(name = "PAY_TO_MEMBER", length = 100)
        @Builder.Default
        private String payToMember = "Y";

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