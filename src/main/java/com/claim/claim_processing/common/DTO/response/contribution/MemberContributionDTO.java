package com.claim.claim_processing.common.DTO.response.contribution;

import java.time.LocalDate;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberContributionDTO {
    private String memberCode;
    private Long schemeId;
    private Integer totalContributionMonths;
    private Integer totalNonContributionMonths;
    private LocalDate startDate;
    private LocalDate endDate;
}