package com.claim.claim_processing.common.DTO.response.others.member;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBankResponseDto {
    private Long id;
    private String actNumber;
    private String holderName;
    private String accountType;
    private Long bankId;
    private String bankName;  // If you need bank name (requires joining with Bank entity)
    private Boolean isDefault;
    private String memberCode;
}
