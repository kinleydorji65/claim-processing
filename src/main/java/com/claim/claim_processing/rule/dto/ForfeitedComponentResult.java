package com.claim.claim_processing.rule.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForfeitedComponentResult {

    // Forfeited components - ONLY COMPONENT CODES (no amounts, no
    // ComponentBalanceDTO)
    private List<String> forfeitedComponentCodes;

    // Loan information
    private String loanNote;    
}