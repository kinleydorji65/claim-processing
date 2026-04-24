package com.claim.claim_processing.common.DTO.others.agency;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyFocalPersonResponseDto {

    private Long id;

    // -------------------------------
    // PERSONAL DETAILS
    // -------------------------------
    private Long titleId;
    private String titleName; // optional (if master lookup is used)

    private String firstName;
    private String middleName;
    private String lastName;

    private String fullName; // derived for UI

    // -------------------------------
    // IDENTITY
    // -------------------------------
    private Long identityTypeId;
    private String identityTypeName; // optional

    private String identityNumber;

    // -------------------------------
    // CONTACT
    // -------------------------------
    private String email;
    private Long contactNumber;

    // -------------------------------
    // AGENCY (FLATTENED)
    // -------------------------------
    private String agencyCode;
    private String agencyName; // optional
}
