package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.others.AccountingInterestMaster;
import com.claim.claim_processing.common.entities.others.CutoffServiceMaster;
import com.claim.claim_processing.common.repository.others.AccountingInterestMasterRepository;
import com.claim.claim_processing.common.repository.others.CutoffServiceMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.entity.ArrConfiguration;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;
import com.claim.claim_processing.integration.contribution.entity.MemberBalanceSnapshot;
import com.claim.claim_processing.integration.contribution.repository.ArrConfigurationRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationDetailRepository;
import com.claim.claim_processing.integration.contribution.repository.MemberBalanceSnapshotRepository;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberContributionServiceImpl implements MemberContributionService {

    private final MemberBalanceSnapshotRepository snapshotRepo;
    private final ContributionBifurcationDetailRepository contributionDetailRepo;
    private final AccountingInterestMasterRepository interestMasterRepository;
    private final CutoffServiceMasterRepository cutoffServiceMasterRepository;
    private final ArrConfigurationRepository arrRepo;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final int TRANSITION_YEAR = 2022;

    // Set of forfeited component codes
    private static final Set<String> FORFEITED_COMPONENTS = new HashSet<>(Arrays.asList(
            "PF_EC", "PF_IEC", "PF_GC", "PF_IGC"
    ));

    @Override
    public MemberContributionSummary getContributionSummary(
            MemberDetailResponseDto memberDetail,
            LocalDate relieveDate) {

        LocalDate asOfDate = LocalDate.now();
        log.info("Calculating contributions as of current date: {}", asOfDate);
        return getContributionSummary(memberDetail, relieveDate, asOfDate);
    }

    public MemberContributionSummary getContributionSummary(
            MemberDetailResponseDto memberDetail,
            LocalDate relieveDate,
            LocalDate asOfDate) {

        log.info("=== START getContributionSummary (asOfDate: {}) ===", asOfDate);
        log.info("nppfNumber: {}, cid: {}", memberDetail.getNppfNumber(), memberDetail.getIdentityNumber());

        try {
            String cid = memberDetail.getIdentityNumber();
            String nppfNumber = memberDetail.getNppfNumber();

            // ================================================================
            // STEP 1: READ FROM CONTRIBUTION_BIFURCATION_DETAIL ONLY
            // ================================================================
            List<ContributionBifurcationDetail> allContributions = contributionDetailRepo
                    .findByCidAndNppfNumberOrderByCreatedAtAsc(cid, nppfNumber);
            
            if (allContributions.isEmpty()) {
                log.error("No contributions found for member: {}", nppfNumber);
                throw ClaimException.notFound("No contributions found for member: " + nppfNumber);
            }

            log.info("Found {} contribution records", allContributions.size());

            // ================================================================
            // STEP 2: GET LAST CONTRIBUTION DATE
            // ================================================================
            LocalDate lastContributionDate = getLastContributionDate(allContributions);
            log.info("Last Contribution Date: {}", lastContributionDate);

            // ================================================================
            // STEP 3: AGGREGATE ALL PRINCIPAL AMOUNTS
            // ================================================================
            BigDecimal totalPfEc = BigDecimal.ZERO;
            BigDecimal totalPfMc = BigDecimal.ZERO;
            BigDecimal totalPensionEc = BigDecimal.ZERO;
            BigDecimal totalGc = BigDecimal.ZERO;
            BigDecimal totalVc = BigDecimal.ZERO;

            BigDecimal totalPrincipal = BigDecimal.ZERO;

            log.info("========== DETAILED CONTRIBUTIONS ==========");
            for (ContributionBifurcationDetail contrib : allContributions) {
                String status = contrib.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    log.debug("Skipping non-POSTED contribution: {}", status);
                    continue;
                }

                BigDecimal pfEc = contrib.getPfEc() != null ? contrib.getPfEc() : BigDecimal.ZERO;
                BigDecimal pfMc = contrib.getPfMc() != null ? contrib.getPfMc() : BigDecimal.ZERO;
                BigDecimal pensionEc = contrib.getPensionEc() != null ? contrib.getPensionEc() : BigDecimal.ZERO;
                BigDecimal gc = contrib.getGc() != null ? contrib.getGc() : BigDecimal.ZERO;
                BigDecimal vc = contrib.getVc() != null ? contrib.getVc() : BigDecimal.ZERO;

                log.debug("Contribution: PF_EC={}, PF_MC={}, PENSION_EC={}, GC={}, VC={}", 
                        pfEc, pfMc, pensionEc, gc, vc);

                totalPfEc = totalPfEc.add(pfEc);
                totalPfMc = totalPfMc.add(pfMc);
                totalPensionEc = totalPensionEc.add(pensionEc);
                totalGc = totalGc.add(gc);
                totalVc = totalVc.add(vc);

                totalPrincipal = totalPrincipal.add(pfEc).add(pfMc).add(pensionEc).add(gc).add(vc);
            }

            log.info("========== AGGREGATED PRINCIPAL TOTALS ==========");
            log.info("PF_EC: {}", totalPfEc);
            log.info("PF_MC: {}", totalPfMc);
            log.info("PENSION_EC: {}", totalPensionEc);
            log.info("GC: {}", totalGc);
            log.info("VC: {}", totalVc);
            log.info("Total Principal: {}", totalPrincipal);

            // ================================================================
            // STEP 4: CALCULATE INTEREST FOR EACH COMPONENT USING asOfDate
            // ================================================================
            String currentAccountingYear = getAccountingYearForDate(asOfDate);

            AccountingInterestMaster interestMaster = getInterestRateDetail(currentAccountingYear);
            BigDecimal rate = BigDecimal.ZERO;

            if (interestMaster != null) {
                rate = interestMaster.getInterestRate();
                log.info("Interest Rate for {}: {}%", currentAccountingYear, rate);
            } else {
                List<AccountingInterestMaster> allRates = interestMasterRepository.findAll();
                if (!allRates.isEmpty()) {
                    allRates.sort((a, b) -> b.getId().compareTo(a.getId()));
                    rate = allRates.get(0).getInterestRate();
                    log.info("Using latest available rate: {}%", rate);
                } else {
                    rate = BigDecimal.valueOf(6.5);
                    log.info("No interest rate found, using default: {}%", rate);
                }
            }

            int yearBasis = getYearBasisForYear(currentAccountingYear);
            if (yearBasis == 0) {
                yearBasis = 365;
            }

            log.info("Final Interest Rate: {}%, Year Basis: {}", rate, yearBasis);

            // Calculate interest for each component
            BigDecimal interestPfEc = calculateInterestForComponent(allContributions, "PF_EC", rate, yearBasis, asOfDate);
            BigDecimal interestPfMc = calculateInterestForComponent(allContributions, "PF_MC", rate, yearBasis, asOfDate);
            BigDecimal interestPension = calculateInterestForComponent(allContributions, "PENSION_EC", rate, yearBasis, asOfDate);
            BigDecimal interestGc = calculateInterestForComponent(allContributions, "GC", rate, yearBasis, asOfDate);
            BigDecimal interestVc = calculateInterestForComponent(allContributions, "VC", rate, yearBasis, asOfDate);

            BigDecimal totalInterest = interestPfEc.add(interestPfMc).add(interestPension).add(interestGc).add(interestVc);

            log.info("========== CALCULATED INTEREST ==========");
            log.info("PF_EC Interest: {}", interestPfEc);
            log.info("PF_MC Interest: {}", interestPfMc);
            log.info("PENSION Interest: {}", interestPension);
            log.info("GC Interest: {}", interestGc);
            log.info("VC Interest: {}", interestVc);
            log.info("Total Interest: {}", totalInterest);

            // ================================================================
            // STEP 5: BUILD COMPONENT GROUPS - SEPARATE PRINCIPAL AND INTEREST
            // ================================================================
            List<MemberContributionSummary.ComponentGroup> componentGroups = new ArrayList<>();

            log.info("========== BUILDING COMPONENT GROUPS ==========");

            // PF_EC - Principal
            if (totalPfEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_EC", "Employee PF Contribution",
                        totalPfEc, BigDecimal.ZERO));
                log.info("✅ PF_EC: Principal={}", totalPfEc);
            }

            // PF_IEC - Interest on PF_EC
            if (interestPfEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_IEC", "Interest on Employee PF",
                        BigDecimal.ZERO, interestPfEc));
                log.info("✅ PF_IEC: Interest={}", interestPfEc);
            }

            // PF_MC - Principal
            if (totalPfMc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_MC", "Employer PF Contribution",
                        totalPfMc, BigDecimal.ZERO));
                log.info("✅ PF_MC: Principal={}", totalPfMc);
            }

            // PF_IMC - Interest on PF_MC
            if (interestPfMc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_IMC", "Interest on Employer PF",
                        BigDecimal.ZERO, interestPfMc));
                log.info("✅ PF_IMC: Interest={}", interestPfMc);
            }

            // P_EC - Principal (Pension)
            if (totalPensionEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("P_EC", "Pension Contribution",
                        totalPensionEc, BigDecimal.ZERO));
                log.info("✅ P_EC: Principal={}", totalPensionEc);
            }

            // P_IEC - Interest on Pension
            if (interestPension.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("P_IEC", "Interest on Pension",
                        BigDecimal.ZERO, interestPension));
                log.info("✅ P_IEC: Interest={}", interestPension);
            }

            // GC - Principal
            if (totalGc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("GC", "Government Contribution",
                        totalGc, BigDecimal.ZERO));
                log.info("✅ GC: Principal={}", totalGc);
            }

            // IGC - Interest on GC
            if (interestGc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("IGC", "Interest on Government",
                        BigDecimal.ZERO, interestGc));
                log.info("✅ IGC: Interest={}", interestGc);
            }

            // VC - Principal
            if (totalVc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("VC", "Voluntary Contribution",
                        totalVc, BigDecimal.ZERO));
                log.info("✅ VC: Principal={}", totalVc);
            }

            // IVC - Interest on VC
            if (interestVc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("IVC", "Interest on Voluntary",
                        BigDecimal.ZERO, interestVc));
                log.info("✅ IVC: Interest={}", interestVc);
            }

            // ================================================================
            // STEP 6: CALCULATE TOTALS
            // ================================================================
            
            // Calculate total from ALL component groups (this is the grand total)
            BigDecimal totalBalance = componentGroups.stream()
                    .map(MemberContributionSummary.ComponentGroup::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Split components into eligible and forfeited for display purposes
            List<MemberContributionSummary.ComponentGroup> eligibleGroups = new ArrayList<>();
            List<MemberContributionSummary.ComponentGroup> forfeitedGroups = new ArrayList<>();

            for (MemberContributionSummary.ComponentGroup group : componentGroups) {
                if (isForfeitedComponent(group.getComponentCode())) {
                    forfeitedGroups.add(group);
                } else {
                    eligibleGroups.add(group);
                }
            }

            // Calculate eligible total and forfeited total
            BigDecimal eligibleTotal = eligibleGroups.stream()
                    .map(MemberContributionSummary.ComponentGroup::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal forfeitedTotal = forfeitedGroups.stream()
                    .map(MemberContributionSummary.ComponentGroup::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            log.info("========== FINAL SUMMARY ==========");
            log.info("Total Principal: {}", totalPrincipal);
            log.info("Total Interest: {}", totalInterest);
            log.info("Eligible Total: {}", eligibleTotal);
            log.info("Forfeited Total: {}", forfeitedTotal);
            log.info("Grand Total (Eligible + Forfeited): {}", totalBalance);
            log.info("Components: {}", componentGroups.size());

            // ================================================================
            // STEP 7: BUILD SUMMARY
            // ================================================================
            MemberContributionSummary.MemberContributionSummaryBuilder builder = MemberContributionSummary.builder()
                    .nppfNumber(nppfNumber)
                    .schemeTypeId(getSchemeTypeId(memberDetail))
                    .totalContributionMonths(calculateContributionMonths(allContributions, asOfDate))
                    .totalContributionYears(calculateContributionMonths(allContributions, asOfDate) / 12)
                    .totalNonContributionMonths(
                            calculateNonContributionMonths(memberDetail, relieveDate, allContributions))
                    .contributionEndDate(lastContributionDate)
                    .totalPrincipalAmount(totalPrincipal)
                    .totalInterestAmount(totalInterest)
                    .totalBalance(totalBalance)  // This is the GRAND TOTAL (eligible + forfeited)
                    .componentGroups(componentGroups)  // All components with their principal/interest
                    .asOfDate(asOfDate)
                    .currentAccountingYear(currentAccountingYear)
                    .openingBalanceFromSnapshot(totalBalance);

            // ================================================================
            // STEP 8: EXCESS SERVICE
            // ================================================================
            if (relieveDate != null && isPensionEligible(memberDetail)) {
                ExcessCalculationResult excessResult = calculateExcessServiceHybrid(
                        cid, nppfNumber, relieveDate, asOfDate);

                if (excessResult != null && excessResult.isEligible()) {
                    builder.excessServiceAmount(excessResult.getTotalExcessAmount())
                            .cutoffServiceDate(excessResult.getCutoffServiceDate())
                            .cutoffYears(excessResult.getCutoffYears())
                            .excessStartDate(excessResult.getExcessStartDate())
                            .excessEndDate(excessResult.getExcessEndDate())
                            .totalEOLMonths(excessResult.getTotalEOLMonths())
                            .eolMonthsInExcess(excessResult.getEolMonthsInExcess())
                            .totalContributionsInExcess(excessResult.getTotalExcessContributions())
                            .totalInterestInExcess(excessResult.getTotalExcessInterest())
                            .excessStatus("CALCULATED")
                            .excessMessage("Excess service calculated successfully as of " + asOfDate);
                }
            }

            MemberContributionSummary summary = builder.build();

            log.info("=== FINAL RESPONSE ===");
            for (MemberContributionSummary.ComponentGroup g : summary.getComponentGroups()) {
                log.info("  {}: Principal={}, Interest={}, Total={}, IsForfeited={}",
                        g.getComponentCode(),
                        g.getPrincipalAmount(),
                        g.getInterestAmount(),
                        g.getTotalAmount(),
                        isForfeitedComponent(g.getComponentCode()));
            }

            return summary;

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            throw ClaimException.internalError("Failed to fetch contribution summary: " + e.getMessage());
        }
    }

    /**
     * Check if a component is forfeited
     */
    private boolean isForfeitedComponent(String componentCode) {
        if (componentCode == null) return false;
        return FORFEITED_COMPONENTS.contains(componentCode.toUpperCase());
    }

    /**
     * Get the last contribution date from the list of contributions
     */
    private LocalDate getLastContributionDate(List<ContributionBifurcationDetail> allContributions) {
        if (allContributions == null || allContributions.isEmpty()) {
            return LocalDate.now();
        }

        LocalDate lastDate = null;
        for (ContributionBifurcationDetail contrib : allContributions) {
            String status = contrib.getPostingStatus();
            if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                continue;
            }

            LocalDate contribDate = contrib.getCreatedAt().toLocalDate();
            if (lastDate == null || contribDate.isAfter(lastDate)) {
                lastDate = contribDate;
            }
        }

        return lastDate != null ? lastDate : LocalDate.now();
    }

    /**
     * Calculate days held based on accounting year rules
     */
    private long calculateDaysHeld(LocalDate contributionDate, LocalDate asOfDate) {
        if (contributionDate == null || asOfDate == null) {
            return 0;
        }

        int year = asOfDate.getYear();
        LocalDate accountingYearStart;

        if (year < TRANSITION_YEAR) {
            accountingYearStart = LocalDate.of(year, 7, 1);
        } else if (year == TRANSITION_YEAR) {
            accountingYearStart = LocalDate.of(year, 7, 1);
        } else {
            accountingYearStart = LocalDate.of(year, 1, 1);
        }

        long daysHeld = ChronoUnit.DAYS.between(accountingYearStart, asOfDate);

        if (contributionDate.getYear() == asOfDate.getYear()) {
            LocalDate yearStart = LocalDate.of(asOfDate.getYear(), 1, 1);
            daysHeld = ChronoUnit.DAYS.between(yearStart, asOfDate);
            log.debug("Same year: Using Jan 1 to asOfDate = {} days", daysHeld);
        }

        if (daysHeld < 0) {
            daysHeld = 0;
        }

        return daysHeld;
    }

    /**
     * Calculate interest for a specific component
     * Formula: Interest = Principal × (Rate/100) × (DaysHeld/YearBasis)
     */
    private BigDecimal calculateInterestForComponent(
            List<ContributionBifurcationDetail> contributions,
            String componentCode,
            BigDecimal rate,
            int yearBasis,
            LocalDate asOfDate) {

        BigDecimal totalInterest = BigDecimal.ZERO;

        log.debug("========== CALCULATING INTEREST FOR {} ==========", componentCode);
        log.debug("Rate: {}%, Year Basis: {}, AsOfDate: {}", rate, yearBasis, asOfDate);

        for (ContributionBifurcationDetail contrib : contributions) {
            String status = contrib.getPostingStatus();
            if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                continue;
            }

            BigDecimal principal = getComponentPrincipal(contrib, componentCode);
            LocalDate contributionDate = contrib.getCreatedAt().toLocalDate();

            if (principal.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            LocalDate yearStart = LocalDate.of(asOfDate.getYear(), 1, 1);
            long daysHeld = ChronoUnit.DAYS.between(yearStart, asOfDate);

            if (daysHeld <= 0) {
                continue;
            }

            BigDecimal factor = BigDecimal.valueOf(daysHeld)
                    .divide(BigDecimal.valueOf(yearBasis), 8, RM);

            BigDecimal interest = principal
                    .multiply(rate)
                    .divide(HUNDRED, 10, RM)
                    .multiply(factor)
                    .setScale(2, RM);

            totalInterest = totalInterest.add(interest);

            log.debug("  ✅ {}: Principal={}, Days={}, Factor={}, Interest={}",
                    componentCode, principal, daysHeld, factor, interest);
        }

        log.debug("Total Interest for {}: {}", componentCode, totalInterest);
        return totalInterest;
    }

    private BigDecimal getComponentPrincipal(ContributionBifurcationDetail contrib, String componentCode) {
        switch (componentCode) {
            case "PF_EC":
                return contrib.getPfEc() != null ? contrib.getPfEc() : BigDecimal.ZERO;
            case "PF_MC":
                return contrib.getPfMc() != null ? contrib.getPfMc() : BigDecimal.ZERO;
            case "PENSION_EC":
                return contrib.getPensionEc() != null ? contrib.getPensionEc() : BigDecimal.ZERO;
            case "GC":
                return contrib.getGc() != null ? contrib.getGc() : BigDecimal.ZERO;
            case "VC":
                return contrib.getVc() != null ? contrib.getVc() : BigDecimal.ZERO;
            default:
                return BigDecimal.ZERO;
        }
    }

    // ================================================================
    // EXCESS SERVICE
    // ================================================================

    private ExcessCalculationResult calculateExcessServiceHybrid(
            String cid,
            String nppfNumber,
            LocalDate relieveDate,
            LocalDate asOfDate) {

        log.info("=== Calculating Excess Service ===");

        List<ContributionBifurcationDetail> allContributions = contributionDetailRepo
                .findByCidAndNppfNumberOrderByCreatedAtAsc(cid, nppfNumber);

        if (allContributions.isEmpty()) {
            return buildEmptyExcessResult();
        }

        CutoffServiceMaster config = getActiveCutoffConfig();
        if (config == null) {
            return buildEmptyExcessResult();
        }

        int cutoffYears = config.getNumberOfYears();
        LocalDate startDate = allContributions.get(0).getCreatedAt().toLocalDate();
        if (startDate == null) {
            return buildEmptyExcessResult();
        }

        LocalDate effectiveEndDate = relieveDate.isBefore(asOfDate) ? relieveDate : asOfDate;
        int totalEOLMonths = calculateEOLMonthsFromHistory(allContributions, startDate, effectiveEndDate);

        int totalMonths = (cutoffYears * 12) + totalEOLMonths;
        LocalDate cutoffServiceDate = startDate.plusMonths(totalMonths);

        if (!effectiveEndDate.isAfter(cutoffServiceDate)) {
            log.info("Member not eligible for excess as of {}", asOfDate);
            return ExcessCalculationResult.builder()
                    .isEligible(false)
                    .totalExcessAmount(BigDecimal.ZERO)
                    .build();
        }

        LocalDate excessStart = cutoffServiceDate.plusMonths(1);
        LocalDate excessEnd = effectiveEndDate;

        List<ContributionBifurcationDetail> excessContributions = allContributions.stream()
                .filter(c -> {
                    LocalDate date = c.getCreatedAt().toLocalDate();
                    return !date.isBefore(excessStart) && !date.isAfter(excessEnd);
                })
                .sorted(Comparator.comparing(ContributionBifurcationDetail::getCreatedAt))
                .collect(Collectors.toList());

        return calculateExcessBalance(excessContributions, excessStart, excessEnd,
                cutoffServiceDate, cutoffYears, totalEOLMonths);
    }

    private ExcessCalculationResult calculateExcessBalance(
            List<ContributionBifurcationDetail> excessContributions,
            LocalDate excessStart,
            LocalDate excessEnd,
            LocalDate cutoffServiceDate,
            int cutoffYears,
            int totalEOLMonths) {

        BigDecimal totalExcessContributions = BigDecimal.ZERO;
        BigDecimal totalExcessInterest = BigDecimal.ZERO;
        int eolMonthsInExcess = 0;

        String accountingYear = getAccountingYearForDate(excessStart);
        AccountingInterestMaster interestMaster = getInterestRateDetail(accountingYear);
        int yearBasis = getYearBasisForYear(accountingYear);
        BigDecimal rate = interestMaster != null ? interestMaster.getInterestRate() : BigDecimal.ZERO;

        for (ContributionBifurcationDetail contrib : excessContributions) {
            BigDecimal principal = getTotalContributionAmount(contrib);
            totalExcessContributions = totalExcessContributions.add(principal);

            if (rate.compareTo(BigDecimal.ZERO) > 0 && yearBasis > 0) {
                LocalDate contributionDate = contrib.getCreatedAt().toLocalDate();
                long daysHeld = calculateDaysHeld(contributionDate, excessEnd);
                if (daysHeld > 0) {
                    BigDecimal factor = BigDecimal.valueOf(daysHeld)
                            .divide(BigDecimal.valueOf(yearBasis), 8, RM);
                    BigDecimal interest = principal
                            .multiply(rate)
                            .divide(HUNDRED, 10, RM)
                            .multiply(factor)
                            .setScale(2, RM);
                    totalExcessInterest = totalExcessInterest.add(interest);
                }
            }
        }

        BigDecimal totalExcessAmount = totalExcessContributions.add(totalExcessInterest);

        log.info("EXCESS SERVICE SUMMARY:");
        log.info("  Total Excess Contributions: {}", totalExcessContributions);
        log.info("  Total Excess Interest: {}", totalExcessInterest);
        log.info("  Total Excess Amount: {}", totalExcessAmount);

        return ExcessCalculationResult.builder()
                .isEligible(true)
                .totalExcessAmount(totalExcessAmount)
                .totalExcessContributions(totalExcessContributions)
                .totalExcessInterest(totalExcessInterest)
                .eolMonthsInExcess(eolMonthsInExcess)
                .cutoffServiceDate(cutoffServiceDate)
                .cutoffYears(cutoffYears)
                .excessStartDate(excessStart)
                .excessEndDate(excessEnd)
                .totalEOLMonths(totalEOLMonths)
                .yearDetails(Collections.emptyList())
                .monthlyDetails(Collections.emptyList())
                .build();
    }

    // ================================================================
    // HELPER METHODS
    // ================================================================

    private BigDecimal getTotalContributionAmount(ContributionBifurcationDetail contrib) {
        return n(contrib.getPfEc())
                .add(n(contrib.getPfMc()))
                .add(n(contrib.getPensionEc()))
                .add(n(contrib.getGc()))
                .add(n(contrib.getVc()));
    }

    private MemberContributionSummary.ComponentGroup createComponentGroup(
            String code, String name, BigDecimal principal, BigDecimal interest) {
        return MemberContributionSummary.ComponentGroup.builder()
                .componentCode(code)
                .componentName(name)
                .principalAmount(principal != null ? principal : BigDecimal.ZERO)
                .interestAmount(interest != null ? interest : BigDecimal.ZERO)
                .totalAmount((principal != null ? principal : BigDecimal.ZERO)
                        .add(interest != null ? interest : BigDecimal.ZERO))
                .build();
    }

    private List<MemberBalanceSnapshot> getSnapshots(String cid, String nppfNumber) {
        if (cid == null || cid.isEmpty()) {
            return snapshotRepo.findByNppfNumberOrderByAccountingYearDesc(nppfNumber);
        } else {
            return snapshotRepo.findByCidAndNppfNumberOrderByAccountingYearDesc(cid, nppfNumber);
        }
    }

    private String getAccountingYearForDate(LocalDate date) {
        if (date == null) {
            return String.valueOf(LocalDate.now().getYear());
        }
        int year = date.getYear();
        int month = date.getMonthValue();

        if (year < TRANSITION_YEAR) {
            if (month <= 6)
                return (year - 1) + "-" + year;
            else
                return year + "-" + (year + 1);
        } else if (year == TRANSITION_YEAR) {
            if (month <= 6)
                return (year - 1) + "-" + year;
            else
                return year + "-" + year;
        } else {
            return year + "-" + year;
        }
    }

    private int getYearBasisForYear(String accountingYear) {
        try {
            Optional<ArrConfiguration> arrOpt = arrRepo.findByAccountingYear(accountingYear);
            if (arrOpt.isPresent()) {
                return arrOpt.get().getYearBasis();
            }
            return 365;
        } catch (Exception e) {
            return 365;
        }
    }

    private AccountingInterestMaster getInterestRateDetail(String accountingYear) {
        return interestMasterRepository.findByFinancialYear(accountingYear).orElse(null);
    }

    private int calculateContributionMonths(
            List<ContributionBifurcationDetail> allContributions,
            LocalDate asOfDate) {

        int count = 0;
        for (ContributionBifurcationDetail contrib : allContributions) {
            String status = contrib.getPostingStatus();
            if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                continue;
            }

            LocalDate date = contrib.getCreatedAt().toLocalDate();
            if (!date.isAfter(asOfDate)) {
                count++;
            }
        }
        return count;
    }

    private int calculateNonContributionMonths(
            MemberDetailResponseDto memberDetail,
            LocalDate relieveDate,
            List<ContributionBifurcationDetail> allContributions) {

        if (relieveDate == null || !isPensionEligible(memberDetail)) {
            return 0;
        }

        LocalDate startDate = getStartDate(memberDetail);
        if (startDate == null) {
            return 0;
        }

        return calculateEOLMonthsFromHistory(allContributions, startDate, relieveDate);
    }

    private boolean isPensionEligible(MemberDetailResponseDto memberDetail) {
        try {
            String categoryId = memberDetail.getMemberCategoryId();
            return categoryId != null && !"04".equals(categoryId.trim());
        } catch (Exception e) {
            return false;
        }
    }

    private LocalDate getStartDate(MemberDetailResponseDto memberDetail) {
        if (memberDetail.getPfJoiningDate() != null) {
            return memberDetail.getPfJoiningDate();
        }
        if (memberDetail.getDateOfServiceJoiningDate() != null) {
            return memberDetail.getDateOfServiceJoiningDate().toLocalDate();
        }
        return null;
    }

    private CutoffServiceMaster getActiveCutoffConfig() {
        return cutoffServiceMasterRepository.findAll().stream()
                .filter(config -> "Y".equals(config.getStatus()))
                .findFirst()
                .orElse(null);
    }

    private int calculateEOLMonthsFromHistory(
            List<ContributionBifurcationDetail> allContributions,
            LocalDate startDate,
            LocalDate endDate) {

        if (allContributions.isEmpty() || startDate == null || endDate == null)
            return 0;

        Set<String> contributionMonths = allContributions.stream()
                .filter(c -> c.getCreatedAt() != null)
                .map(c -> c.getCreatedAt().toLocalDate().getYear() + "-" +
                        String.format("%02d", c.getCreatedAt().toLocalDate().getMonthValue()))
                .collect(Collectors.toSet());

        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);
        int eolMonths = 0;

        YearMonth current = startMonth;
        while (!current.isAfter(endMonth)) {
            String monthKey = current.getYear() + "-" + String.format("%02d", current.getMonthValue());
            if (!contributionMonths.contains(monthKey)) {
                eolMonths++;
            }
            current = current.plusMonths(1);
        }

        return eolMonths;
    }

    private BigDecimal n(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private Long getSchemeTypeId(MemberDetailResponseDto memberDetail) {
        try {
            return memberDetail.getSchemeTypeId();
        } catch (Exception e) {
            return 1L;
        }
    }

    private ExcessCalculationResult buildEmptyExcessResult() {
        return ExcessCalculationResult.builder()
                .isEligible(false)
                .totalExcessAmount(BigDecimal.ZERO)
                .totalExcessContributions(BigDecimal.ZERO)
                .totalExcessInterest(BigDecimal.ZERO)
                .eolMonthsInExcess(0)
                .yearDetails(Collections.emptyList())
                .monthlyDetails(Collections.emptyList())
                .cutoffServiceDate(null)
                .cutoffYears(0)
                .excessStartDate(null)
                .excessEndDate(null)
                .totalEOLMonths(0)
                .build();
    }

    // ================================================================
    // INNER CLASSES
    // ================================================================

    @lombok.Builder
    @lombok.Getter
    private static class ExcessCalculationResult {
        private final boolean isEligible;
        private final BigDecimal totalExcessAmount;
        private final BigDecimal totalExcessContributions;
        private final BigDecimal totalExcessInterest;
        private final int eolMonthsInExcess;
        private final LocalDate cutoffServiceDate;
        private final int cutoffYears;
        private final LocalDate excessStartDate;
        private final LocalDate excessEndDate;
        private final int totalEOLMonths;
        private final List<MemberContributionSummary.ExcessYearDetail> yearDetails;
        private final List<MemberContributionSummary.ExcessMonthlyDetail> monthlyDetails;
    }
}