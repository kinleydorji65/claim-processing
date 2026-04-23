package com.claim.claim_processing.common.entities.common;

import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "CLAIM_TYPE_DEDUCTION_MAP",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_CLAIM_TYPE_DEDUCTION_MAP",
                        columnNames = {"CLAIM_TYPE_ID", "DEDUCTION_TYPE_ID"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimTypeDeductionMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // -------------------------------
    // RELATIONS
    // -------------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_TYPE_ID", nullable = false)
    private ClaimTypeMaster claimType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEDUCTION_TYPE_ID", nullable = false)
    private DeductionTypeMaster deductionType;

    // -------------------------------
    // BUSINESS FIELDS
    // -------------------------------

    @Column(name = "IS_ALLOWED", nullable = false, length = 1)
    @Builder.Default
    private Character isAllowed = 'Y';

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "REMARKS", length = 300)
    private String remarks;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    @Builder.Default
    private Character isActive = 'Y';

    // -------------------------------
    // AUDIT FIELDS
    // -------------------------------

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}