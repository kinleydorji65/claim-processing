package com.claim.claim_processing.common.DTO.response.others.member;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberNomineeResponseDto {

    private Long id;
    private String fullName;
    private String relationName; 
    private String identityTypeName; // optional

    private String identityNumber;
    private Date dateOfBirth;

    // -------------------------------
    // SHARE
    // -------------------------------
    private BigDecimal sharePercentage;

    // -------------------------------
    // MEMBER (FLATTENED)
    // -------------------------------
    private String memberCode;
    private String memberName; // optional
}
