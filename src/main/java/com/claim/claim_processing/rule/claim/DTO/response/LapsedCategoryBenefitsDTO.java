package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;

import lombok.*;

@Data
@Builder
public class LapsedCategoryBenefitsDTO {
    private String agencyCategoryId;
    private String agencyCategoryName;
    private List<EligibleBenefitComponentDTO> allBenefits;
    private List<EligibleBenefitComponentDTO> employerBenefits;  // PF_EC, PC_EC
    private List<EligibleBenefitComponentDTO> memberBenefits;    // PC_MC
}
