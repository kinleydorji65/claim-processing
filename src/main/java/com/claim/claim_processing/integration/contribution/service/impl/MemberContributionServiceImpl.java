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

        // Calculate totals correctly
        BigDecimal pfMcPrincipal = new BigDecimal("500000");
        BigDecimal pfMcInterest = new BigDecimal("2000");
        BigDecimal pfMcTotal = pfMcPrincipal.add(pfMcInterest); // 502,000

        BigDecimal pfImcPrincipal = new BigDecimal("0");
        BigDecimal pfImcInterest = new BigDecimal("2000");
        BigDecimal pfImcTotal = pfImcPrincipal.add(pfImcInterest); // 2,000

        BigDecimal pfEcPrincipal = new BigDecimal("1000000");
        BigDecimal pfEcInterest = new BigDecimal("15000");
        BigDecimal pfEcTotal = pfEcPrincipal.add(pfEcInterest); // 1,015,000

        BigDecimal pfIecPrincipal = new BigDecimal("0");
        BigDecimal pfIecInterest = new BigDecimal("15000");
        BigDecimal pfIecTotal = pfIecPrincipal.add(pfIecInterest); // 15,000

        List<MemberContributionSummary.ComponentGroup> groups = List.of(
                component("PF_MC", "PF Member Contribution", "500000", "2000", pfMcTotal),
                component("PF_IMC", "PF Member Interest", "0", "2000", pfImcTotal),
                component("PF_EC", "PF Employer Contribution", "1000000", "15000", pfEcTotal),
                component("PF_IEC", "PF Employer Interest", "0", "15000", pfIecTotal)
        );

        // Now these totals will be correct
        BigDecimal totalPrincipalAmount = pfMcPrincipal.add(pfImcPrincipal).add(pfEcPrincipal).add(pfIecPrincipal); // 1,500,000
        BigDecimal totalInterestAmount = pfMcInterest.add(pfImcInterest).add(pfEcInterest).add(pfIecInterest); // 34,000
        BigDecimal totalBalance = pfMcTotal.add(pfImcTotal).add(pfEcTotal).add(pfIecTotal); // 1,534,000

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
                .totalPrincipalAmount(totalPrincipalAmount)    // 1,500,000
                .totalInterestAmount(totalInterestAmount)      // 34,000
                .totalBalance(totalBalance)                    // 1,534,000
                .componentGroups(groups)
                .build();
    }

    private MemberContributionSummary.ComponentGroup component(
            String code,
            String name,
            String principal,
            String interest,
            BigDecimal totalAmount) {

        BigDecimal principalAmount = new BigDecimal(principal);
        BigDecimal interestAmount = new BigDecimal(interest);

        return MemberContributionSummary.ComponentGroup.builder()
                .componentCode(code)
                .componentName(name)
                .principalAmount(principalAmount)
                .interestAmount(interestAmount)
                .totalAmount(totalAmount)  // Use pre-calculated total
                .build();
    }
}