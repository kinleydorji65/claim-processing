package com.claim.claim_processing.common.DTO.request.contribution;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemeCreateRequestDto {

    private String code;
    private String name;
    private String description;
}