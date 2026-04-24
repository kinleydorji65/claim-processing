package com.claim.claim_processing.common.DTO.response.contribution;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemeTypeResponseDto {

    private Long id;
    private String code;
    private String name;
}
