package com.claim.claim_processing.common.entities.others;


import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "member_contribution_joining_date_history", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberContributionJoiningDateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_code", nullable = true, length = 50)
    private String memberCode;

    // Source Agency Details
    @Column(name = "source_institution_name", nullable = false, length = 50)
    private String sourceInstitutionName;

    @Column(name = "source_agency_code", length = 50)
    private String sourceAgencyCode;

    @Column(name = "source_agency_name", length = 200)
    private String sourceAgencyName;

    @Column(name = "identity_number", length = 50)
    private String identityNumber;

    // Current/Destination Agency
    @Column(name = "current_agency_code", length = 50)
    private String currentAgencyCode;

    // Original Dates (OLD - from source agency)
    @Column(name = "original_pf_joining_date", nullable = false)
    private LocalDate originalPfJoiningDate;

    @Column(name = "original_pension_joining_date", nullable = false)
    private LocalDate originalPensionJoiningDate;

    // Current Dates (NEW - at current agency)
    @Column(name = "current_pf_joining_date", nullable = false)
    private LocalDate currentPfJoiningDate;

    @Column(name = "current_pension_joining_date", nullable = false)
    private LocalDate currentPensionJoiningDate;

    // Service Period at Source Agency
    @Column(name = "service_from_date", nullable = false)
    private LocalDate serviceFromDate;

    @Column(name = "service_to_date", nullable = false)
    private LocalDate serviceToDate;

    // Transfer Details
    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    @Column(name = "transfer_type", length = 20)
    private String transferType;

    // Combined Dates (For Claim Calculation)
    @Column(name = "combined_pf_joining_date")
    private LocalDate combinedPfJoiningDate;

    @Column(name = "combined_pension_joining_date")
    private LocalDate combinedPensionJoiningDate;

    // Status
    @Column(name = "status", length = 20)
    private String status;

    // Audit Fields
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedAt;

    @Column(name = "remarks", length = 200)
    private String remarks;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

