package com.claim.claim_processing.common.DTO.request.refundMaster;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundScopeRequestDto {

    private String code;
    private String name;
}