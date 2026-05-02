package com.claim.claim_processing.common.DTO.response.others.member;

import lombok.*;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailResponseDto {
    private String memberName;
    private String nppfNumber;
    private String memberCode;
    private String identityTypeName;
    private Long identityNumber;
    private String memberCategory;
    private String employmentTypeName;
    private Date dateOfServiceJoiningDate;
    private String basicSalary;
    private String memberStatus;
    private String agencyCode;
    private String agencyName;

    private List<MemberBankResponseDto> memberBanks;
    private List<MemberNomineeResponseDto> memberNominees;
    private List<MemberFamilyResponseDto> memberFamilies;
}
