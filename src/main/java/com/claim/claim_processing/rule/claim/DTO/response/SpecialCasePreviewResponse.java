package com.claim.claim_processing.rule.claim.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialCasePreviewResponse {
    
    
    private List<ComponentDto> components;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentDto {
        private String component;
        private String componentAmount;
    }
}
