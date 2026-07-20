package com.claim.claim_processing.application.DTO.response.application;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationDeductionItemResponseDto {

    private Long id;

    private Long deductionDetailId;

    private String deductionCategory; // LOAN / RENTAL / TAX / OTHER

    private BigDecimal outstandingAmount;

    private BigDecimal deductedAmount;

    private BigDecimal remainingAmount;

    private String remarks;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}