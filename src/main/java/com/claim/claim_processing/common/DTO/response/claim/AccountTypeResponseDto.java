package com.claim.claim_processing.common.DTO.response.claim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTypeResponseDto {

    private Long id;
    private String code;
    private String name;
}