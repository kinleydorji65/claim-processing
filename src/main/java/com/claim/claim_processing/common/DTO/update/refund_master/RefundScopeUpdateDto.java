package com.claim.claim_processing.common.DTO.update.refund_master;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundScopeUpdateDto {

    private String name;
    private String isActive;
}