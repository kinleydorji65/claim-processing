package com.claim.claim_processing.common.DTO.others.agency;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyDetailResponseDto {

    private Long id;

    // -------------------------------
    // BASIC INFO
    // -------------------------------
    private String agencyCode;
    private String agencyName;

    private String status;
    private String employerType;

    private Long agencyTypeId;
    private String agencyTypeName; // optional (from master)

    private String agencyCategoryId;
    private String agencyCategoryName; // optional

    private String claimBy;

    private Long additionalTypeId;
    private Long roleId;

    private String nppfNumber;

    private Timestamp effectiveFrom;

    private String remarks;

    // -------------------------------
    // CONTACT PERSON (ONE)
    // -------------------------------
    private AgencyFocalPersonResponseDto agencyContactPerson;

    // -------------------------------
    // BANK DETAILS (LIST)
    // -------------------------------
    private List<AgencyBankResponseDto> bankDetails;
}
