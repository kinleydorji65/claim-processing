package com.claim.claim_processing.common.entities.others.agency.agencyRelated;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AGENCY_ADDITIONAL_TYPES", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AgencyAdditionalType {

    @Id
    @Column(name = "TYPE_ID")
    private Long typeId;

    @Column(name = "ADDITIONAL_TYPE_ID")
    private String additionalTypeId;

    @Column(name = "ADDITIONAL_TYPE_NAME")
    private String additionalTypeName;

    @Column(name = "ADDITIONAL_TYPE_CODE")
    private String additionalTypeCode;

    @Column(name = "STATUS", length = 1)
    private String status;

    @Column(name = "AGENCY_CATEGORY_ID")
    private String agencyCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENCY_CATEGORY_CODE", referencedColumnName = "AGENCY_CATEGORY_CODE")
    private AgencyCategory agencyCategory;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;
}

