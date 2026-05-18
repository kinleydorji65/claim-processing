package com.claim.claim_processing.common.DTO.response.others;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusMasterResponseDto {
    private Long statusId;
    private String statuseName;
}