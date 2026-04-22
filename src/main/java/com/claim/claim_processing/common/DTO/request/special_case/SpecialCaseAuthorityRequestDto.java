package com.claim.claim_processing.common.DTO.request.special_case;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialCaseAuthorityRequestDto {

    private String code;
    private String name;
}