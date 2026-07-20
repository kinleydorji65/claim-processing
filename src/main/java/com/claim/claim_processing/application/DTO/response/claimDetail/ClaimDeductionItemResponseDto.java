package com.claim.claim_processing.application.DTO.response.claimDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDeductionItemResponseDto {
    private Long id;

    private String deductionCategory;

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
