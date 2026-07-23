package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.others.CutoffServiceMaster;
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
    private final CutoffServiceMasterRepository cutoffServiceMasterRepository;
    private final ArrConfigurationRepository arrRepo;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final int TRANSITION_YEAR = 2022;

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
            // STEP 1: READ FROM CONTRIBUTION_BIFURCATION_DETAIL
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
            // STEP 3: GET RATE AND YEAR BASIS
            // ================================================================
            String currentAccountingYear = getAccountingYearForDate(asOfDate);
            ArrConfiguration arrConfig = getArrConfiguration(currentAccountingYear);

            BigDecimal rate = BigDecimal.ZERO;
            int yearBasis = 365;

            if (arrConfig != null) {
                rate = arrConfig.getArrRate() != null ? arrConfig.getArrRate() : BigDecimal.ZERO;
                if (arrConfig.getYearStartDate() != null && arrConfig.getYearEndDate() != null) {
                    long days = ChronoUnit.DAYS.between(
                            arrConfig.getYearStartDate(),
                            arrConfig.getYearEndDate()) + 1;
                    yearBasis = (int) days;
                }
                log.info("Interest Rate for {}: {}%, Year Basis: {} ({} to {})",
                        currentAccountingYear, rate, yearBasis,
                        arrConfig.getYearStartDate(), arrConfig.getYearEndDate());
            } else {
                log.warn("No ARR configuration found for year: {}, using defaults", currentAccountingYear);
                rate = BigDecimal.valueOf(6.5);
                yearBasis = 365;
            }

            log.info("Final Interest Rate: {}%, Year Basis: {}", rate, yearBasis);

            // ================================================================
            // STEP 4: GET PREVIOUS YEARS' DATA FROM SNAPSHOT
            // ================================================================
            // Data from previous years (before current year) is FROZEN from snapshot
            // This includes both principal and interest from all years before current year
            MemberBalanceSnapshot previousSnapshot = getPreviousYearSnapshot(cid, nppfNumber, asOfDate.getYear());

            // Initialize component totals - will be populated from snapshot for previous years
            BigDecimal openingBalancePfEc = BigDecimal.ZERO;
            BigDecimal openingBalancePfMc = BigDecimal.ZERO;
            BigDecimal openingBalancePensionEc = BigDecimal.ZERO;
            BigDecimal openingBalancePensionMc = BigDecimal.ZERO;
            BigDecimal openingBalanceGc = BigDecimal.ZERO;
            BigDecimal openingBalanceVc = BigDecimal.ZERO;

            // Previous years' interest (already calculated and frozen)
            BigDecimal previousInterestPfEc = BigDecimal.ZERO;
            BigDecimal previousInterestPfMc = BigDecimal.ZERO;
            BigDecimal previousInterestPensionEc = BigDecimal.ZERO;
            BigDecimal previousInterestPensionMc = BigDecimal.ZERO;
            BigDecimal previousInterestGc = BigDecimal.ZERO;
            BigDecimal previousInterestVc = BigDecimal.ZERO;

            // ================================================================
            // STEP 5: GET PREVIOUS YEARS' OPENING BALANCE FROM SNAPSHOT
            // ================================================================
            if (previousSnapshot != null) {
                log.info("Using previous years' data from snapshot: {}", previousSnapshot.getAccountingYear());

                // Opening balance from previous years (frozen data)
                openingBalancePfEc = n(previousSnapshot.getPfEc());
                openingBalancePfMc = n(previousSnapshot.getPfMc());
                openingBalancePensionEc = n(previousSnapshot.getPensionEc());
                openingBalancePensionMc = n(BigDecimal.valueOf(0.0));
                openingBalanceGc = n(previousSnapshot.getGc());
                openingBalanceVc = n(previousSnapshot.getVc());

                // Previous years' interest (already calculated and frozen)
                previousInterestPfEc = n(previousSnapshot.getInterestEc());
                previousInterestPfMc = n(previousSnapshot.getInterestMc());
                previousInterestPensionEc = n(previousSnapshot.getInterestPension());
                previousInterestPensionMc = n(previousSnapshot.getInterestPension());
                previousInterestGc = n(previousSnapshot.getInterestGc());
                previousInterestVc = n(previousSnapshot.getInterestVc());

                log.info("Opening Balance from snapshot - PF_EC: {}, PF_MC: {}, PENSION_EC: {}, PENSION_MC: {}",
                        openingBalancePfEc, openingBalancePfMc, openingBalancePensionEc, openingBalancePensionMc);
                log.info("Previous Interest - PF_EC: {}, PF_MC: {}, PENSION_EC: {}, PENSION_MC: {}",
                        previousInterestPfEc, previousInterestPfMc, previousInterestPensionEc, previousInterestPensionMc);
            } else {
                log.info("No previous snapshot found - this is the first year for the member");
            }

            // ================================================================
            // STEP 6: CALCULATE CURRENT YEAR INTEREST ON OPENING BALANCE
            // ================================================================
            // Current year interest on opening balance = Opening Balance × Rate × Days / YearBasis
            // Days = from Jan 1 of current year to asOfDate
            LocalDate yearStart = LocalDate.of(asOfDate.getYear(), 1, 1);
            long daysInCurrentYear = ChronoUnit.DAYS.between(yearStart, asOfDate);

            BigDecimal interestOnOpeningPfEc = calculateInterestOnBalance(
                    openingBalancePfEc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningPfMc = calculateInterestOnBalance(
                    openingBalancePfMc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningPensionEc = calculateInterestOnBalance(
                    openingBalancePensionEc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningPensionMc = calculateInterestOnBalance(
                    openingBalancePensionMc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningGc = calculateInterestOnBalance(
                    openingBalanceGc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningVc = calculateInterestOnBalance(
                    openingBalanceVc, rate, daysInCurrentYear, yearBasis);

            log.info("Interest on Opening Balances for current year ({} days from Jan 1 to asOfDate):", daysInCurrentYear);
            log.info("  PF_EC: {}", interestOnOpeningPfEc);
            log.info("  PF_MC: {}", interestOnOpeningPfMc);
            log.info("  PENSION_EC: {}", interestOnOpeningPensionEc);
            log.info("  PENSION_MC: {}", interestOnOpeningPensionMc);
            log.info("  GC: {}", interestOnOpeningGc);
            log.info("  VC: {}", interestOnOpeningVc);

            // ================================================================
            // STEP 7: CALCULATE CURRENT YEAR CONTRIBUTIONS AND THEIR INTEREST
            // ================================================================
            // Current year contributions are read from ContributionBifurcationDetail table
            // All current year contributions earn interest from Jan 1 to asOfDate
            BigDecimal currentYearPfEc = BigDecimal.ZERO;
            BigDecimal currentYearPfMc = BigDecimal.ZERO;
            BigDecimal currentYearPensionEc = BigDecimal.ZERO;
            BigDecimal currentYearPensionMc = BigDecimal.ZERO;
            BigDecimal currentYearGc = BigDecimal.ZERO;
            BigDecimal currentYearVc = BigDecimal.ZERO;

            BigDecimal interestOnCurrentYearPfEc = BigDecimal.ZERO;
            BigDecimal interestOnCurrentYearPfMc = BigDecimal.ZERO;
            BigDecimal interestOnCurrentYearPensionEc = BigDecimal.ZERO;
            BigDecimal interestOnCurrentYearPensionMc = BigDecimal.ZERO;
            BigDecimal interestOnCurrentYearGc = BigDecimal.ZERO;
            BigDecimal interestOnCurrentYearVc = BigDecimal.ZERO;

            // ✅ All current year contributions earn interest from Jan 1 to asOfDate
            // NOT from contribution date to asOfDate
            long daysForCurrentYearContributions = daysInCurrentYear;
            log.info("Using {} days for current year contributions interest (Jan 1 to {})",
                    daysForCurrentYearContributions, asOfDate);

            for (ContributionBifurcationDetail contrib : allContributions) {
                String status = contrib.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    continue;
                }

                LocalDate contribDate = contrib.getCreatedAt().toLocalDate();

                // Only process current year contributions
                if (contribDate.getYear() != asOfDate.getYear()) {
                    continue;
                }

                // PF_EC
                BigDecimal pfEc = contrib.getPfEc() != null ? contrib.getPfEc() : BigDecimal.ZERO;
                if (pfEc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearPfEc = currentYearPfEc.add(pfEc);
                    interestOnCurrentYearPfEc = interestOnCurrentYearPfEc.add(
                            calculateInterestOnBalance(pfEc, rate, daysForCurrentYearContributions, yearBasis));
                }

                // PF_MC
                BigDecimal pfMc = contrib.getPfMc() != null ? contrib.getPfMc() : BigDecimal.ZERO;
                if (pfMc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearPfMc = currentYearPfMc.add(pfMc);
                    interestOnCurrentYearPfMc = interestOnCurrentYearPfMc.add(
                            calculateInterestOnBalance(pfMc, rate, daysForCurrentYearContributions, yearBasis));
                }

                // PENSION_EC
                BigDecimal pensionEc = contrib.getPensionEc() != null ? contrib.getPensionEc() : BigDecimal.ZERO;
                if (pensionEc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearPensionEc = currentYearPensionEc.add(pensionEc);
                    interestOnCurrentYearPensionEc = interestOnCurrentYearPensionEc.add(
                            calculateInterestOnBalance(pensionEc, rate, daysForCurrentYearContributions, yearBasis));
                }

                // PENSION_MC
                BigDecimal pensionMc = contrib.getPensionMc() != null ? contrib.getPensionMc() : BigDecimal.ZERO;
                if (pensionMc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearPensionMc = currentYearPensionMc.add(pensionMc);
                    interestOnCurrentYearPensionMc = interestOnCurrentYearPensionMc.add(
                            calculateInterestOnBalance(pensionMc, rate, daysForCurrentYearContributions, yearBasis));
                }

                // GC
                BigDecimal gc = contrib.getGc() != null ? contrib.getGc() : BigDecimal.ZERO;
                if (gc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearGc = currentYearGc.add(gc);
                    interestOnCurrentYearGc = interestOnCurrentYearGc.add(
                            calculateInterestOnBalance(gc, rate, daysForCurrentYearContributions, yearBasis));
                }

                // VC
                BigDecimal vc = contrib.getVc() != null ? contrib.getVc() : BigDecimal.ZERO;
                if (vc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearVc = currentYearVc.add(vc);
                    interestOnCurrentYearVc = interestOnCurrentYearVc.add(
                            calculateInterestOnBalance(vc, rate, daysForCurrentYearContributions, yearBasis));
                }
            }

            log.info("Current Year Contributions:");
            log.info("  PF_EC: {} (Interest: {})", currentYearPfEc, interestOnCurrentYearPfEc);
            log.info("  PF_MC: {} (Interest: {})", currentYearPfMc, interestOnCurrentYearPfMc);
            log.info("  PENSION_EC: {} (Interest: {})", currentYearPensionEc, interestOnCurrentYearPensionEc);
            log.info("  PENSION_MC: {} (Interest: {})", currentYearPensionMc, interestOnCurrentYearPensionMc);
            log.info("  GC: {} (Interest: {})", currentYearGc, interestOnCurrentYearGc);
            log.info("  VC: {} (Interest: {})", currentYearVc, interestOnCurrentYearVc);

            // ================================================================
            // STEP 8: CALCULATE FINAL TOTALS
            // ================================================================
            // FINAL TOTAL = Previous Years (from snapshot) + Current Year (recalculated)

            // Principal totals
            BigDecimal finalPfEc = openingBalancePfEc.add(currentYearPfEc);
            BigDecimal finalPfMc = openingBalancePfMc.add(currentYearPfMc);
            BigDecimal finalPensionEc = openingBalancePensionEc.add(currentYearPensionEc);
            BigDecimal finalPensionMc = openingBalancePensionMc.add(currentYearPensionMc);
            BigDecimal finalGc = openingBalanceGc.add(currentYearGc);
            BigDecimal finalVc = openingBalanceVc.add(currentYearVc);

            // Interest totals
            BigDecimal finalInterestPfEc = previousInterestPfEc
                    .add(interestOnOpeningPfEc)
                    .add(interestOnCurrentYearPfEc);
            BigDecimal finalInterestPfMc = previousInterestPfMc
                    .add(interestOnOpeningPfMc)
                    .add(interestOnCurrentYearPfMc);
            BigDecimal finalInterestPensionEc = previousInterestPensionEc
                    .add(interestOnOpeningPensionEc)
                    .add(interestOnCurrentYearPensionEc);
            BigDecimal finalInterestPensionMc = previousInterestPensionMc
                    .add(interestOnOpeningPensionMc)
                    .add(interestOnCurrentYearPensionMc);
            BigDecimal finalInterestGc = previousInterestGc
                    .add(interestOnOpeningGc)
                    .add(interestOnCurrentYearGc);
            BigDecimal finalInterestVc = previousInterestVc
                    .add(interestOnOpeningVc)
                    .add(interestOnCurrentYearVc);

            log.info("========== FINAL TOTALS ==========");
            log.info("PF_EC: Principal={}, Interest={}, Total={}",
                    finalPfEc, finalInterestPfEc, finalPfEc.add(finalInterestPfEc));
            log.info("PF_MC: Principal={}, Interest={}, Total={}",
                    finalPfMc, finalInterestPfMc, finalPfMc.add(finalInterestPfMc));
            log.info("PENSION_EC: Principal={}, Interest={}, Total={}",
                    finalPensionEc, finalInterestPensionEc, finalPensionEc.add(finalInterestPensionEc));
            log.info("PENSION_MC: Principal={}, Interest={}, Total={}",
                    finalPensionMc, finalInterestPensionMc, finalPensionMc.add(finalInterestPensionMc));
            log.info("GC: Principal={}, Interest={}, Total={}",
                    finalGc, finalInterestGc, finalGc.add(finalInterestGc));
            log.info("VC: Principal={}, Interest={}, Total={}",
                    finalVc, finalInterestVc, finalVc.add(finalInterestVc));

            // ================================================================
            // STEP 9: BUILD COMPONENT GROUPS
            // ================================================================
            List<MemberContributionSummary.ComponentGroup> componentGroups = new ArrayList<>();

            // PF_EC - Principal
            if (finalPfEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_EC", "Employee PF Contribution",
                        finalPfEc, BigDecimal.ZERO));
            }

            // PF_IEC - Interest on PF_EC
            if (finalInterestPfEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_IEC", "Interest on Employee PF",
                        BigDecimal.ZERO, finalInterestPfEc));
            }

            // PF_MC - Principal
            if (finalPfMc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_MC", "Employer PF Contribution",
                        finalPfMc, BigDecimal.ZERO));
            }

            // PF_IMC - Interest on PF_MC
            if (finalInterestPfMc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_IMC", "Interest on Employer PF",
                        BigDecimal.ZERO, finalInterestPfMc));
            }

            // P_EC - Employer Pension Principal
            if (finalPensionEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("P_EC", "Employer Pension Contribution",
                        finalPensionEc, BigDecimal.ZERO));
            }

            // P_IEC - Interest on Employer Pension
            if (finalInterestPensionEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("P_IEC", "Interest on Employer Pension",
                        BigDecimal.ZERO, finalInterestPensionEc));
            }

            // P_MC - Member Pension Principal
            if (finalPensionMc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("P_MC", "Member Pension Contribution",
                        finalPensionMc, BigDecimal.ZERO));
            }

            // P_IMC - Interest on Member Pension
            if (finalInterestPensionMc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("P_IMC", "Interest on Member Pension",
                        BigDecimal.ZERO, finalInterestPensionMc));
            }

            // GC - Principal
            if (finalGc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("GC", "Government Contribution",
                        finalGc, BigDecimal.ZERO));
            }

            // IGC - Interest on GC
            if (finalInterestGc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("IGC", "Interest on Government",
                        BigDecimal.ZERO, finalInterestGc));
            }

            // VC - Principal
            if (finalVc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("VC", "Voluntary Contribution",
                        finalVc, BigDecimal.ZERO));
            }

            // IVC - Interest on VC
            if (finalInterestVc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("IVC", "Interest on Voluntary",
                        BigDecimal.ZERO, finalInterestVc));
            }

            // ================================================================
            // STEP 10: CALCULATE TOTALS
            // ================================================================
            BigDecimal totalPrincipal = finalPfEc.add(finalPfMc).add(finalPensionEc).add(finalPensionMc).add(finalGc).add(finalVc);
            BigDecimal totalInterest = finalInterestPfEc.add(finalInterestPfMc)
                    .add(finalInterestPensionEc).add(finalInterestPensionMc)
                    .add(finalInterestGc).add(finalInterestVc);
            BigDecimal totalBalance = totalPrincipal.add(totalInterest);

            log.info("========== FINAL SUMMARY ==========");
            log.info("Total Principal: {}", totalPrincipal);
            log.info("Total Interest: {}", totalInterest);
            log.info("Grand Total: {}", totalBalance);
            log.info("Components: {}", componentGroups.size());

            // ================================================================
            // STEP 11: BUILD SUMMARY
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
                    .totalBalance(totalBalance)
                    .componentGroups(componentGroups)
                    .asOfDate(asOfDate)
                    .currentAccountingYear(currentAccountingYear)
                    .openingBalanceFromSnapshot(previousSnapshot != null ? totalPrincipal : BigDecimal.ZERO);

            // ================================================================
            // STEP 12: EXCESS SERVICE
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

            return builder.build();

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            throw ClaimException.internalError("Failed to fetch contribution summary: " + e.getMessage());
        }
    }

    // ================================================================
    // HELPER METHODS
    // ================================================================

    /**
     * Get the previous year's snapshot (latest snapshot before current year)
     */
    private MemberBalanceSnapshot getPreviousYearSnapshot(String cid, String nppfNumber, int currentYear) {
        List<MemberBalanceSnapshot> snapshots = snapshotRepo
                .findByCidAndNppfNumberOrderByAccountingYearDesc(cid, nppfNumber);

        if (snapshots.isEmpty()) {
            log.info("No previous snapshots found for member");
            return null;
        }

        for (MemberBalanceSnapshot snapshot : snapshots) {
            if (snapshot.getAccountingYear() != null) {
                String yearStr = snapshot.getAccountingYear().split("-")[0];
                try {
                    int snapshotYear = Integer.parseInt(yearStr);
                    if (snapshotYear < currentYear) {
                        log.info("Found previous snapshot for year: {}", snapshot.getAccountingYear());
                        return snapshot;
                    }
                } catch (NumberFormatException e) {
                    log.warn("Could not parse year from: {}", snapshot.getAccountingYear());
                }
            }
        }

        return null;
    }

    /**
     * Calculate interest on a balance
     * Formula: Balance × Rate × Days / YearBasis
     */
    private BigDecimal calculateInterestOnBalance(BigDecimal balance, BigDecimal rate, long days, int yearBasis) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) == 0 || days <= 0) {
            return BigDecimal.ZERO;
        }

        // Rate is a percentage (0.8 = 0.8%), so divide by 100
        BigDecimal dailyRate = rate.divide(BigDecimal.valueOf(100), 10, RM)
                .divide(BigDecimal.valueOf(yearBasis), 10, RM);

        return balance.multiply(dailyRate)
                .multiply(BigDecimal.valueOf(days))
                .setScale(2, RM);
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
     * Get ARR configuration with fallback
     */
    private ArrConfiguration getArrConfiguration(String accountingYear) {
        if (accountingYear == null) {
            return null;
        }

        // 1. Try current year
        Optional<ArrConfiguration> arrOpt = arrRepo.findByAccountingYear(accountingYear);
        if (arrOpt.isPresent()) {
            log.info("Using ARR configuration for current year: {}", accountingYear);
            return arrOpt.get();
        }

        // 2. Try previous years (up to 5 years back)
        log.warn("ARR configuration not found for year: {}, trying previous years", accountingYear);
        try {
            int year = Integer.parseInt(accountingYear.trim());
            for (int i = 1; i <= 5; i++) {
                String previousYear = String.valueOf(year - i);
                Optional<ArrConfiguration> prevArrOpt = arrRepo.findByAccountingYear(previousYear);
                if (prevArrOpt.isPresent()) {
                    log.info("Using ARR configuration from previous year: {} ({} years back)", previousYear, i);
                    return prevArrOpt.get();
                }
            }
        } catch (NumberFormatException e) {
            log.warn("Could not parse accounting year: {}", accountingYear);
        }

        // 3. Try latest available
        log.warn("No ARR configuration found for year: {} or previous 5 years, trying latest available",
                accountingYear);
        List<ArrConfiguration> allArr = arrRepo.findAll();
        if (!allArr.isEmpty()) {
            allArr.sort((a, b) -> b.getAccountingYear().compareTo(a.getAccountingYear()));
            ArrConfiguration latest = allArr.get(0);
            log.info("Using latest available ARR configuration from year: {}", latest.getAccountingYear());
            return latest;
        }

        log.warn("No ARR configuration found in database");
        return null;
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
        ArrConfiguration arrConfig = getArrConfiguration(accountingYear);

        BigDecimal rate = BigDecimal.ZERO;
        int yearBasis = 365;

        if (arrConfig != null) {
            rate = arrConfig.getArrRate() != null ? arrConfig.getArrRate() : BigDecimal.ZERO;
            if (arrConfig.getYearStartDate() != null && arrConfig.getYearEndDate() != null) {
                long days = ChronoUnit.DAYS.between(
                        arrConfig.getYearStartDate(),
                        arrConfig.getYearEndDate()) + 1;
                yearBasis = (int) days;
            }
        }

        // ✅ Calculate days from year start to excess end
        long daysHeld = 0;
        if (arrConfig != null && arrConfig.getYearStartDate() != null && excessEnd != null) {
            daysHeld = ChronoUnit.DAYS.between(arrConfig.getYearStartDate(), excessEnd);
        }

        for (ContributionBifurcationDetail contrib : excessContributions) {
            BigDecimal principal = getTotalContributionAmount(contrib);
            totalExcessContributions = totalExcessContributions.add(principal);

            if (rate.compareTo(BigDecimal.ZERO) > 0 && yearBasis > 0 && daysHeld > 0) {
                totalExcessInterest = totalExcessInterest.add(
                        calculateInterestOnBalance(principal, rate, daysHeld, yearBasis));
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

    private BigDecimal getTotalContributionAmount(ContributionBifurcationDetail contrib) {
        return n(contrib.getPfEc())
                .add(n(contrib.getPfMc()))
                .add(n(contrib.getPensionEc()))
                .add(n(contrib.getGc()))
                .add(n(contrib.getVc()));
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