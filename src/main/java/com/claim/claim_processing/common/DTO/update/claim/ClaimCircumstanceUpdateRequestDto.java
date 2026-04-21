package com.claim.claim_processing.common.DTO.update.claim;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimCircumstanceUpdateRequestDto {

    private String name;
    private String description;
    private String isActive;
}