package com.claim.claim_processing.claim.entity.detail;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(
        name = "BENEFICIARY_SETTLEMENT_DETAIL",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
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
    
    @Column(name = "PF_JOINING_DATE")
    private LocalDate pfJoiningDate;

    @Column(name = "PENSION_JOINING_DATE")
    private LocalDate pensionJoiningDate;
    @Column(name = "DATE_OF_DEATH")
    private LocalDate dateOfDeath;

    @Column(name = "SERVICE_JOINING_DATE")
    private LocalDate serviceJoiningDate;

    @Column(name = "LAST_CONTRIBUTION_DATE")
    private LocalDate lastContributionDate;

    @Column(name = "NON_CONTRIBUTION_MONTHS")
    private Integer nonContributionMonths;

    /**
     * Usually DEATH for beneficiary settlement
     */
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