package com.claim.claim_processing.common.DTO.request.partial;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisasterTypeRequestDto {

    private String code;
    private String name;
    private String description;
}