package com.claim.claim_processing.common.entities.others.member;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_WORK_INFO", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class WorkInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "AGENCY_CODE")
    private String agencyCode;

    @Column(name = "AGENCY_TYPE_ID")
    private Long agencyTypeId;

    @Column(name = "AGENCY_CATEGORY_ID")
    private String agencyCategoryId;

    @Column(name = "AGENCY_NAME")
    private String agencyName;

    @Column(name = "DESIGNATION")
    private String designation;

    @Column(name = "SERVICE_JOINING_DATE")
    private Date serviceJoiningDate;

    @Column(name = "EMPLOYMENT_TYPE_ID")
    private Long employmentTypeId;

    @Column(name = "BASIC_PAY", precision = 12, scale = 2)
    private BigDecimal basicPay;

    @Column(name = "CREATED_AT", updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "MEMBER_CODE", referencedColumnName = "MEMBER_CODE", nullable = false)
    private MemberDetail member;

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
