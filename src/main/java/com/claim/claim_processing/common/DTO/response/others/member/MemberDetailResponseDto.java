package com.claim.claim_processing.common.DTO.response.others.member;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailResponseDto {
    private String memberName;
    private String nppfNumber;
    private Long schemeTypeId;
    private String memberCode;
    private String identityTypeName;
    private String identityNumber;
    private String memberCategory;
    private String employmentTypeName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfServiceJoiningDate;
    private String basicSalary;
    private String memberStatus;
    private String agencyCode;
    private String agencyName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pfJoiningDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pensionJoiningDate;
    private BigDecimal totalBalanceAmount;
    private BigDecimal totalBalanceWithoutInterestAmount;

    private List<MemberBankResponseDto> memberBanks;
    private List<MemberNomineeResponseDto> memberNominees;
    private List<MemberFamilyResponseDto> memberFamilies;
}
