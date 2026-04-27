package com.claim.claim_processing.common.DTO.request.unclaimed;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnclaimedActionRequestDto {

    private String code;
    private String name;
}