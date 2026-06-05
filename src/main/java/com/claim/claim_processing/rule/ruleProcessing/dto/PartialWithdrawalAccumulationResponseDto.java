package com.claim.claim_processing.rule.ruleProcessing.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartialWithdrawalAccumulationResponseDto {

    private Long id;

    private String code;

    private String name;

    private String isActive;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;
}