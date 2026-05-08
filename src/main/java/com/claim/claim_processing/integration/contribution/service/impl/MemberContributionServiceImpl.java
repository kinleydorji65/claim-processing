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

    List<MemberContributionSummary.ComponentGroup> groups = List.of(

            // ================= PF (Member Contribution) =================
            MemberContributionSummary.ComponentGroup.builder()
                    .code("PF_MC")
                    .name("PF Member Contribution")
                    .principal(new BigDecimal("100000"))
                    .interest(new BigDecimal("20000"))
                    .totalBalance(new BigDecimal("120000"))
                    .interestRate(new BigDecimal("0.08"))
                    .lastInterestDate(LocalDate.of(2024, 12, 31))
                    .lastUpdatedDate(LocalDate.now())
                    .build(),

            // ================= PF (Employer Contribution) =================
            MemberContributionSummary.ComponentGroup.builder()
                    .code("PF_EC")
                    .name("PF Employer Contribution")
                    .principal(new BigDecimal("150000"))
                    .interest(new BigDecimal("30000"))
                    .totalBalance(new BigDecimal("180000"))
                    .interestRate(new BigDecimal("0.08"))
                    .lastInterestDate(LocalDate.of(2024, 12, 31))
                    .lastUpdatedDate(LocalDate.now())
                    .build(),

            // ================= Pension (Member Contribution) =================
            MemberContributionSummary.ComponentGroup.builder()
                    .code("PC_MC")
                    .name("Pension Member Contribution")
                    .principal(new BigDecimal("50000"))
                    .interest(new BigDecimal("10000"))
                    .totalBalance(new BigDecimal("60000"))
                    .interestRate(new BigDecimal("0.05"))
                    .lastInterestDate(LocalDate.of(2024, 12, 31))
                    .lastUpdatedDate(LocalDate.now())
                    .build()
    );

    // ================= totals =================
    BigDecimal totalBalance = groups.stream()
            .map(MemberContributionSummary.ComponentGroup::getTotalBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return MemberContributionSummary.builder()
            .memberCode(memberCode) // KEEP SAME
            .schemeTypeId(1L)

            // service period
            .totalContributionMonths(120)
            .totalContributionYears(10)
            .totalNonContributionMonths(5)
            .contributionStartDate(LocalDate.of(2015, 1, 1))
            .contributionEndDate(LocalDate.of(2025, 1, 1))

            // components
            .componentGroups(groups)

            // total
            .totalBalance(totalBalance)
            .build();
}
}
