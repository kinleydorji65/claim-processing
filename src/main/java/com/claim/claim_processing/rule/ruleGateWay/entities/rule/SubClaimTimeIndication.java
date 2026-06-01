package com.claim.claim_processing.rule.ruleGateWay.entities.rule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SUB_CLAIM_TIME_INDICATION", schema = "PPFMS_MASTER_SERVICE_SCHEMA", uniqueConstraints = {
                @UniqueConstraint(name = "UK_SUB_CLAIM_TIME_IND_CODE", columnNames = "TIME_INDICATION_CODE")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubClaimTimeIndication {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @Column(name = "TIME_INDICATION_CODE", nullable = false, length = 100)
        private String timeIndicationCode;

        @Column(name = "TIME_INDICATION", nullable = false, length = 150)
        private String timeIndication;

        @Column(name = "START_DATE")
        private LocalDate startDate;

        @Column(name = "END_DATE")
        private LocalDate endDate;

        @Column(name = "EFFECTIVE_FROM", nullable = false)
        private LocalDate effectiveFrom;

        @Column(name = "EFFECTIVE_TO")
        private LocalDate effectiveTo;

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "UPDATED_AT")
        private LocalDateTime updatedAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

        @PreUpdate
        public void preUpdate() {
                this.updatedAt = LocalDateTime.now();
        }
}
