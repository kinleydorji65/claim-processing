package com.claim.claim_processing.common.DTO.update.partial;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialReasonUpdateDto {

    private String name;
    private String isActive;
}