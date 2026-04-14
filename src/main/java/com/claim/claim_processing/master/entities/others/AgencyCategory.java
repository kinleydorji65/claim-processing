package com.claim.claim_processing.master.entities.others;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AGENCY_CATEGORIES", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AgencyCategory {
    @Id
    @Column(name = "CATEGORY_ID")
    private String categoryId;

    @Column(name = "AGENCY_CATEGORY_CODE")
    private String agencyCategoryCode;

    @Column(name = "CATEGORY_NAME")
    private String categoryName;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name= "STATUS")
    private  String status;
}
