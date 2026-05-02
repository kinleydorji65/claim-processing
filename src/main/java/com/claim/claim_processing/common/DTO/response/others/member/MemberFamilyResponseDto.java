package com.claim.claim_processing.common.DTO.response.others.member;

import lombok.*;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFamilyResponseDto {

    private Long id;

    // -------------------------------
    // NAME
    // -------------------------------
    private String fullName;

    private String identityTypeName; // optional (master)

    private String identityNumber;

    private String relationName; // optional (master)

    // -------------------------------
    // DOB
    // -------------------------------
    private Date dateOfBirth;

    // -------------------------------
    // MEMBER (FLATTENED)
    // -------------------------------
    private String memberCode;
}
