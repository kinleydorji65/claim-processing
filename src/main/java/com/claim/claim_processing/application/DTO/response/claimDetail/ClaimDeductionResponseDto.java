package com.claim.claim_processing.application.DTO.response.claimDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDeductionResponseDto {
    private Long id;
    private BigDecimal outstandingAmount;
    private BigDecimal verifiedDeductedAmount;
    private BigDecimal approvedDeductedAmount;
    private BigDecimal deductedAmount;

    private String remarks;

    private List<ClaimDeductionItemResponseDto> deductionItems;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
