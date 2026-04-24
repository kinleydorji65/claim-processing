package com.claim.claim_processing.common.DTO.request.agency;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyCategoryRequestDto {

    private String categoryId;

    private String agencyCategoryCode;

    private String categoryName;

    private String status;
}