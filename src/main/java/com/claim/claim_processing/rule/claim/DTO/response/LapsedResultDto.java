package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LapsedResultDto {

    private boolean forfeited;

    private List<ComponentBalanceDTO> forfeitedComponents;

    private List<String> forfeitedComponentCodes;

}
