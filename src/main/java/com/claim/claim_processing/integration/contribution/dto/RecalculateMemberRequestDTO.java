package com.claim.claim_processing.integration.contribution.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecalculateMemberRequestDTO {
    private String year;
    private List<NppfAndMonthRequest> nppfAndMonthRequest;

    @Data
    @Builder
    @NoArgsConstructor  // ✅ ADD THIS
    @AllArgsConstructor  // ✅ ADD THIS
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