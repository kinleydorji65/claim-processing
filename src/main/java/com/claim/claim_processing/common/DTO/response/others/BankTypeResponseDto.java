package com.claim.claim_processing.common.DTO.response.others;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankTypeResponseDto {

    private Long bankTypeId;
    private String bankTypeName;
}
