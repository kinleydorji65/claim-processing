package com.claim.claim_processing.common.DTO.request.refundMaster;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcessRefundReasonRequestDto {

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
}