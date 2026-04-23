package com.claim.claim_processing.common.DTO.request.others;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NppfOfficeRequestDto {

    private Long code;
    private String name;
}