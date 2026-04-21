package com.claim.claim_processing.common.DTO.response.claim;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminationReasonResponseDto {

    private Long id;
    private String code;
    private String name;
}
