package com.claim.claim_processing.claimBulkUpload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimBulkUploadRequestDTO {
    private int rowNumber;
    private String identityNumber;
    private String memberCode;
    private String nppfNumber;
    private String submissionChannel;
    private Date applicationDate;
    private String agencyCode;
    private String contactNo;
    private String email;
    private String agencyName;
    private String onBehalfFoMember;
    private ActivityEnum isSpecialCase;
    private BigDecimal numberOfYearInService;
    private String message;

    private String createdAt;
}


