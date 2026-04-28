package com.claim.claim_processing.integration.contribution.service.impl;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.repository.contribution.MemberContributionSnapshotRepository;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.mapper.MemberContributionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberContributionServiceImpl implements MemberContributionService {

    private final MemberContributionSnapshotRepository snapshotRepository;
    private final MemberContributionMapper contributionMapper;

    @Override
    public MemberContributionSummary getContributionSummary(String memberCode) {
        return snapshotRepository.findByMemberCode(memberCode)
            .map(contributionMapper::toSummaryFromEntity)  // Now maps Entity to Summary directly
            .orElseGet(() -> emptySummary(memberCode));
    }
    
    private MemberContributionSummary emptySummary(String memberCode) {
        return MemberContributionSummary.builder()
            .memberCode(memberCode)
            .totalContributionMonths(0)
            .totalContributionYears(0)
            .build();
    }
}
