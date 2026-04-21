package com.claim.claim_processing.common.entities.others.member;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.claim.claim_processing.common.entities.others.agency.AgencyDetail;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_DETAIL", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MemberDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REGISTRATION_TYPE")
    private String registrationType;

    @Column(name = "CHANNEL_SELECTION")
    private String channelSelection;

    @Column(name = "MEMBER_TYPE_ID")
    private Long memberTypeId;

    @Column(name = "AGENCY_CODE")
    private String agencyCode;

    @Column(name = "AGENCY_NAME")
    private String agencyName;

    @Column(name = "EMPLOYER_TYPE")
    private String employerType;

    @Column(name = "AGENCY_TYPE_ID")
    private Long agencyTypeId;

    @Column(name = "AGENCY_CATEGORY_ID")
    private String agencyCategoryId;

    @Column(name = "IDENTITY_TYPE_ID")
    private Long identityTypeId;

    @Column(name = "REJECT_IDS")
    private String rejectIds;

    @Column(name = "IS_PRIMARY_MEMBER", nullable = false)
    @Builder.Default
    private Long isPrimaryMember = 0L;

    @Column(name = "MEMBER_CATEGORY", nullable = true)
    private String memberCategory;

    @Column(name = "IDENTITY_NUMBER")
    private String identityNumber;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "NATIONALITY_ID")
    private Long nationalityId;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "LAST_NAME")
    private String lastName;
    
    @Column(name = "IS_KYC_APPROVED")
    @Builder.Default
    private String isKycApproved = "NO";

    @Column(name = "DATE_OF_BIRTH")
    private Date dateOfBirth;

    @Column(name = "AGE")
    private String age;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "MARITAL_STATUS_ID")
    private Long maritalStatusId;

    @Column(name = "CONTACT_NO")
    private Long contactNo;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "MEMBER_CODE")
    private String memberCode;

    @Column(name = "NPPF_NUMBER")
    private String nppfNumber;

    @Column(name = "APPROVED_BY")
    private String approvedBy;

    @Column(name = "APPROVED_AT")
    private Date approvedAt;

    @Column(name = "EFFECTIVE_FROM")
    private Date effectiveFrom;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<MemberBank> memberBanks;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<MemberNominee> memberNominees;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<MemberFamily> memberFamilies;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private WorkInfo workInfo;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private MemberAddress address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "AGENCY_ID", nullable = true)
    private AgencyDetail applicationDetail;

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
