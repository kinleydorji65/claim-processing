package com.claim.claim_processing.common.DTO.update.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimSourceUpdateDto {

    private String name;
    private String isActive;
}