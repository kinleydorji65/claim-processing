package com.claim.claim_processing.common.DTO.request.partial;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalReasonRequestDto {

    private String code;
    private String name;
}