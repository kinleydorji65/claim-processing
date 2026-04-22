package com.claim.claim_processing.common.DTO.update.special_case;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialCaseAuthorityUpdateRequestDto {

    private String name;
    private String isActive;
}