package com.claim.claim_processing.common.DTO.update.partial;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisasterTypeUpdateDto {

    private String name;
    private String description;
    private String isActive;
}