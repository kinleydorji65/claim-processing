package com.claim.claim_processing.common.DTO.response.others.member;

import lombok.*;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailResponseDto {

    private Long id;

    // Basic Info
    private String status;
    private Long memberTypeId;
    private String memberTypeName;

    // Member Identity
    private Long identityTypeId;
    private String identityTypeName;
    private String identityNumber;

    private String memberCode;
    private String nppfNumber;
    private String memberCategory;

    // Member Name
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;

    // Contact
    private Long contactNo;
    private String email;

    // Agency Info
    private Long agencyId;
    private String agencyCode;
    private String agencyName;
    private String employerType;
    private Long agencyTypeId;
    private String agencyTypeName;
    private String agencyCategoryId;
    private String agencyCategoryName;

    // Other
    private Long roleId;
    private Date effectiveFrom;
    private String remarks;

    // Child Details
    private List<MemberBankResponseDto> memberBanks;
    private List<MemberNomineeResponseDto> memberNominees;
    private List<MemberFamilyResponseDto> memberFamilies;

    private WorkInfoResponseDto workInfo;
    private MemberAddressResponseDto address;
}
