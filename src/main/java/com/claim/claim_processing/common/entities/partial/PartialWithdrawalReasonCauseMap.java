package com.claim.claim_processing.common.entities.partial;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "PARTIAL_WITHDRAWAL_REASON_CAUSE_MAP", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA", uniqueConstraints = {
                @UniqueConstraint(name = "UK_PARTIAL_WITHDRAWAL_REASON_CAUSE", columnNames = { "REASON_ID",
                                "CAUSE_ID" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalReasonCauseMap {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        /**
         * Reason (UNEMPLOYMENT / BUSINESS / HOUSING)
         */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "REASON_ID", nullable = false)
        private PartialWithdrawalReasonMaster reason;

        /**
         * Cause (DISABILITY / PANDEMIC / NATURAL_DISASTER)
         */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CAUSE_ID", nullable = false)
        private PartialWithdrawalCauseMaster cause;

        @Column(name = "IS_ACTIVE", length = 1)
        @Builder.Default
        private String isActive = "Y";

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private Timestamp createdAt;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "UPDATED_AT", insertable = false)
        private Timestamp updatedAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;
}
