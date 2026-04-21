package com.claim.claim_processing.common.entities.others.agency;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "AGENCY_CONTACT_PERSON_DETAIL", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class AgencyFocalPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE_ID")
    private Long titleId;

    // @Column(name = "AGENCY_CODE")
    // private String agencyCode;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "NATIONALITY_ID")
    private Long nationalityId;

    @Column(name = "IDENTITY_TYPE_ID")
    private Long identityTypeId;

    @Column(name = "IDENTITY_NUMBER")
    private String identityNumber;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "CONTACT_NUMBER")
    private Long contactNumber;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
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
