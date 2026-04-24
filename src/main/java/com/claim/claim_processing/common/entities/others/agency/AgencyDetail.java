package com.claim.claim_processing.common.entities.others.agency;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AGENCY_DETAIL", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class AgencyDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "AGENCY_NAME", length = 255)
    private String agencyName;

    @Column(name = "EMPLOYER_TYPE ", length = 255)
    private String employerType;

    @Column(name = "AGENCY_TYPE_ID")
    private Long agencyTypeId;

    @Column(name = "AGENCY_CATEGORY_ID")
    private String agencyCategoryId;

    @Column(name = "AGENCY_CODE", unique = true)
    private String agencyCode;

    @Column(name = "CLAIM_BY")
    private String claimBy;

    @Column(name = "ADDITIONAL_TYPE_ID")
    private Long additionalTypeId;

    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "EFFECTIVE_FROM")
    private Timestamp effectiveFrom;
    
    @Column(name = "NPPF_NUMBER")
    private String nppfNumber;

    @Column(name = "REMARKS")
    private String remarks;

    @OneToOne(mappedBy = "agency", fetch = FetchType.LAZY)
    private AgencyFocalPerson agencyContactPerson;

    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AgencyBank> agency;
}
