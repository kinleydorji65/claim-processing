package com.claim.claim_processing.common.entities.others.agency;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "AGENCY_BANK_DETAIL", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class AgencyBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACCOUNT_NUMBER")
    private String actNumber;

    // @Column(name = "AGENCY_CODE")
    // private String agencyCode;

    @Column(name = "ACCOUNT_HOLDER_NAME")
    private String holderName;

    @Column(name = "ACCOUNT_TYPE")
    private String accountType;

    @Column(name = "SWIFT_CODE", nullable = true)
    private String swiftCode;

    @Column(name = "BANK_ID")
    private Long bankId;

    @Column(name = "CURRENCY_CODE_ID")
    private Long currencyCodeId;
    
    @Column(name = "IS_DEFAULT")
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @ManyToOne
    @JsonBackReference  
    @JoinColumn(name = "AGENCY_CODE", referencedColumnName = "AGENCY_CODE", nullable = true)
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
