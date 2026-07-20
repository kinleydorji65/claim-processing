package com.claim.claim_processing.application.entity.application;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "CLAIM_APPLICATION_DEDUCTION_DETAIL", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationDeductionDetail {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false)
        private ClaimApplication claimApplication;

        @Column(name = "OUTSTANDING_AMOUNT", precision = 15, scale = 2)
        private BigDecimal outstandingAmount;

        @Column(name = "VERIFIED_DEDUCTED_AMOUNT", precision = 15, scale = 2)
        private BigDecimal verifiedDeductedAmount;

        @Column(name = "APPROVED_DEDUCTED_AMOUNT", precision = 15, scale = 2)
        private BigDecimal approvedDeductedAmount;

        @Column(name = "DEDUCTED_AMOUNT", precision = 15, scale = 2)
        private BigDecimal deductedAmount;

        @Column(name = "REMARKS", length = 1000)
        private String remarks;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private Timestamp createdAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

        @Column(name = "UPDATED_AT", insertable = false, updatable = false)
        private Timestamp updatedAt;

        @OneToMany(mappedBy = "deductionDetail", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<ClaimApplicationDeductionItem> deductionItems = new java.util.ArrayList<>();

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
