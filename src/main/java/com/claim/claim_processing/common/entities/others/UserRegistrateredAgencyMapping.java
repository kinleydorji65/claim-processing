package com.claim.claim_processing.common.entities.others;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "USER_REGISTERED_AGENCY_MAPPING", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@IdClass(UserRegistredAgencyMappingId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrateredAgencyMapping {

    @Id
    @Column(name = "USER_CODE", length = 100, nullable = false)
    private String userCode;

    @Id
    @Column(name = "AGENCY_CODE", length = 100, nullable = false)
    private String agencyCode;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
