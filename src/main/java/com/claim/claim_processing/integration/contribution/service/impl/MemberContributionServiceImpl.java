package com.claim.claim_processing.integration.contribution.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
        // return MemberContributionSummary.builder()
        //     .memberCode(memberCode)
        //     .totalContributionMonths(0)
        //     .totalContributionYears(0)
        //     .build();
        MemberContributionSummary summary = MemberContributionSummary.builder()
        .memberCode("PPFMS20260316M00235")
        .schemeTypeId(1L)
        .totalContributionMonths(120)
        .totalContributionYears(10)
        .contributionStartDate(LocalDate.of(2015, 1, 1))
        .contributionEndDate(LocalDate.of(2025, 1, 1))
        .cessationDate(LocalDate.of(2025, 1, 1))
        .balanceAsOfDate(LocalDate.now())

        .componentGroups(List.of(
                MemberContributionSummary.ComponentGroup.builder()
                        .code("PF_MC")
                        .name("Member Contribution")
                        .principal(new BigDecimal("100000"))
                        .interest(new BigDecimal("20000"))
                        .totalBalance(new BigDecimal("120000"))
                        .interestRate(new BigDecimal("0.08"))
                        .lastInterestDate(LocalDate.of(2024, 12, 31))
                        .lastUpdatedDate(LocalDate.now())
                        .build(),

                MemberContributionSummary.ComponentGroup.builder()
                        .code("PF_EC")
                        .name("Employer Contribution")
                        .principal(new BigDecimal("150000"))
                        .interest(new BigDecimal("30000"))
                        .totalBalance(new BigDecimal("180000"))
                        .interestRate(new BigDecimal("0.08"))
                        .lastInterestDate(LocalDate.of(2024, 12, 31))
                        .lastUpdatedDate(LocalDate.now())
                        .build(),

                MemberContributionSummary.ComponentGroup.builder()
                        .code("PC_MC")
                        .name("Pension Contribution")
                        .principal(new BigDecimal("50000"))
                        .interest(new BigDecimal("10000"))
                        .totalBalance(new BigDecimal("60000"))
                        .interestRate(new BigDecimal("0.05"))
                        .lastInterestDate(LocalDate.of(2024, 12, 31))
                        .lastUpdatedDate(LocalDate.now())
                        .build()
        ))

        .totalBalance(new BigDecimal("360000"))
        .build();
        return summary;
    }
}
