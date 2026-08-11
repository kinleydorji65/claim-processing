package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.repository.others.CutoffServiceMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.dto.ExcessServiceResultDto;
import com.claim.claim_processing.integration.contribution.entity.ArrConfiguration;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationHeader;
import com.claim.claim_processing.integration.contribution.entity.MemberBalanceSnapshot;
import com.claim.claim_processing.integration.contribution.repository.ArrConfigurationRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationDetailRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationHeaderRepository;
import com.claim.claim_processing.integration.contribution.repository.MemberBalanceSnapshotRepository;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for Member Contribution Management
 * 
 * ALL calculations are derived from Contribution Bifurcation Detail and Header tables ONLY.
 * NO dependency on MemberBalanceSnapshot table.
 * 
 * This service handles all contribution-related calculations including:
 * - Fetching and aggregating member contributions
 * - Calculating PF (Provident Fund) and Pension balances
 * - Computing interest on contributions using ARR (Annual Rate of Return)
 * - Handling excess service months for pension calculations
 * - Calculating contribution and non-contribution months
 * 
 * Key Concepts:
 * - PF (Provident Fund): Employee and Employer contributions with interest
 * - Pension: Member and Employer pension contributions with interest
 * - GC (Government Contribution): Government contributions to PF
 * - VC (Voluntary Contribution): Voluntary contributions by member
 * - ARR (Annual Rate of Return): Interest rate applied to balances
 * - Excess Service: Service months that should be excluded from pension
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberContributionServiceImpl implements MemberContributionService {

    // ================================================================
    // DEPENDENCY INJECTIONS
    // ================================================================
    
    private final MemberBalanceSnapshotRepository snapshotRepo;
    private final ContributionBifurcationDetailRepository contributionDetailRepo;
    private final ContributionBifurcationHeaderRepository contributionHeaderRepo;
    private final ArrConfigurationRepository arrRepo;
    private final ExcessServiceCalculator excessServiceCalculator;

    // ================================================================
    // CONSTANTS
    // ================================================================
    
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final int TRANSITION_YEAR = 2022;

    private static final Map<String, Integer> MONTH_NAME_TO_NUMBER = new HashMap<>();
    static {
        MONTH_NAME_TO_NUMBER.put("JANUARY", 1);
        MONTH_NAME_TO_NUMBER.put("FEBRUARY", 2);
        MONTH_NAME_TO_NUMBER.put("MARCH", 3);
        MONTH_NAME_TO_NUMBER.put("APRIL", 4);
        MONTH_NAME_TO_NUMBER.put("MAY", 5);
        MONTH_NAME_TO_NUMBER.put("JUNE", 6);
        MONTH_NAME_TO_NUMBER.put("JULY", 7);
        MONTH_NAME_TO_NUMBER.put("AUGUST", 8);
        MONTH_NAME_TO_NUMBER.put("SEPTEMBER", 9);
        MONTH_NAME_TO_NUMBER.put("OCTOBER", 10);
        MONTH_NAME_TO_NUMBER.put("NOVEMBER", 11);
        MONTH_NAME_TO_NUMBER.put("DECEMBER", 12);
    }

    // ================================================================
    // MAIN PUBLIC METHODS
    // ================================================================

    @Override
    public MemberContributionSummary getContributionSummary(
            MemberDetailResponseDto memberDetail,
            LocalDate relieveDate) {

        LocalDate asOfDate = LocalDate.now();
        log.info("🔍 Calculating contributions as of current date: {}", asOfDate);
        return getContributionSummary(memberDetail, relieveDate, asOfDate);
    }

    /**
     * Get contribution summary for a member as of a specific date
     * ALL calculations are derived from Contribution Bifurcation Detail and Header tables ONLY
     * NO dependency on MemberBalanceSnapshot
     */
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
            // STEP 1: FETCH ALL POSTED CONTRIBUTIONS FROM BIFURCATION DETAIL
            // ================================================================
            List<ContributionBifurcationDetail> allContributions = contributionDetailRepo
                    .findByCidAndNppfNumberAndPostingStatusOrderByCreatedAtAsc(cid, nppfNumber, "POSTED");

            if (allContributions.isEmpty()) {
                log.error("❌ No contributions found for member: {}", nppfNumber);
                throw ClaimException.notFound("No contributions found for member: " + nppfNumber);
            }

            log.info("✅ Found {} contribution records", allContributions.size());

            // ================================================================
            // STEP 2: GET HEADER INFO FROM BIFURCATION HEADER TABLE
            // ================================================================
            Map<Long, ContributionHeaderInfo> headerInfoMap = getHeaderInfoForContributions(allContributions);
            log.info("Found header info for {} contributions", headerInfoMap.size());

            // ================================================================
            // STEP 3: GET LAST CONTRIBUTION DATE
            // ================================================================
            LocalDate lastContributionDate = getLastContributionDate(allContributions, headerInfoMap);
            log.info("Last Contribution Date: {}", lastContributionDate);

            // ================================================================
            // STEP 4: GET INTEREST RATE AND YEAR BASIS
            // ================================================================
            String currentAccountingYear = getAccountingYearForDate(asOfDate);
            log.info("Current Accounting Year: {}", currentAccountingYear);
            
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
                log.info("Interest Rate: {}%, Year Basis: {}", rate, yearBasis);
            } else {
                log.warn("⚠️ No ARR configuration found, using defaults");
                rate = BigDecimal.valueOf(6.5);
                yearBasis = 365;
            }

            // ================================================================
            // STEP 5: CALCULATE PF OPENING BALANCES FROM CONTRIBUTIONS
            // ================================================================
            PfOpeningBalanceResult pfResult = calculatePfOpeningBalanceFromContributions(
                allContributions, headerInfoMap, asOfDate.getYear(), rate, yearBasis);
            
            BigDecimal openingBalancePfEc = pfResult.getPfEc();
            BigDecimal openingBalancePfMc = pfResult.getPfMc();
            BigDecimal previousInterestPfEc = pfResult.getInterestPfEc();
            BigDecimal previousInterestPfMc = pfResult.getInterestPfMc();

            // ================================================================
            // STEP 6: CALCULATE GC & VC OPENING BALANCES FROM CONTRIBUTIONS
            // ================================================================
            GcVcOpeningBalanceResult gcVcResult = calculateGcVcOpeningBalanceFromContributions(
                allContributions, headerInfoMap, asOfDate.getYear(), rate, yearBasis);
            
            BigDecimal openingBalanceGc = gcVcResult.getGc();
            BigDecimal openingBalanceVc = gcVcResult.getVc();
            BigDecimal previousInterestGc = gcVcResult.getInterestGc();
            BigDecimal previousInterestVc = gcVcResult.getInterestVc();

            // ================================================================
            // STEP 7: CALCULATE INTEREST ON OPENING BALANCES
            // ================================================================
            LocalDate yearStart = LocalDate.of(asOfDate.getYear(), 1, 1);
            long daysInCurrentYear = ChronoUnit.DAYS.between(yearStart, asOfDate);

            BigDecimal interestOnOpeningPfEc = calculateInterestOnBalance(
                    openingBalancePfEc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningPfMc = calculateInterestOnBalance(
                    openingBalancePfMc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningGc = calculateInterestOnBalance(
                    openingBalanceGc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningVc = calculateInterestOnBalance(
                    openingBalanceVc, rate, daysInCurrentYear, yearBasis);

            // ================================================================
            // STEP 8: CALCULATE CURRENT YEAR CONTRIBUTIONS
            // ================================================================
            CurrentYearContributionResult currentYearResult = calculateCurrentYearContributions(
                allContributions, headerInfoMap, asOfDate, rate, yearBasis);
            
            BigDecimal currentYearPfEc = currentYearResult.getPfEc();
            BigDecimal currentYearPfMc = currentYearResult.getPfMc();
            BigDecimal currentYearPensionEc = currentYearResult.getPensionEc();
            BigDecimal currentYearPensionMc = currentYearResult.getPensionMc();
            BigDecimal currentYearGc = currentYearResult.getGc();
            BigDecimal currentYearVc = currentYearResult.getVc();
            BigDecimal interestOnCurrentYearPfEc = currentYearResult.getInterestPfEc();
            BigDecimal interestOnCurrentYearPfMc = currentYearResult.getInterestPfMc();
            BigDecimal interestOnCurrentYearPensionEc = currentYearResult.getInterestPensionEc();
            BigDecimal interestOnCurrentYearPensionMc = currentYearResult.getInterestPensionMc();
            BigDecimal interestOnCurrentYearGc = currentYearResult.getInterestGc();
            BigDecimal interestOnCurrentYearVc = currentYearResult.getInterestVc();

            // ================================================================
            // STEP 9: CHECK FOR EXCESS SERVICE
            // ================================================================
            ExcessServiceResultDto excessResult = null;
            List<YearMonth> monthsToExclude = new ArrayList<>();
            PensionRebuildResult rebuildResult = null;
            boolean hasExcessService = false;

            BigDecimal openingBalancePensionEc = BigDecimal.ZERO;
            BigDecimal openingBalancePensionMc = BigDecimal.ZERO;
            BigDecimal openingBalancePensionIec = BigDecimal.ZERO;
            BigDecimal openingBalancePensionImc = BigDecimal.ZERO;

            if (relieveDate != null && isPensionEligible(memberDetail)) {
                log.info("=== Checking for Excess Service ===");
                try {
                    excessResult = excessServiceCalculator.calculateExcessService(memberDetail);
                    if (excessResult != null && excessResult.isEligible()) {
                        log.info("✅ Excess Service found");
                        hasExcessService = true;
                        
                        LocalDate excessStartDate = excessResult.getExcessStartDate();
                        
                        OpeningBalanceResult openingResult = calculateOpeningBalanceFromAllContributions(
                            allContributions, headerInfoMap, excessStartDate, rate, yearBasis, asOfDate);
                        
                        openingBalancePensionEc = openingResult.getPensionEc();
                        openingBalancePensionMc = openingResult.getPensionMc();
                        openingBalancePensionIec = openingResult.getInterestPensionEc();
                        openingBalancePensionImc = openingResult.getInterestPensionMc();
                        
                        monthsToExclude = getMonthsToExcludeForExcess(excessResult, asOfDate);
                        
                        if (!monthsToExclude.isEmpty()) {
                            rebuildResult = rebuildPensionWithoutExcess(
                                allContributions, headerInfoMap, monthsToExclude,
                                openingBalancePensionEc, openingBalancePensionMc,
                                openingBalancePensionIec, openingBalancePensionImc,
                                rate, yearBasis, asOfDate);
                        }
                    }
                } catch (Exception e) {
                    log.error("❌ Error calculating excess service: {}", e.getMessage(), e);
                }
            }

            // If no excess service, calculate pension opening from contributions
            if (!hasExcessService) {
                LocalDate currentYearStart = LocalDate.of(asOfDate.getYear(), 1, 1);
                OpeningBalanceResult pensionResult = calculateOpeningBalanceFromAllContributions(
                    allContributions, headerInfoMap, currentYearStart, rate, yearBasis, asOfDate);
                
                openingBalancePensionEc = pensionResult.getPensionEc();
                openingBalancePensionMc = pensionResult.getPensionMc();
                openingBalancePensionIec = pensionResult.getInterestPensionEc();
                openingBalancePensionImc = pensionResult.getInterestPensionMc();
            }

            // ================================================================
            // STEP 10: CALCULATE FINAL TOTALS
            // ================================================================
            
            BigDecimal finalPfEc = openingBalancePfEc.add(currentYearPfEc);
            BigDecimal finalPfMc = openingBalancePfMc.add(currentYearPfMc);
            
            BigDecimal finalPensionEc;
            BigDecimal finalPensionMc;
            BigDecimal finalInterestPensionEc;
            BigDecimal finalInterestPensionMc;
            
            if (hasExcessService && rebuildResult != null) {
                finalPensionEc = rebuildResult.getAdjustedPensionEc();
                finalPensionMc = rebuildResult.getAdjustedPensionMc();
                finalInterestPensionEc = rebuildResult.getAdjustedPensionIec();
                finalInterestPensionMc = rebuildResult.getAdjustedPensionImc();
            } else {
                finalPensionEc = openingBalancePensionEc.add(currentYearPensionEc);
                finalPensionMc = openingBalancePensionMc.add(currentYearPensionMc);
                finalInterestPensionEc = openingBalancePensionIec.add(interestOnCurrentYearPensionEc);
                finalInterestPensionMc = openingBalancePensionImc.add(interestOnCurrentYearPensionMc);
            }
            
            BigDecimal finalGc = openingBalanceGc.add(currentYearGc);
            BigDecimal finalVc = openingBalanceVc.add(currentYearVc);
            
            BigDecimal finalInterestPfEc = previousInterestPfEc
                    .add(interestOnOpeningPfEc)
                    .add(interestOnCurrentYearPfEc);
            BigDecimal finalInterestPfMc = previousInterestPfMc
                    .add(interestOnOpeningPfMc)
                    .add(interestOnCurrentYearPfMc);
            BigDecimal finalInterestGc = previousInterestGc
                    .add(interestOnOpeningGc)
                    .add(interestOnCurrentYearGc);
            BigDecimal finalInterestVc = previousInterestVc
                    .add(interestOnOpeningVc)
                    .add(interestOnCurrentYearVc);

            // ================================================================
            // STEP 11: BUILD COMPONENT GROUPS
            // ================================================================
            List<MemberContributionSummary.ComponentGroup> componentGroups = new ArrayList<>();

            // PF Components
            if (finalPfEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_EC", "Employee PF Contribution",
                        finalPfEc, BigDecimal.ZERO));
            }
            if (finalInterestPfEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_IEC", "Interest on Employee PF",
                        BigDecimal.ZERO, finalInterestPfEc));
            }
            if (finalPfMc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_MC", "Employer PF Contribution",
                        finalPfMc, BigDecimal.ZERO));
            }
            if (finalInterestPfMc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("PF_IMC", "Interest on Employer PF",
                        BigDecimal.ZERO, finalInterestPfMc));
            }
            
            // Pension Components
            if (finalPensionEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("P_EC", "Employer Pension Contribution",
                        finalPensionEc, BigDecimal.ZERO));
            }
            if (finalInterestPensionEc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("P_IEC", "Interest on Employer Pension",
                        BigDecimal.ZERO, finalInterestPensionEc));
            }
            if (finalPensionMc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("P_MC", "Member Pension Contribution",
                        finalPensionMc, BigDecimal.ZERO));
            }
            if (finalInterestPensionMc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("P_IMC", "Interest on Member Pension",
                        BigDecimal.ZERO, finalInterestPensionMc));
            }
            
            // GC & VC Components
            if (finalGc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("GC", "Government Contribution",
                        finalGc, BigDecimal.ZERO));
            }
            if (finalInterestGc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("IGC", "Interest on Government",
                        BigDecimal.ZERO, finalInterestGc));
            }
            if (finalVc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("VC", "Voluntary Contribution",
                        finalVc, BigDecimal.ZERO));
            }
            if (finalInterestVc.compareTo(BigDecimal.ZERO) > 0) {
                componentGroups.add(createComponentGroup("IVC", "Interest on Voluntary",
                        BigDecimal.ZERO, finalInterestVc));
            }

            // ================================================================
            // STEP 12: CALCULATE TOTALS
            // ================================================================
            BigDecimal totalPrincipal = finalPfEc.add(finalPfMc)
                    .add(finalPensionEc).add(finalPensionMc)
                    .add(finalGc).add(finalVc);
                    
            BigDecimal totalInterest = finalInterestPfEc.add(finalInterestPfMc)
                    .add(finalInterestPensionEc).add(finalInterestPensionMc)
                    .add(finalInterestGc).add(finalInterestVc);
                    
            BigDecimal totalBalance = totalPrincipal.add(totalInterest);

            // ================================================================
            // STEP 13: BUILD SUMMARY
            // ================================================================
            MemberContributionSummary.MemberContributionSummaryBuilder builder = MemberContributionSummary.builder()
                    .nppfNumber(nppfNumber)
                    .schemeTypeId(getSchemeTypeId(memberDetail))
                    .totalContributionMonths(calculateContributionMonths(allContributions, headerInfoMap, asOfDate))
                    .totalContributionYears(calculateContributionMonths(allContributions, headerInfoMap, asOfDate) / 12)
                    .totalNonContributionMonths(
                            calculateNonContributionMonths(memberDetail, relieveDate, allContributions, headerInfoMap))
                    .contributionEndDate(lastContributionDate)
                    .totalPrincipalAmount(totalPrincipal)
                    .totalInterestAmount(totalInterest)
                    .totalBalance(totalBalance)
                    .componentGroups(componentGroups)
                    .asOfDate(asOfDate)
                    .currentAccountingYear(currentAccountingYear)
                    .rate(rate)
                    
                    // PF Opening Balances (ALL from contributions)
                    .openingPfMc(openingBalancePfMc)
                    .openingPfEc(openingBalancePfEc)
                    .openingPfImc(previousInterestPfMc)
                    .openingPfIec(previousInterestPfEc)
                    
                    // Pension Opening Balances (ALL from contributions)
                    .openingPMc(hasExcessService && rebuildResult != null 
                            ? rebuildResult.getAdjustedOpeningPmc() 
                            : openingBalancePensionMc)
                    .openingPEc(hasExcessService && rebuildResult != null 
                            ? rebuildResult.getAdjustedOpeningPec() 
                            : openingBalancePensionEc)
                    .openingPImc(hasExcessService && rebuildResult != null 
                            ? rebuildResult.getAdjustedOpeningPimc() 
                            : openingBalancePensionImc)
                    .openingPIec(hasExcessService && rebuildResult != null 
                            ? rebuildResult.getAdjustedOpeningPiec() 
                            : openingBalancePensionIec)
                    
                    .excessService(excessResult);

            return builder.build();

        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage(), e);
            throw ClaimException.internalError("Failed to fetch contribution summary: " + e.getMessage());
        }
    }

    // ================================================================
    // HELPER METHODS - HEADER INFO
    // ================================================================

    private Map<Long, ContributionHeaderInfo> getHeaderInfoForContributions(
            List<ContributionBifurcationDetail> contributions) {

        Map<Long, ContributionHeaderInfo> headerInfoMap = new HashMap<>();

        Set<Long> bifIds = contributions.stream()
                .map(ContributionBifurcationDetail::getBifId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        if (!bifIds.isEmpty()) {
            List<ContributionBifurcationHeader> headers = contributionHeaderRepo.findAllById(bifIds);

            for (ContributionBifurcationHeader header : headers) {
                if (header.getBifId() != null) {
                    String monthName = header.getMonthName();
                    String year = header.getYear();
                    Integer monthNumber = MONTH_NAME_TO_NUMBER.get(monthName.toUpperCase());

                    if (monthNumber != null && year != null) {
                        headerInfoMap.put(header.getBifId(),
                                new ContributionHeaderInfo(monthNumber, Integer.parseInt(year)));
                    }
                }
            }
        }

        return headerInfoMap;
    }

    private LocalDate getContributionDate(
            ContributionBifurcationDetail detail,
            Map<Long, ContributionHeaderInfo> headerInfoMap) {

        ContributionHeaderInfo headerInfo = headerInfoMap.get(detail.getBifId());
        if (headerInfo != null) {
            return LocalDate.of(headerInfo.getYear(), headerInfo.getMonth(), 1);
        }
        return detail.getCreatedAt().toLocalDate();
    }

    private String getMonthKey(
            ContributionBifurcationDetail detail,
            Map<Long, ContributionHeaderInfo> headerInfoMap) {

        ContributionHeaderInfo headerInfo = headerInfoMap.get(detail.getBifId());
        if (headerInfo != null) {
            return headerInfo.getYear() + "-" + String.format("%02d", headerInfo.getMonth());
        }
        LocalDate date = detail.getCreatedAt().toLocalDate();
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }

    // ================================================================
    // HELPER METHODS - OPENING BALANCE CALCULATIONS
    // ================================================================

    /**
     * Calculate PF opening balances from contributions BEFORE current year
     */
    private PfOpeningBalanceResult calculatePfOpeningBalanceFromContributions(
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap,
            int currentYear,
            BigDecimal rate,
            int yearBasis) {
        
        log.info("=== Calculating PF Opening Balance from Contributions ===");
        
        List<ContributionBifurcationDetail> historicalContribs = allContributions.stream()
            .filter(c -> {
                if (c.getCreatedAt() == null) return false;
                String status = c.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    return false;
                }
                LocalDate date = getContributionDate(c, headerInfoMap);
                return date.getYear() < currentYear;
            })
            .sorted(Comparator.comparing(c -> getContributionDate(c, headerInfoMap)))
            .collect(Collectors.toList());
        
        log.info("Found {} historical contributions before {}", historicalContribs.size(), currentYear);
        
        BigDecimal runningPfEc = BigDecimal.ZERO;
        BigDecimal runningPfMc = BigDecimal.ZERO;
        BigDecimal runningPfIec = BigDecimal.ZERO;
        BigDecimal runningPfImc = BigDecimal.ZERO;
        
        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
        
        for (ContributionBifurcationDetail contrib : historicalContribs) {
            LocalDate contribDate = getContributionDate(contrib, headerInfoMap);
            
            LocalDate yearEnd = getYearEndForDate(contribDate);
            long days = ChronoUnit.DAYS.between(contribDate, yearEnd);
            if (days < 0) days = 0;
            
            BigDecimal daysFactor = BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(yearBasis), 10, RM);
            
            BigDecimal pfEc = n(contrib.getPfEc());
            BigDecimal pfMc = n(contrib.getPfMc());
            
            BigDecimal interestOnRunningPfEc = runningPfEc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningPfMc = runningPfMc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningPfIec = runningPfIec.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningPfImc = runningPfImc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            BigDecimal interestOnPfEc = pfEc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnPfMc = pfMc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            runningPfEc = runningPfEc.add(interestOnRunningPfEc).add(pfEc);
            runningPfMc = runningPfMc.add(interestOnRunningPfMc).add(pfMc);
            runningPfIec = runningPfIec.add(interestOnRunningPfIec).add(interestOnPfEc);
            runningPfImc = runningPfImc.add(interestOnRunningPfImc).add(interestOnPfMc);
        }
        
        return PfOpeningBalanceResult.builder()
            .pfEc(runningPfEc)
            .pfMc(runningPfMc)
            .interestPfEc(runningPfIec)
            .interestPfMc(runningPfImc)
            .contributionsProcessed(historicalContribs.size())
            .build();
    }

    /**
     * Calculate GC and VC opening balances from contributions BEFORE current year
     */
    private GcVcOpeningBalanceResult calculateGcVcOpeningBalanceFromContributions(
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap,
            int currentYear,
            BigDecimal rate,
            int yearBasis) {
        
        log.info("=== Calculating GC/VC Opening Balance from Contributions ===");
        
        List<ContributionBifurcationDetail> historicalContribs = allContributions.stream()
            .filter(c -> {
                if (c.getCreatedAt() == null) return false;
                String status = c.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    return false;
                }
                LocalDate date = getContributionDate(c, headerInfoMap);
                return date.getYear() < currentYear;
            })
            .sorted(Comparator.comparing(c -> getContributionDate(c, headerInfoMap)))
            .collect(Collectors.toList());
        
        BigDecimal runningGc = BigDecimal.ZERO;
        BigDecimal runningVc = BigDecimal.ZERO;
        BigDecimal runningIgc = BigDecimal.ZERO;
        BigDecimal runningIvc = BigDecimal.ZERO;
        
        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
        
        for (ContributionBifurcationDetail contrib : historicalContribs) {
            LocalDate contribDate = getContributionDate(contrib, headerInfoMap);
            
            LocalDate yearEnd = getYearEndForDate(contribDate);
            long days = ChronoUnit.DAYS.between(contribDate, yearEnd);
            if (days < 0) days = 0;
            
            BigDecimal daysFactor = BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(yearBasis), 10, RM);
            
            BigDecimal gc = n(contrib.getGc());
            BigDecimal vc = n(contrib.getVc());
            
            BigDecimal interestOnRunningGc = runningGc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningVc = runningVc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningIgc = runningIgc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningIvc = runningIvc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            BigDecimal interestOnGc = gc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnVc = vc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            runningGc = runningGc.add(interestOnRunningGc).add(gc);
            runningVc = runningVc.add(interestOnRunningVc).add(vc);
            runningIgc = runningIgc.add(interestOnRunningIgc).add(interestOnGc);
            runningIvc = runningIvc.add(interestOnRunningIvc).add(interestOnVc);
        }
        
        return GcVcOpeningBalanceResult.builder()
            .gc(runningGc)
            .vc(runningVc)
            .interestGc(runningIgc)
            .interestVc(runningIvc)
            .contributionsProcessed(historicalContribs.size())
            .build();
    }

    /**
     * Calculate opening balance from all contributions before a cutoff date
     */
    private OpeningBalanceResult calculateOpeningBalanceFromAllContributions(
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap,
            LocalDate cutoffDate,
            BigDecimal rate,
            int yearBasis,
            LocalDate asOfDate) {
        
        log.info("=== Calculating Opening Balance BEFORE {}", cutoffDate);
        
        List<ContributionBifurcationDetail> preCutoffContribs = allContributions.stream()
            .filter(c -> {
                if (c.getCreatedAt() == null) return false;
                String status = c.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    return false;
                }
                LocalDate date = getContributionDate(c, headerInfoMap);
                return date.isBefore(cutoffDate);
            })
            .sorted(Comparator.comparing(c -> getContributionDate(c, headerInfoMap)))
            .collect(Collectors.toList());
        
        BigDecimal runningPensionEc = BigDecimal.ZERO;
        BigDecimal runningPensionMc = BigDecimal.ZERO;
        BigDecimal runningPensionIec = BigDecimal.ZERO;
        BigDecimal runningPensionImc = BigDecimal.ZERO;
        
        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
        
        for (ContributionBifurcationDetail contrib : preCutoffContribs) {
            LocalDate contribDate = getContributionDate(contrib, headerInfoMap);
            
            BigDecimal pec = n(contrib.getPensionEc());
            BigDecimal pmc = n(contrib.getPensionMc());
            
            long days = ChronoUnit.DAYS.between(contribDate, cutoffDate);
            if (days < 0) days = 0;
            
            BigDecimal daysFactor = BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(yearBasis), 10, RM);
            
            BigDecimal interestOnRunningPec = runningPensionEc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningPmc = runningPensionMc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningIec = runningPensionIec.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningImc = runningPensionImc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            BigDecimal interestOnPec = pec.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnPmc = pmc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            runningPensionEc = runningPensionEc.add(interestOnRunningPec).add(pec);
            runningPensionMc = runningPensionMc.add(interestOnRunningPmc).add(pmc);
            runningPensionIec = runningPensionIec.add(interestOnRunningIec).add(interestOnPec);
            runningPensionImc = runningPensionImc.add(interestOnRunningImc).add(interestOnPmc);
        }
        
        return OpeningBalanceResult.builder()
            .pensionEc(runningPensionEc)
            .pensionMc(runningPensionMc)
            .interestPensionEc(runningPensionIec)
            .interestPensionMc(runningPensionImc)
            .contributionsProcessed(preCutoffContribs.size())
            .build();
    }

    // ================================================================
    // HELPER METHODS - CURRENT YEAR CONTRIBUTIONS
    // ================================================================

    private CurrentYearContributionResult calculateCurrentYearContributions(
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap,
            LocalDate asOfDate,
            BigDecimal rate,
            int yearBasis) {
        
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

        long daysForCurrentYearContributions = ChronoUnit.DAYS.between(
            LocalDate.of(asOfDate.getYear(), 1, 1), asOfDate);

        for (ContributionBifurcationDetail contrib : allContributions) {
            String status = contrib.getPostingStatus();
            if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                continue;
            }

            LocalDate contribDate = getContributionDate(contrib, headerInfoMap);
            if (contribDate.getYear() != asOfDate.getYear()) {
                continue;
            }

            BigDecimal pfEc = n(contrib.getPfEc());
            if (pfEc.compareTo(BigDecimal.ZERO) > 0) {
                currentYearPfEc = currentYearPfEc.add(pfEc);
                interestOnCurrentYearPfEc = interestOnCurrentYearPfEc.add(
                    calculateInterestOnBalance(pfEc, rate, daysForCurrentYearContributions, yearBasis));
            }

            BigDecimal pfMc = n(contrib.getPfMc());
            if (pfMc.compareTo(BigDecimal.ZERO) > 0) {
                currentYearPfMc = currentYearPfMc.add(pfMc);
                interestOnCurrentYearPfMc = interestOnCurrentYearPfMc.add(
                    calculateInterestOnBalance(pfMc, rate, daysForCurrentYearContributions, yearBasis));
            }

            BigDecimal pensionEc = n(contrib.getPensionEc());
            if (pensionEc.compareTo(BigDecimal.ZERO) > 0) {
                currentYearPensionEc = currentYearPensionEc.add(pensionEc);
                interestOnCurrentYearPensionEc = interestOnCurrentYearPensionEc.add(
                    calculateInterestOnBalance(pensionEc, rate, daysForCurrentYearContributions, yearBasis));
            }

            BigDecimal pensionMc = n(contrib.getPensionMc());
            if (pensionMc.compareTo(BigDecimal.ZERO) > 0) {
                currentYearPensionMc = currentYearPensionMc.add(pensionMc);
                interestOnCurrentYearPensionMc = interestOnCurrentYearPensionMc.add(
                    calculateInterestOnBalance(pensionMc, rate, daysForCurrentYearContributions, yearBasis));
            }

            BigDecimal gc = n(contrib.getGc());
            if (gc.compareTo(BigDecimal.ZERO) > 0) {
                currentYearGc = currentYearGc.add(gc);
                interestOnCurrentYearGc = interestOnCurrentYearGc.add(
                    calculateInterestOnBalance(gc, rate, daysForCurrentYearContributions, yearBasis));
            }

            BigDecimal vc = n(contrib.getVc());
            if (vc.compareTo(BigDecimal.ZERO) > 0) {
                currentYearVc = currentYearVc.add(vc);
                interestOnCurrentYearVc = interestOnCurrentYearVc.add(
                    calculateInterestOnBalance(vc, rate, daysForCurrentYearContributions, yearBasis));
            }
        }

        return CurrentYearContributionResult.builder()
            .pfEc(currentYearPfEc)
            .pfMc(currentYearPfMc)
            .pensionEc(currentYearPensionEc)
            .pensionMc(currentYearPensionMc)
            .gc(currentYearGc)
            .vc(currentYearVc)
            .interestPfEc(interestOnCurrentYearPfEc)
            .interestPfMc(interestOnCurrentYearPfMc)
            .interestPensionEc(interestOnCurrentYearPensionEc)
            .interestPensionMc(interestOnCurrentYearPensionMc)
            .interestGc(interestOnCurrentYearGc)
            .interestVc(interestOnCurrentYearVc)
            .build();
    }

    // ================================================================
    // HELPER METHODS - EXCESS SERVICE
    // ================================================================

    private List<YearMonth> getMonthsToExcludeForExcess(
            ExcessServiceResultDto excessResult,
            LocalDate asOfDate) {
        
        List<YearMonth> monthsToExclude = new ArrayList<>();
        
        if (excessResult == null || !excessResult.isEligible()) {
            return monthsToExclude;
        }
        
        LocalDate excessStart = excessResult.getExcessStartDate();
        LocalDate excessEnd = excessResult.getExcessEndDate();
        
        if (excessStart == null || excessEnd == null) {
            return monthsToExclude;
        }
        
        YearMonth current = YearMonth.from(excessStart);
        YearMonth end = YearMonth.from(excessEnd);
        
        while (!current.isAfter(end)) {
            monthsToExclude.add(current);
            current = current.plusMonths(1);
        }
        
        return monthsToExclude;
    }

    private PensionRebuildResult rebuildPensionWithoutExcess(
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap,
            List<YearMonth> monthsToExclude,
            BigDecimal openingPensionEc,
            BigDecimal openingPensionMc,
            BigDecimal openingPensionIec,
            BigDecimal openingPensionImc,
            BigDecimal rate,
            int yearBasis,
            LocalDate asOfDate) {
        
        log.info("=== Rebuilding Pension Components (Excess Service Detected) ===");
        
        if (monthsToExclude.isEmpty()) {
            return PensionRebuildResult.builder()
                .adjustedPensionEc(openingPensionEc)
                .adjustedPensionMc(openingPensionMc)
                .adjustedPensionIec(openingPensionIec)
                .adjustedPensionImc(openingPensionImc)
                .adjustedOpeningPec(openingPensionEc)
                .adjustedOpeningPmc(openingPensionMc)
                .adjustedOpeningPiec(openingPensionIec)
                .adjustedOpeningPimc(openingPensionImc)
                .excludedPrincipal(BigDecimal.ZERO)
                .excludedInterest(BigDecimal.ZERO)
                .build();
        }
        
        Set<YearMonth> excludeSet = new HashSet<>(monthsToExclude);
        
        // Find contributions in excess months
        List<ContributionBifurcationDetail> excludedContributions = allContributions.stream()
            .filter(c -> {
                if (c.getCreatedAt() == null) return false;
                String status = c.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    return false;
                }
                YearMonth ym = getYearMonthFromContribution(c, headerInfoMap);
                return excludeSet.contains(ym);
            })
            .collect(Collectors.toList());
        
        // Calculate excluded amounts
        BigDecimal excludedPec = BigDecimal.ZERO;
        BigDecimal excludedPmc = BigDecimal.ZERO;
        BigDecimal excludedPiec = BigDecimal.ZERO;
        BigDecimal excludedPimc = BigDecimal.ZERO;
        
        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
        
        for (ContributionBifurcationDetail excl : excludedContributions) {
            LocalDate date = getContributionDate(excl, headerInfoMap);
            
            BigDecimal pec = n(excl.getPensionEc());
            BigDecimal pmc = n(excl.getPensionMc());
            
            long days = ChronoUnit.DAYS.between(date, asOfDate);
            if (days < 0) days = 0;
            
            BigDecimal daysFactor = BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(yearBasis), 10, RM);
            
            excludedPec = excludedPec.add(pec);
            excludedPmc = excludedPmc.add(pmc);
            excludedPiec = excludedPiec.add(pec.multiply(rateFactor).multiply(daysFactor).setScale(2, RM));
            excludedPimc = excludedPimc.add(pmc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM));
        }
        
        // Calculate adjusted opening balances
        BigDecimal adjustedOpeningPec = openingPensionEc.subtract(excludedPec);
        BigDecimal adjustedOpeningPmc = openingPensionMc.subtract(excludedPmc);
        BigDecimal adjustedOpeningPiec = openingPensionIec.subtract(excludedPiec);
        BigDecimal adjustedOpeningPimc = openingPensionImc.subtract(excludedPimc);
        
        // Calculate interest on adjusted opening balances
        LocalDate yearStart = LocalDate.of(asOfDate.getYear(), 1, 1);
        long daysInCurrentYear = ChronoUnit.DAYS.between(yearStart, asOfDate);
        if (daysInCurrentYear < 0) daysInCurrentYear = 0;
        
        BigDecimal interestOnAdjustedPec = calculateInterestOnBalance(
            adjustedOpeningPec, rate, daysInCurrentYear, yearBasis);
        BigDecimal interestOnAdjustedPmc = calculateInterestOnBalance(
            adjustedOpeningPmc, rate, daysInCurrentYear, yearBasis);
        BigDecimal interestOnAdjustedPiec = calculateInterestOnBalance(
            adjustedOpeningPiec, rate, daysInCurrentYear, yearBasis);
        BigDecimal interestOnAdjustedPimc = calculateInterestOnBalance(
            adjustedOpeningPimc, rate, daysInCurrentYear, yearBasis);
        
        // Calculate current year contributions (excluding excess months)
        BigDecimal currentYearPec = BigDecimal.ZERO;
        BigDecimal currentYearPmc = BigDecimal.ZERO;
        BigDecimal currentYearInterestPec = BigDecimal.ZERO;
        BigDecimal currentYearInterestPmc = BigDecimal.ZERO;
        
        for (ContributionBifurcationDetail contrib : allContributions) {
            String status = contrib.getPostingStatus();
            if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                continue;
            }
            
            LocalDate date = getContributionDate(contrib, headerInfoMap);
            YearMonth ym = YearMonth.from(date);
            
            if (excludeSet.contains(ym)) {
                continue;
            }
            
            if (date.getYear() == asOfDate.getYear()) {
                BigDecimal pec = n(contrib.getPensionEc());
                BigDecimal pmc = n(contrib.getPensionMc());
                
                long daysFromDate = ChronoUnit.DAYS.between(date, asOfDate);
                if (daysFromDate < 0) daysFromDate = 0;
                
                currentYearPec = currentYearPec.add(pec);
                currentYearPmc = currentYearPmc.add(pmc);
                currentYearInterestPec = currentYearInterestPec.add(
                    calculateInterestOnBalance(pec, rate, daysFromDate, yearBasis));
                currentYearInterestPmc = currentYearInterestPmc.add(
                    calculateInterestOnBalance(pmc, rate, daysFromDate, yearBasis));
            }
        }
        
        // Calculate final adjusted values
        BigDecimal adjustedPensionEc = adjustedOpeningPec
            .add(interestOnAdjustedPec)
            .add(currentYearPec);
        
        BigDecimal adjustedPensionMc = adjustedOpeningPmc
            .add(interestOnAdjustedPmc)
            .add(currentYearPmc);
        
        BigDecimal adjustedPensionIec = adjustedOpeningPiec
            .add(interestOnAdjustedPiec)
            .add(currentYearInterestPec);
        
        BigDecimal adjustedPensionImc = adjustedOpeningPimc
            .add(interestOnAdjustedPimc)
            .add(currentYearInterestPmc);
        
        return PensionRebuildResult.builder()
            .adjustedPensionEc(adjustedPensionEc)
            .adjustedPensionMc(adjustedPensionMc)
            .adjustedPensionIec(adjustedPensionIec)
            .adjustedPensionImc(adjustedPensionImc)
            .adjustedOpeningPec(adjustedOpeningPec)
            .adjustedOpeningPmc(adjustedOpeningPmc)
            .adjustedOpeningPiec(adjustedOpeningPiec)
            .adjustedOpeningPimc(adjustedOpeningPimc)
            .excludedPrincipal(excludedPec.add(excludedPmc))
            .excludedInterest(excludedPiec.add(excludedPimc))
            .build();
    }

    private YearMonth getYearMonthFromContribution(
            ContributionBifurcationDetail detail,
            Map<Long, ContributionHeaderInfo> headerInfoMap) {

        ContributionHeaderInfo headerInfo = headerInfoMap.get(detail.getBifId());
        if (headerInfo != null) {
            return YearMonth.of(headerInfo.getYear(), headerInfo.getMonth());
        }
        LocalDate date = detail.getCreatedAt().toLocalDate();
        return YearMonth.from(date);
    }

    // ================================================================
    // HELPER METHODS - UTILITY
    // ================================================================

    private BigDecimal calculateInterestOnBalance(BigDecimal balance, BigDecimal rate, long days, int yearBasis) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) == 0 || days <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal dailyRate = rate.divide(BigDecimal.valueOf(yearBasis), 10, RM);
        return balance.multiply(dailyRate)
                .multiply(BigDecimal.valueOf(days))
                .setScale(2, RM);
    }

    private LocalDate getLastContributionDate(
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap) {

        if (allContributions == null || allContributions.isEmpty()) {
            return LocalDate.now();
        }

        LocalDate lastDate = null;
        for (ContributionBifurcationDetail contrib : allContributions) {
            String status = contrib.getPostingStatus();
            if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                continue;
            }

            LocalDate contribDate = getContributionDate(contrib, headerInfoMap);
            if (lastDate == null || contribDate.isAfter(lastDate)) {
                lastDate = contribDate;
            }
        }

        return lastDate != null ? lastDate : LocalDate.now();
    }

    private ArrConfiguration getArrConfiguration(String accountingYear) {
        if (accountingYear == null) {
            return null;
        }

        Optional<ArrConfiguration> arrOpt = arrRepo.findByAccountingYear(accountingYear);
        if (arrOpt.isPresent()) {
            return arrOpt.get();
        }

        // Try without dash
        String yearWithoutDash = accountingYear.replace("-", "");
        List<ArrConfiguration> allArr = arrRepo.findAll();
        for (ArrConfiguration arr : allArr) {
            String arrYear = arr.getAccountingYear().replace("-", "");
            if (arrYear.equals(yearWithoutDash)) {
                return arr;
            }
        }

        // Try previous years
        try {
            int year = Integer.parseInt(accountingYear.split("-")[0]);
            for (int i = 1; i <= 5; i++) {
                String previousYearDash = (year - i) + "-" + (year - i);
                Optional<ArrConfiguration> prevArrOpt = arrRepo.findByAccountingYear(previousYearDash);
                if (prevArrOpt.isPresent()) {
                    return prevArrOpt.get();
                }
            }
        } catch (Exception e) {
            // Ignore
        }

        // Use latest available
        allArr = arrRepo.findAll();
        if (!allArr.isEmpty()) {
            allArr.sort((a, b) -> b.getAccountingYear().compareTo(a.getAccountingYear()));
            return allArr.get(0);
        }

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
                return String.valueOf(year);
        } else {
            return String.valueOf(year);
        }
    }

    private int calculateContributionMonths(
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap,
            LocalDate asOfDate) {

        int count = 0;
        for (ContributionBifurcationDetail contrib : allContributions) {
            String status = contrib.getPostingStatus();
            if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                continue;
            }

            LocalDate date = getContributionDate(contrib, headerInfoMap);
            if (!date.isAfter(asOfDate)) {
                count++;
            }
        }
        return count;
    }

    private int calculateNonContributionMonths(
            MemberDetailResponseDto memberDetail,
            LocalDate relieveDate,
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap) {

        if (relieveDate == null || !isPensionEligible(memberDetail)) {
            return 0;
        }

        LocalDate startDate = getStartDate(memberDetail);
        if (startDate == null) {
            return 0;
        }

        return calculateEOLMonthsFromHistory(allContributions, headerInfoMap, startDate, relieveDate);
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

    private int calculateEOLMonthsFromHistory(
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap,
            LocalDate startDate,
            LocalDate endDate) {

        if (allContributions.isEmpty() || startDate == null || endDate == null)
            return 0;

        Set<String> contributionMonths = allContributions.stream()
                .filter(c -> c.getCreatedAt() != null)
                .map(c -> getMonthKey(c, headerInfoMap))
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

    private LocalDate getYearEndForDate(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        
        if (year < TRANSITION_YEAR) {
            if (month <= 6) {
                return LocalDate.of(year, 6, 30);
            } else {
                return LocalDate.of(year + 1, 6, 30);
            }
        } else {
            return LocalDate.of(year, 12, 31);
        }
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
    // INNER CLASSES
    // ================================================================

    private static class ContributionHeaderInfo {
        private final int month;
        private final int year;

        public ContributionHeaderInfo(int month, int year) {
            this.month = month;
            this.year = year;
        }

        public int getMonth() { return month; }
        public int getYear() { return year; }
    }

    @Builder
    @Data
    public static class PfOpeningBalanceResult {
        private BigDecimal pfEc;
        private BigDecimal pfMc;
        private BigDecimal interestPfEc;
        private BigDecimal interestPfMc;
        private int contributionsProcessed;
    }

    @Builder
    @Data
    public static class GcVcOpeningBalanceResult {
        private BigDecimal gc;
        private BigDecimal vc;
        private BigDecimal interestGc;
        private BigDecimal interestVc;
        private int contributionsProcessed;
    }

    @Builder
    @Data
    public static class CurrentYearContributionResult {
        private BigDecimal pfEc;
        private BigDecimal pfMc;
        private BigDecimal pensionEc;
        private BigDecimal pensionMc;
        private BigDecimal gc;
        private BigDecimal vc;
        private BigDecimal interestPfEc;
        private BigDecimal interestPfMc;
        private BigDecimal interestPensionEc;
        private BigDecimal interestPensionMc;
        private BigDecimal interestGc;
        private BigDecimal interestVc;
    }

    @Builder
    @Data
    public static class OpeningBalanceResult {
        private BigDecimal pensionEc;
        private BigDecimal pensionMc;
        private BigDecimal interestPensionEc;
        private BigDecimal interestPensionMc;
        private int contributionsProcessed;
    }

    @Builder
    @Data
    public static class PensionRebuildResult {
        private BigDecimal adjustedPensionEc;
        private BigDecimal adjustedPensionMc;
        private BigDecimal adjustedPensionIec;
        private BigDecimal adjustedPensionImc;
        private BigDecimal adjustedOpeningPec;
        private BigDecimal adjustedOpeningPmc;
        private BigDecimal adjustedOpeningPiec;
        private BigDecimal adjustedOpeningPimc;
        private BigDecimal excludedPrincipal;
        private BigDecimal excludedInterest;
    }
}