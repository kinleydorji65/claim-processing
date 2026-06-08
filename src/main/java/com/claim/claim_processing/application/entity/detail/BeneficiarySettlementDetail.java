package com.claim.claim_processing.application.entity.detail;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "BENEFICIARY_SETTLEMENT_DETAIL",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiarySettlementDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * One claim application has one beneficiary settlement detail
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, unique = true)
    private ClaimApplication claimApplication;

    @Column(name = "DATE_OF_DEATH")
    private LocalDate dateOfDeath;

    @Column(name = "LAST_CONTRIBUTION_DATE")
    private LocalDate lastContributionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CESSATION_TYPE_ID")
    private CessationTypeMaster cessationType;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT", insertable = false)
    private Timestamp updatedAt;

    @OneToMany(
        mappedBy = "beneficiarySettlementDetail",
        cascade = CascadeType.ALL,
        orphanRemoval = true
)
@Builder.Default
private List<BeneficiaryClaimantDetail> claimantDetails = new ArrayList<>();

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