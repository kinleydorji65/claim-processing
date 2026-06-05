package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.repository.contribution.MemberContributionSnapshotRepository;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.claim.mapper.MemberContributionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberContributionServiceImpl implements MemberContributionService {

    private final MemberContributionSnapshotRepository snapshotRepository;
    private final MemberContributionMapper contributionMapper;

    @Override
    public MemberContributionSummary getContributionSummary(String nppfNumber) {
        return snapshotRepository.findByNppfNumber(nppfNumber)
                .map(contributionMapper::toSummaryFromEntity)
                .orElseGet(() -> emptySummary(nppfNumber));
    }

    private MemberContributionSummary emptySummary(String nppfNumber) {

        List<MemberContributionSummary.ComponentGroup> groups = List.of(
                component("PF_MC", "PF Member Contribution", "500000", "2000"),
                component("PF_IMC", "PF Member Interest", "0", "2000"),
                component("PF_EC", "PF Employer Contribution", "1000000", "15000"),
                component("PF_IEC", "PF Employer Interest", "0", "15000")
        );

        BigDecimal totalPrincipalAmount = groups.stream()
                .map(MemberContributionSummary.ComponentGroup::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInterestAmount = groups.stream()
                .map(MemberContributionSummary.ComponentGroup::getInterestAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBalance = groups.stream()
                .map(MemberContributionSummary.ComponentGroup::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return MemberContributionSummary.builder()
                .nppfNumber(nppfNumber)
                .schemeTypeId(1L)
                .pfJoiningDate(LocalDate.of(2024, 6, 1))
                .pensionJoiningDate(LocalDate.of(2024, 6, 1))
                .totalContributionMonths(130)
                .totalContributionYears(23)
                .totalNonContributionMonths(6)
                .contributionStartDate(LocalDate.of(2024, 6, 1))
                .contributionEndDate(LocalDate.of(2025, 2, 1))
                .totalPrincipalAmount(totalPrincipalAmount)
                .totalInterestAmount(totalInterestAmount)
                .totalBalance(totalBalance)
                .componentGroups(groups)
                .build();
    }

    private MemberContributionSummary.ComponentGroup component(
            String code,
            String name,
            String principal,
            String interest) {

        BigDecimal principalAmount = new BigDecimal(principal);
        BigDecimal interestAmount = new BigDecimal(interest);

        return MemberContributionSummary.ComponentGroup.builder()
                .componentCode(code)
                .componentName(name)
                .principalAmount(principalAmount)
                .interestAmount(interestAmount)
                .totalAmount(principalAmount.add(interestAmount))
                .build();
    }
}