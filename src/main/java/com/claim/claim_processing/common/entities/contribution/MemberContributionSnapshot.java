package com.claim.claim_processing.common.entities.contribution;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CONTRIBUTION_MASTER", schema = "PPFMS_CONTRIBUTIONS_PAYMENTS_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberContributionSnapshot {

    @Id
    @Column(name = "NPPF_NUMBER", nullable = false, unique = true)
    private String nppfNumber;

    @Column(name = "SCHEME_ID")
    private Long schemeId;

    @Column(name = "TOTAL_CONTRIBUTION_MONTHS")
    private Integer totalContributionMonths;

    @Column(name = "TOTAL_NON_CONTRIBUTION_MONTHS")
    private Integer totalNonContributionMonths;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;
}