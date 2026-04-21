package com.claim.claim_processing.common.DTO.update.contribution;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemeUpdateRequestDto {

    private String name;
    private String description;
    private String isActive;
}