package com.claim.claim_processing.integration.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecalculateMemberRequestDTO {
    
    private String year;
    
    private List<NppfAndMonthRequest> nppfAndMonthRequest;
    
    // NEW FLAG: Whether to calculate interest or not
    private Boolean withInterest;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NppfAndMonthRequest {
        private String nppfNumber;
        private List<MonthIds> monthIds;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthIds {
        private Long monthIds;
    }
}