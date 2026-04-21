package com.claim.claim_processing.common.entities.others.agency;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AGENCY_LICENSE_DETAIL", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AgencyLicense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TPN_NUMBER", length = 255)
    private String tpnNumber;

    @Column(name = "LICENSE_NUMBER", length = 100)
    private String licenseNumber;
    
    @Column(name = "CREATED_AT", updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT", length = 100)
    private Timestamp updatedAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "AGENCY_CODE", referencedColumnName = "AGENCY_CODE", nullable = false, unique = true)
    private AgencyDetail agency;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
