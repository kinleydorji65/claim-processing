package com.claim.claim_processing.common.DTO.response.contribution;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemeResponseDto {

    private Long id;
    private String code;
    private String name;
}
