package com.claim.claim_processing.application.DTO.response.application;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationDeductionResponseDto {

        private Long id;
        private String applicationNumber;
        private BigDecimal outstandingAmount;
        private BigDecimal verifiedDeductedAmount;
        private BigDecimal approvedDeductedAmount;

        private BigDecimal deductedAmount;

        private String remarks;


    private List<ClaimApplicationDeductionItemResponseDto> deductionItems;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}