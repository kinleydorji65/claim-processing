package com.claim.claim_processing.rule.claim.DTO.contribution;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberContributionSummary {

    private String memberCode;
    private Long schemeTypeId;
    private Integer totalContributionMonths;
    private Integer totalContributionYears;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contributionStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contributionEndDate;
}
