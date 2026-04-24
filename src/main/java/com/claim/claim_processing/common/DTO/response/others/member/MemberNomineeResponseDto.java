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

    // -------------------------------
    // NAME
    // -------------------------------
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;

    // -------------------------------
    // RELATION
    // -------------------------------
    private Long relationId;
    private String relationName; // optional (master)

    // -------------------------------
    // IDENTITY
    // -------------------------------
    private Long identityTypeId;
    private String identityTypeName; // optional

    private String identityNumber;

    // -------------------------------
    // DOB
    // -------------------------------
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
