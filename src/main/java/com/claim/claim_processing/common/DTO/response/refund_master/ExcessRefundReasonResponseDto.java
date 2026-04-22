package com.claim.claim_processing.common.DTO.response.refund_master;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcessRefundReasonResponseDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
    private String isActive; // convert from Character

    private String createdBy;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;
}