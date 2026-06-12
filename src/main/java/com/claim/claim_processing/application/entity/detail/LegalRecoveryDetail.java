package com.claim.claim_processing.application.entity.detail;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.others.StatusMaster;

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
        @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, unique = true)
        private ClaimApplication claimApplication;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_DETAIL_ID", nullable = false, unique = true)
        private ClaimDetail claimDetail;

        @Column(name = "JUDGEMENT_NUMBER", length = 100)
        private String judgementNumber;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PAYEE_TYPE_ID", nullable = false)
        private PayeeTypeMaster payeeType;

        @Column(name = "JUDGEMENT_DATE")
        private LocalDate judgementDate;

        @Column(name = "REASON", length = 1000)
        private String reason;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CURRENT_STATUS_ID")
        private StatusMaster currentStatus;

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