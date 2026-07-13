package com.claim.claim_processing.integration.contribution.service;

import java.time.LocalDate;

import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;

public interface MemberContributionService {

    MemberContributionSummary getContributionSummary(MemberDetailResponseDto memberDetail, LocalDate relieveDate);

}
