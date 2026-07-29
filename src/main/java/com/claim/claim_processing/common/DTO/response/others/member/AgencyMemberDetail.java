package com.claim.claim_processing.common.DTO.response.others.member;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyMemberDetail {
    private String memberName;
    private String nppfNumber;
    private String email;
    private String contactNo;
    private String identityTypeName;
    private String identityNumber;
}
