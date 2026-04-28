package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBenefitsDTO {
    private String agencyCategoryId;
    private String agencyCategoryName;
    private List<EligibleBenefitComponentDTO> allBenefits;
    private List<EligibleBenefitComponentDTO> pensionBenefits;
    private List<EligibleBenefitComponentDTO> pfBenefits;
    private List<EligibleBenefitComponentDTO> fullFormulaBenefits;
}
