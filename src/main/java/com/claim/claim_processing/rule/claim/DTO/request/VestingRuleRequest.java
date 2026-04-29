package com.claim.claim_processing.rule.claim.DTO.request;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class VestingRuleRequest {
    
    // Member Identification
    private String memberCode;
    private String memberCategoryId;        // "01" for Civil, "03" for AF, "04" for Private
    
    // Dates (from request/member service)
    private LocalDate serviceJoiningDate;    // When member joined service
    private LocalDate cessationDate;         // Date of leaving service (from UI)
}