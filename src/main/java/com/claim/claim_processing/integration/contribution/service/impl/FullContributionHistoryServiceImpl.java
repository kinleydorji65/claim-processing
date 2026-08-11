package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.ExcessServiceResultDto;
import com.claim.claim_processing.integration.contribution.dto.FullContributionHistoryResponse;
import com.claim.claim_processing.integration.contribution.dto.FullContributionHistoryResponse.*;
import com.claim.claim_processing.integration.contribution.entity.ArrConfiguration;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationHeader;
import com.claim.claim_processing.integration.contribution.repository.ArrConfigurationRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationDetailRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationHeaderRepository;
import com.claim.claim_processing.integration.contribution.service.FullContributionHistoryService;
import com.claim.claim_processing.integration.member.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FullContributionHistoryServiceImpl implements FullContributionHistoryService {

    private final ContributionBifurcationDetailRepository contributionBifurcationDetailRepository;
    private final ContributionBifurcationHeaderRepository contributionBifurcationHeaderRepository;
    private final ArrConfigurationRepository arrRepo;
    private final MemberService memberService;
    private final ExcessServiceCalculator excessServiceCalculator;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final int TRANSITION_YEAR = 2022;

    // Month name to number mapping
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

    @Override
    public ApiResponseDTO<FullContributionHistoryResponse> getFullContributionHistory(
            String nppfNumber) {

        log.info("========== FULL CONTRIBUTION HISTORY ==========");
        log.info("NPPF Number: {}", nppfNumber);

        // ========== 1. GET MEMBER DETAILS ==========
        ApiResponseDTO<MemberDetailResponseDto> memberResponse = memberService.getMemberDetails(nppfNumber);
        if (memberResponse == null || memberResponse.getData() == null) {
            throw ClaimException.notFound("Member not found: " + nppfNumber);
        }

        MemberDetailResponseDto memberDetail = memberResponse.getData();
        String memberName = memberDetail.getMemberName();
        String cid = memberDetail.getIdentityNumber();

        // ========== 2. FIND CONTRIBUTIONS ==========
        List<ContributionBifurcationDetail> allContributions = findContributions(nppfNumber, cid);

        if (allContributions.isEmpty()) {
            log.error("❌ No contributions found for NPPF: {} or CID: {}", nppfNumber, cid);
            throw ClaimException.notFound("No contributions found for member: " + nppfNumber);
        }

        log.info("Found {} total contributions", allContributions.size());

        // ========== 3. GET HEADER INFO ==========
        Map<Long, ContributionHeaderInfo> headerInfoMap = getHeaderInfoForContributions(allContributions);
        log.info("Found header info for {} contributions", headerInfoMap.size());

        // ========== 4. GET FIRST AND LAST CONTRIBUTION DATES ==========
        LocalDate firstContributionDate = getFirstContributionDate(allContributions, headerInfoMap);
        LocalDate lastContributionDate = getLastContributionDate(allContributions, headerInfoMap);

        log.info("First Contribution Date: {}, Last Contribution Date: {}", 
                firstContributionDate, lastContributionDate);

        // ========== 5. GET START DATE ==========
        LocalDate startDate = getStartDate(memberDetail);
        if (startDate == null || startDate.isAfter(firstContributionDate)) {
            startDate = firstContributionDate;
        }
        log.info("Start Date: {}", startDate);

        // ========== 6. GET ACCOUNTING YEARS ==========
        List<Integer> contributionYears = getContributionYears(allContributions, headerInfoMap);
        log.info("Contribution Years: {}", contributionYears);

        List<String> accountingYears = new ArrayList<>();
        for (Integer year : contributionYears) {
            String accountingYear = getAccountingYearForDate(LocalDate.of(year, 1, 1));
            if (!accountingYears.contains(accountingYear)) {
                accountingYears.add(accountingYear);
            }
        }

        log.info("Accounting Years with contributions: {}", accountingYears);

        if (accountingYears.isEmpty()) {
            throw ClaimException.notFound("No valid accounting years found for member: " + nppfNumber);
        }

        // ========== 7. GET RATE AND YEAR BASIS ==========
        String targetYear = String.valueOf(LocalDate.now().getYear());
        ArrConfiguration currentYearArr = getArrConfigurationWithFallback(targetYear);
        BigDecimal rate = currentYearArr != null ? currentYearArr.getArrRate() : BigDecimal.ZERO;
        int yearBasis = currentYearArr != null ? currentYearArr.getYearBasis() : 365;

        log.info("Interest Rate: {}%, Year Basis: {} days", rate, yearBasis);

        // ========== 8. CALCULATE EXCESS SERVICE ==========
        ExcessServiceResultDto excessResult = null;
        boolean hasExcess = false;
        List<YearMonth> monthsToExclude = new ArrayList<>();
        PensionRebuildResult rebuildResult = null;
        BigDecimal openingBalancePensionEc = BigDecimal.ZERO;
        BigDecimal openingBalancePensionMc = BigDecimal.ZERO;
        BigDecimal openingBalancePensionIec = BigDecimal.ZERO;
        BigDecimal openingBalancePensionImc = BigDecimal.ZERO;

        try {
            excessResult = excessServiceCalculator.calculateExcessService(memberDetail);
            if (excessResult != null && excessResult.isEligible()) {
                hasExcess = true;
                log.info("✅ Excess Service found for member: {}", nppfNumber);
                
                LocalDate excessStartDate = excessResult.getExcessStartDate();
                log.info("Excess Start Date: {}", excessStartDate);
                
                // ✅ Calculate opening balance BEFORE excess period
                OpeningBalanceResult openingResult = calculateOpeningBalanceFromAllContributions(
                    allContributions,
                    headerInfoMap,
                    excessStartDate,
                    rate,
                    yearBasis,
                    LocalDate.now()
                );
                
                openingBalancePensionEc = openingResult.getPensionEc();
                openingBalancePensionMc = openingResult.getPensionMc();
                openingBalancePensionIec = openingResult.getInterestPensionEc();
                openingBalancePensionImc = openingResult.getInterestPensionMc();
                
                log.info("=== Opening Balance BEFORE Excess Period ===");
                log.info("  PEC: {}, PMC: {}", openingBalancePensionEc, openingBalancePensionMc);
                log.info("  PIEC: {}, PIMC: {}", openingBalancePensionIec, openingBalancePensionImc);
                
                monthsToExclude = getMonthsToExcludeForExcess(excessResult, LocalDate.now());
                log.info("Months to exclude from pension: {}", monthsToExclude.size());
                
                // ✅ Rebuild pension without excess months
                if (!monthsToExclude.isEmpty()) {
                    rebuildResult = rebuildPensionWithoutExcess(
                        allContributions,
                        headerInfoMap,
                        monthsToExclude,
                        openingBalancePensionEc,
                        openingBalancePensionMc,
                        openingBalancePensionIec,
                        openingBalancePensionImc,
                        rate,
                        yearBasis,
                        LocalDate.now()
                    );
                    
                    log.info("=== Pension Rebuild Complete ===");
                    log.info("Adjusted PEC: {}", rebuildResult.getAdjustedPensionEc());
                    log.info("Adjusted PMC: {}", rebuildResult.getAdjustedPensionMc());
                    log.info("Excluded Principal: {}", rebuildResult.getExcludedPrincipal());
                }
            }
        } catch (Exception e) {
            log.warn("Error calculating excess service: {}", e.getMessage());
        }

        // ========== 9. INITIALIZE OPENING BALANCES ==========
        ComponentBalances currentBalances = ComponentBalances.builder()
                .pfMc(BigDecimal.ZERO)
                .pfEc(BigDecimal.ZERO)
                .pfImc(BigDecimal.ZERO)
                .pfIec(BigDecimal.ZERO)
                .pMc(BigDecimal.ZERO)
                .pEc(BigDecimal.ZERO)
                .pImc(BigDecimal.ZERO)
                .pIec(BigDecimal.ZERO)
                .gc(BigDecimal.ZERO)
                .gic(BigDecimal.ZERO)
                .vc(BigDecimal.ZERO)
                .vic(BigDecimal.ZERO)
                .ivc(BigDecimal.ZERO)
                .igc(BigDecimal.ZERO)
                .build();

        // ========== 10. PROCESS EACH YEAR ==========
        LocalDate calculationDate = LocalDate.now();
        List<FinancialYearData> financialYearDataList = new ArrayList<>();
        List<MonthlyContributionDetail> currentYearMonthlyDetails = new ArrayList<>();
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;

        YearMonth currentMonth = YearMonth.now();
        Set<YearMonth> excludeSet = new HashSet<>(monthsToExclude);

        for (String accountingYear : accountingYears) {
            YearType yearType = getYearType(accountingYear);

            ArrConfiguration arrConfig = getArrConfigurationWithFallback(accountingYear);
            BigDecimal yearRate = arrConfig.getArrRate() != null ? arrConfig.getArrRate() : rate;
            int yearYearBasis = arrConfig != null ? arrConfig.getYearBasis() : yearBasis;

            // ===== CAPTURE OPENING BALANCES =====
            OpeningBalances openingBalances = OpeningBalances.builder()
                    .pfMc(currentBalances.getPfMc())
                    .pfEc(currentBalances.getPfEc())
                    .pfImc(currentBalances.getPfImc())
                    .pfIec(currentBalances.getPfIec())
                    .pfTotal(calculateTotal(currentBalances.getPfMc(), currentBalances.getPfEc(),
                            currentBalances.getPfImc(), currentBalances.getPfIec()))
                    .pcMc(currentBalances.getPMc())
                    .pcEc(currentBalances.getPEc())
                    .pcImc(currentBalances.getPImc())
                    .pcIec(currentBalances.getPIec())
                    .pcTotal(calculateTotal(currentBalances.getPMc(), currentBalances.getPEc(),
                            currentBalances.getPImc(), currentBalances.getPIec()))
                    .grandTotal(calculateGrandTotal(currentBalances))
                    .build();

            // ===== GET MONTHS IN THIS YEAR =====
            List<YearMonth> monthsInYear = getMonthsInYear(accountingYear, yearType);
            
            if (accountingYear.equals(targetYear)) {
                monthsInYear = monthsInYear.stream()
                        .filter(ym -> !ym.isAfter(currentMonth))
                        .collect(Collectors.toList());
                log.info("Target year {}: Processing {} months (up to {})", 
                        targetYear, monthsInYear.size(), currentMonth);
            }

            boolean isTargetYear = accountingYear.equals(targetYear);

            // ===== YEARLY ACCUMULATORS =====
            ComponentBalances yearlyContributions = ComponentBalances.builder()
                    .pfMc(BigDecimal.ZERO).pfEc(BigDecimal.ZERO)
                    .pMc(BigDecimal.ZERO).pEc(BigDecimal.ZERO)
                    .gc(BigDecimal.ZERO).vc(BigDecimal.ZERO)
                    .pfImc(BigDecimal.ZERO).pfIec(BigDecimal.ZERO)
                    .pImc(BigDecimal.ZERO).pIec(BigDecimal.ZERO)
                    .gic(BigDecimal.ZERO).vic(BigDecimal.ZERO)
                    .build();

            // ===== PROCESS EACH MONTH =====
            for (YearMonth yearMonth : monthsInYear) {
                ContributionBifurcationDetail detail = findContributionForMonthFromHeader(
                        allContributions, headerInfoMap, yearMonth);

                boolean hasContribution = detail != null;
                
                // ✅ Check if this month is in excess period (should be excluded from pension)
                boolean isExcessMonth = excludeSet.contains(yearMonth);

                ComponentBalances monthContribution = ComponentBalances.builder()
                        .pfMc(BigDecimal.ZERO).pfEc(BigDecimal.ZERO)
                        .pMc(BigDecimal.ZERO).pEc(BigDecimal.ZERO)
                        .gc(BigDecimal.ZERO).vc(BigDecimal.ZERO)
                        .pfImc(BigDecimal.ZERO).pfIec(BigDecimal.ZERO)
                        .pImc(BigDecimal.ZERO).pIec(BigDecimal.ZERO)
                        .gic(BigDecimal.ZERO).vic(BigDecimal.ZERO)
                        .build();

                int daysSinceContribution = 0;
                LocalDate contributionDate = null;

                if (hasContribution) {
                    contributionDate = getContributionDate(detail, headerInfoMap);

                    // PF components (ALWAYS included)
                    monthContribution.setPfMc(n(detail.getPfMc()));
                    monthContribution.setPfEc(n(detail.getPfEc()));
                    
                    // ===== PENSION COMPONENTS: EXCLUDE IF EXCESS MONTH =====
                    if (!isExcessMonth) {
                        // ✅ Non-excess month: Pension goes to pension
                        monthContribution.setPMc(n(detail.getPensionMc()));
                        monthContribution.setPEc(n(detail.getPensionEc()));
                    } else {
                        // ❌ Excess month: Pension goes to PF (excess transferred)
                        log.info("   ⏭️ Month {} is in excess period - pension moved to PF", yearMonth);
                        monthContribution.setPfMc(monthContribution.getPfMc().add(n(detail.getPensionMc())));
                        monthContribution.setPfEc(monthContribution.getPfEc().add(n(detail.getPensionEc())));
                    }
                    
                    monthContribution.setGc(n(detail.getGc()));
                    monthContribution.setVc(n(detail.getVc()));

                    // ===== CALCULATE INTEREST =====
                    if (isTargetYear) {
                        daysSinceContribution = (int) ChronoUnit.DAYS.between(contributionDate, calculationDate);
                        if (daysSinceContribution < 0) daysSinceContribution = 0;

                        if (daysSinceContribution > 0) {
                            BigDecimal rateFactor = yearRate.divide(HUNDRED, 10, RM);
                            BigDecimal daysFactor = BigDecimal.valueOf(daysSinceContribution)
                                    .divide(BigDecimal.valueOf(yearYearBasis), 10, RM);

                            // Interest on PF
                            monthContribution.setPfImc(
                                    monthContribution.getPfMc()
                                            .multiply(rateFactor).multiply(daysFactor).setScale(2, RM)
                            );
                            monthContribution.setPfIec(
                                    monthContribution.getPfEc()
                                            .multiply(rateFactor).multiply(daysFactor).setScale(2, RM)
                            );

                            // Interest on Pension (only if not excess month)
                            if (!isExcessMonth) {
                                monthContribution.setPImc(
                                        monthContribution.getPMc()
                                                .multiply(rateFactor).multiply(daysFactor).setScale(2, RM)
                                );
                                monthContribution.setPIec(
                                        monthContribution.getPEc()
                                                .multiply(rateFactor).multiply(daysFactor).setScale(2, RM)
                                );
                            }
                            
                            monthContribution.setGic(
                                    monthContribution.getGc()
                                            .multiply(rateFactor).multiply(daysFactor).setScale(2, RM)
                            );
                            monthContribution.setVic(
                                    monthContribution.getVc()
                                            .multiply(rateFactor).multiply(daysFactor).setScale(2, RM)
                            );
                        }
                    }
                }

                // ===== ADD TO RUNNING BALANCE =====
                currentBalances = addBalances(currentBalances, monthContribution);
                yearlyContributions = addBalances(yearlyContributions, monthContribution);

                // ===== ADD TO MONTHLY DETAILS (ONLY IF HAS CONTRIBUTION) =====
                if (isTargetYear && hasContribution) {
                    BigDecimal monthPrincipal = calculateTotalContribution(monthContribution);
                    BigDecimal monthInterestAmt = calculateTotalInterest(monthContribution);
                    BigDecimal monthTotal = monthPrincipal.add(monthInterestAmt);

                    totalPrincipal = totalPrincipal.add(monthPrincipal);
                    totalInterest = totalInterest.add(monthInterestAmt);

                    MonthlyContributionDetail monthlyDetail = MonthlyContributionDetail.builder()
                            .month(yearMonth.toString())
                            .monthName(yearMonth.getMonth().toString())
                            .monthNumber(yearMonth.getMonthValue())
                            .contributionDate(contributionDate)
                            .daysSinceContribution(daysSinceContribution)
                            .interestRate(yearRate)
                            .hasContribution(true)
                            .pfMc(monthContribution.getPfMc())
                            .pfEc(monthContribution.getPfEc())
                            .pMc(monthContribution.getPMc())
                            .pEc(monthContribution.getPEc())
                            .gc(monthContribution.getGc())
                            .vc(monthContribution.getVc())
                            .pfImc(monthContribution.getPfImc())
                            .pfIec(monthContribution.getPfIec())
                            .pImc(monthContribution.getPImc())
                            .pIec(monthContribution.getPIec())
                            .gic(monthContribution.getGic())
                            .vic(monthContribution.getVic())
                            .runningPfMc(currentBalances.getPfMc())
                            .runningPfEc(currentBalances.getPfEc())
                            .runningPfImc(currentBalances.getPfImc())
                            .runningPfIec(currentBalances.getPfIec())
                            .runningPMc(currentBalances.getPMc())
                            .runningPEc(currentBalances.getPEc())
                            .runningPImc(currentBalances.getPImc())
                            .runningPIec(currentBalances.getPIec())
                            .runningGc(currentBalances.getGc())
                            .runningVc(currentBalances.getVc())
                            .monthContribution(monthPrincipal)
                            .monthInterest(monthInterestAmt)
                            .monthTotal(monthTotal)
                            .cumulativeTotal(totalPrincipal.add(totalInterest))
                            .build();

                    currentYearMonthlyDetails.add(monthlyDetail);
                }
            }

            // ===== BUILD TRANSACTION DURING THE YEAR =====
            TransactionDuringYear transaction = TransactionDuringYear.builder()
                    .pfMc(yearlyContributions.getPfMc())
                    .pfEc(yearlyContributions.getPfEc())
                    .pfImc(yearlyContributions.getPfImc())
                    .pfIec(yearlyContributions.getPfIec())
                    .pfTotal(calculateTotal(yearlyContributions.getPfMc(), yearlyContributions.getPfEc(),
                            yearlyContributions.getPfImc(), yearlyContributions.getPfIec()))
                    .pcMc(yearlyContributions.getPMc())
                    .pcEc(yearlyContributions.getPEc())
                    .pcImc(yearlyContributions.getPImc())
                    .pcIec(yearlyContributions.getPIec())
                    .pcTotal(calculateTotal(yearlyContributions.getPMc(), yearlyContributions.getPEc(),
                            yearlyContributions.getPImc(), yearlyContributions.getPIec()))
                    .grandTotal(calculateGrandTotal(yearlyContributions))
                    .build();

            // ===== BUILD CLOSING BALANCES =====
            ClosingBalances closingBalances = ClosingBalances.builder()
                    .pfMc(currentBalances.getPfMc())
                    .pfEc(currentBalances.getPfEc())
                    .pfImc(currentBalances.getPfImc())
                    .pfIec(currentBalances.getPfIec())
                    .pfTotal(calculateTotal(currentBalances.getPfMc(), currentBalances.getPfEc(),
                            currentBalances.getPfImc(), currentBalances.getPfIec()))
                    .pcMc(currentBalances.getPMc())
                    .pcEc(currentBalances.getPEc())
                    .pcImc(currentBalances.getPImc())
                    .pcIec(currentBalances.getPIec())
                    .pcTotal(calculateTotal(currentBalances.getPMc(), currentBalances.getPEc(),
                            currentBalances.getPImc(), currentBalances.getPIec()))
                    .grandTotal(calculateGrandTotal(currentBalances))
                    .build();

            // ===== BUILD EXCESS TRANSFERRED =====
            ExcessTransferred excessTransferred = ExcessTransferred.builder()
                    .pcMc(BigDecimal.ZERO)
                    .pcEc(BigDecimal.ZERO)
                    .pcImc(BigDecimal.ZERO)
                    .pcIec(BigDecimal.ZERO)
                    .total(BigDecimal.ZERO)
                    .build();

            if (hasExcess && excessResult != null && rebuildResult != null) {
                excessTransferred = ExcessTransferred.builder()
                        .pcMc(rebuildResult.getExcludedPmc())
                        .pcEc(rebuildResult.getExcludedPec())
                        .pcImc(rebuildResult.getExcludedPimc())
                        .pcIec(rebuildResult.getExcludedPiec())
                        .total(rebuildResult.getExcludedPrincipal().add(rebuildResult.getExcludedInterest()))
                        .build();
            }

            // ===== BUILD FINANCIAL YEAR DATA =====
            FinancialYearData yearData = FinancialYearData.builder()
                    .financialYear(accountingYear)
                    .openingBalances(openingBalances)
                    .transactionDuringYear(transaction)
                    .excessTransferred(excessTransferred)
                    .closingBalances(closingBalances)
                    .build();

            financialYearDataList.add(yearData);
        }

        // ========== 11. BUILD RESPONSE ==========
        BigDecimal finalTotal = totalPrincipal.add(totalInterest);

        ExcessServiceInfo excessServiceInfo = null;
        if (hasExcess && excessResult != null) {
            excessServiceInfo = ExcessServiceInfo.builder()
                    .isEligible(excessResult.isEligible())
                    .excessStartDate(excessResult.getExcessStartDate())
                    .excessEndDate(excessResult.getExcessEndDate())
                    .cutoffServiceDate(excessResult.getCutoffServiceDate())
                    .cutoffYears(excessResult.getCutoffYears())
                    .totalExcessAmount(excessResult.getTotalExcessAmount())
                    .totalContributionsInExcess(excessResult.getTotalContributionsInExcess())
                    .totalInterestInExcess(excessResult.getTotalInterestInExcess())
                    .totalEOLMonths(excessResult.getTotalEOLMonths())
                    .eolMonthsInExcess(excessResult.getEolMonthsInExcess())
                    .status(excessResult.getStatus())
                    .message(excessResult.getMessage())
                    .build();
        }

        long monthsWithContribution = currentYearMonthlyDetails.size();

        String message = String.format(
                "Calculated from %s to %s. Total balance: %.2f. %d months with contributions in %s.",
                accountingYears.get(0),
                accountingYears.get(accountingYears.size() - 1),
                finalTotal,
                monthsWithContribution,
                targetYear
        );

        FullContributionHistoryResponse response = FullContributionHistoryResponse.builder()
                .nppfNumber(nppfNumber)
                .memberName(memberName)
                .joiningDate(startDate)
                .calculationDate(calculationDate)
                .firstContributionDate(firstContributionDate)
                .lastContributionDate(lastContributionDate)
                .financialYearData(financialYearDataList)
                .currentYearMonthlyDetails(currentYearMonthlyDetails)
                .totalPrincipal(totalPrincipal)
                .totalInterest(totalInterest)
                .totalBalance(finalTotal)
                .currentYear(targetYear)
                .currentYearRate(rate)
                .currentYearBasis(yearBasis)
                .excessService(excessServiceInfo)
                .status("SUCCESS")
                .message(message)
                .build();

        log.info("========== FULL CONTRIBUTION HISTORY COMPLETE ==========");
        log.info("Total Principal: {}, Total Interest: {}, Total Balance: {}",
                totalPrincipal, totalInterest, finalTotal);
        log.info("Financial Years processed: {}", financialYearDataList.size());
        log.info("Current year months shown: {}", currentYearMonthlyDetails.size());

        return ApiResponseDTO.success(response);
    }

    // ================================================================
    // HEADER INFO METHODS
    // ================================================================

    private Map<Long, ContributionHeaderInfo> getHeaderInfoForContributions(
            List<ContributionBifurcationDetail> contributions) {

        Map<Long, ContributionHeaderInfo> headerInfoMap = new HashMap<>();

        Set<Long> bifIds = contributions.stream()
                .map(ContributionBifurcationDetail::getBifId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        if (!bifIds.isEmpty()) {
            List<ContributionBifurcationHeader> headers = contributionBifurcationHeaderRepository
                    .findAllById(bifIds);

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

    private LocalDate getFirstContributionDate(
            List<ContributionBifurcationDetail> contributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap) {

        LocalDate firstDate = null;
        for (ContributionBifurcationDetail detail : contributions) {
            LocalDate date = getContributionDate(detail, headerInfoMap);
            if (firstDate == null || date.isBefore(firstDate)) {
                firstDate = date;
            }
        }
        return firstDate != null ? firstDate : LocalDate.now();
    }

    private LocalDate getLastContributionDate(
            List<ContributionBifurcationDetail> contributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap) {

        LocalDate lastDate = null;
        for (ContributionBifurcationDetail detail : contributions) {
            LocalDate date = getContributionDate(detail, headerInfoMap);
            if (lastDate == null || date.isAfter(lastDate)) {
                lastDate = date;
            }
        }
        return lastDate != null ? lastDate : LocalDate.now();
    }

    private List<Integer> getContributionYears(
            List<ContributionBifurcationDetail> contributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap) {

        List<Integer> years = new ArrayList<>();
        for (ContributionBifurcationDetail detail : contributions) {
            ContributionHeaderInfo headerInfo = headerInfoMap.get(detail.getBifId());
            if (headerInfo != null) {
                if (!years.contains(headerInfo.getYear())) {
                    years.add(headerInfo.getYear());
                }
            }
        }
        Collections.sort(years);

        if (years.isEmpty()) {
            years = contributions.stream()
                    .map(c -> c.getCreatedAt().toLocalDate().getYear())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        }
        return years;
    }

    // ================================================================
    // EXCESS SERVICE METHODS (SAME AS MemberContributionService)
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
        
        log.debug("Months to exclude from pension: {}", monthsToExclude);
        return monthsToExclude;
    }

    private OpeningBalanceResult calculateOpeningBalanceFromAllContributions(
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap,
            LocalDate excessStartDate,
            BigDecimal rate,
            int yearBasis,
            LocalDate asOfDate) {
        
        log.info("=== Calculating Opening Balance from ALL Historical Contributions ===");
        log.info("Excess starts from: {}", excessStartDate);
        
        List<ContributionBifurcationDetail> preExcessContribs = allContributions.stream()
            .filter(c -> {
                if (c.getCreatedAt() == null) return false;
                String status = c.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    return false;
                }
                LocalDate date = getContributionDate(c, headerInfoMap);
                return date.isBefore(excessStartDate);
            })
            .sorted(Comparator.comparing(c -> getContributionDate(c, headerInfoMap)))
            .collect(Collectors.toList());
        
        log.info("Found {} contributions BEFORE excess period", preExcessContribs.size());
        
        BigDecimal runningPensionEc = BigDecimal.ZERO;
        BigDecimal runningPensionMc = BigDecimal.ZERO;
        BigDecimal runningPensionIec = BigDecimal.ZERO;
        BigDecimal runningPensionImc = BigDecimal.ZERO;
        
        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
        
        for (ContributionBifurcationDetail contrib : preExcessContribs) {
            LocalDate contribDate = getContributionDate(contrib, headerInfoMap);
            
            BigDecimal pec = n(contrib.getPensionEc());
            BigDecimal pmc = n(contrib.getPensionMc());
            
            long days = ChronoUnit.DAYS.between(contribDate, excessStartDate);
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
        
        log.info("=== Opening Balance BEFORE Excess Period ===");
        log.info("  PEC: {}, PMC: {}", runningPensionEc, runningPensionMc);
        log.info("  PIEC: {}, PIMC: {}", runningPensionIec, runningPensionImc);
        
        return OpeningBalanceResult.builder()
            .pensionEc(runningPensionEc)
            .pensionMc(runningPensionMc)
            .interestPensionEc(runningPensionIec)
            .interestPensionMc(runningPensionImc)
            .build();
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
        log.info("Months to exclude: {}", monthsToExclude.size());
        
        if (monthsToExclude.isEmpty()) {
            return PensionRebuildResult.builder()
                .adjustedPensionEc(openingPensionEc)
                .adjustedPensionMc(openingPensionMc)
                .adjustedPensionIec(openingPensionIec)
                .adjustedPensionImc(openingPensionImc)
                .excludedPrincipal(BigDecimal.ZERO)
                .excludedInterest(BigDecimal.ZERO)
                .excludedPec(BigDecimal.ZERO)
                .excludedPmc(BigDecimal.ZERO)
                .excludedPiec(BigDecimal.ZERO)
                .excludedPimc(BigDecimal.ZERO)
                .build();
        }
        
        Set<YearMonth> excludeSet = new HashSet<>(monthsToExclude);
        
        BigDecimal excludedPec = BigDecimal.ZERO;
        BigDecimal excludedPmc = BigDecimal.ZERO;
        BigDecimal excludedPiec = BigDecimal.ZERO;
        BigDecimal excludedPimc = BigDecimal.ZERO;
        
        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
        
        for (ContributionBifurcationDetail detail : allContributions) {
            if (detail.getCreatedAt() == null) continue;
            String status = detail.getPostingStatus();
            if (status == null || !"POSTED".equals(status.trim().toUpperCase())) continue;
            
            LocalDate date = getContributionDate(detail, headerInfoMap);
            YearMonth ym = YearMonth.from(date);
            
            if (!excludeSet.contains(ym)) continue;
            
            BigDecimal pec = n(detail.getPensionEc());
            BigDecimal pmc = n(detail.getPensionMc());
            
            long days = ChronoUnit.DAYS.between(date, asOfDate);
            if (days < 0) days = 0;
            
            BigDecimal daysFactor = BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(yearBasis), 10, RM);
            
            BigDecimal interestPec = pec.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestPmc = pmc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            excludedPec = excludedPec.add(pec);
            excludedPmc = excludedPmc.add(pmc);
            excludedPiec = excludedPiec.add(interestPec);
            excludedPimc = excludedPimc.add(interestPmc);
        }
        
        log.info("Excluded amounts:");
        log.info("  Principal - PEC: {}, PMC: {}", excludedPec, excludedPmc);
        log.info("  Interest - PIEC: {}, PIMC: {}", excludedPiec, excludedPimc);
        
        BigDecimal adjustedPensionEc = openingPensionEc.subtract(excludedPec);
        BigDecimal adjustedPensionMc = openingPensionMc.subtract(excludedPmc);
        BigDecimal adjustedPensionIec = openingPensionIec.subtract(excludedPiec);
        BigDecimal adjustedPensionImc = openingPensionImc.subtract(excludedPimc);
        
        log.info("Adjusted Pension:");
        log.info("  PEC: {} - {} = {}", openingPensionEc, excludedPec, adjustedPensionEc);
        log.info("  PMC: {} - {} = {}", openingPensionMc, excludedPmc, adjustedPensionMc);
        
        return PensionRebuildResult.builder()
            .adjustedPensionEc(adjustedPensionEc)
            .adjustedPensionMc(adjustedPensionMc)
            .adjustedPensionIec(adjustedPensionIec)
            .adjustedPensionImc(adjustedPensionImc)
            .excludedPrincipal(excludedPec.add(excludedPmc))
            .excludedInterest(excludedPiec.add(excludedPimc))
            .excludedPec(excludedPec)
            .excludedPmc(excludedPmc)
            .excludedPiec(excludedPiec)
            .excludedPimc(excludedPimc)
            .build();
    }

    // ================================================================
    // EXISTING HELPER METHODS
    // ================================================================

    private ContributionBifurcationDetail findContributionForMonthFromHeader(
            List<ContributionBifurcationDetail> contributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap,
            YearMonth yearMonth) {

        return contributions.stream()
                .filter(c -> {
                    ContributionHeaderInfo headerInfo = headerInfoMap.get(c.getBifId());
                    if (headerInfo != null) {
                        return headerInfo.getYear() == yearMonth.getYear() &&
                               headerInfo.getMonth() == yearMonth.getMonthValue();
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);
    }

    private List<ContributionBifurcationDetail> findContributions(String nppfNumber, String cid) {
        List<ContributionBifurcationDetail> results = new ArrayList<>();

        log.info("Searching contributions for NPPF: {}, CID: {}", nppfNumber, cid);

        try {
            List<ContributionBifurcationDetail> byNppf = contributionBifurcationDetailRepository
                    .findByNppfNumberOrderByCreatedAtAsc(nppfNumber);
            if (!byNppf.isEmpty()) {
                log.info("✅ Found {} contributions by NPPF number", byNppf.size());
                List<ContributionBifurcationDetail> filtered = byNppf.stream()
                        .filter(c -> hasActualAmounts(c))
                        .collect(Collectors.toList());
                if (!filtered.isEmpty()) {
                    log.info("✅ Found {} contributions with actual amounts", filtered.size());
                    return filtered;
                }
                return byNppf;
            }
        } catch (Exception e) {
            log.warn("Error finding by NPPF: {}", e.getMessage());
        }

        try {
            List<ContributionBifurcationDetail> all = contributionBifurcationDetailRepository.findAll();
            
            List<ContributionBifurcationDetail> filtered = all.stream()
                    .filter(c -> {
                        String nppf = c.getNppfNumber();
                        String cidField = c.getCid();
                        return (nppf != null && nppf.equals(nppfNumber)) ||
                               (cidField != null && cidField.equals(cid));
                    })
                    .filter(c -> hasActualAmounts(c))
                    .sorted((a, b) -> {
                        if (a.getCreatedAt() == null) return 1;
                        if (b.getCreatedAt() == null) return -1;
                        return a.getCreatedAt().compareTo(b.getCreatedAt());
                    })
                    .collect(Collectors.toList());
            
            if (!filtered.isEmpty()) {
                log.info("✅ Found {} contributions by filtering all records", filtered.size());
                return filtered;
            }
        } catch (Exception e) {
            log.warn("Error filtering all records: {}", e.getMessage());
        }

        return results;
    }

    private boolean hasActualAmounts(ContributionBifurcationDetail c) {
        BigDecimal pfMc = c.getPfMc();
        BigDecimal pfEc = c.getPfEc();
        BigDecimal pensionMc = c.getPensionMc();
        BigDecimal pensionEc = c.getPensionEc();
        BigDecimal gc = c.getGc();
        BigDecimal vc = c.getVc();
        
        return (pfMc != null && pfMc.compareTo(BigDecimal.ZERO) > 0) ||
               (pfEc != null && pfEc.compareTo(BigDecimal.ZERO) > 0) ||
               (pensionMc != null && pensionMc.compareTo(BigDecimal.ZERO) > 0) ||
               (pensionEc != null && pensionEc.compareTo(BigDecimal.ZERO) > 0) ||
               (gc != null && gc.compareTo(BigDecimal.ZERO) > 0) ||
               (vc != null && vc.compareTo(BigDecimal.ZERO) > 0);
    }

    private String getAccountingYearForDate(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        if (year < TRANSITION_YEAR) {
            if (month <= 6) {
                return (year - 1) + "-" + year;
            } else {
                return year + "-" + (year + 1);
            }
        } else if (year == TRANSITION_YEAR) {
            if (month <= 6) {
                return (year - 1) + "-" + year;
            } else {
                return String.valueOf(year);
            }
        } else {
            return String.valueOf(year);
        }
    }

    private BigDecimal calculateTotal(BigDecimal mc, BigDecimal ec, BigDecimal imc, BigDecimal iec) {
        return n(mc).add(n(ec)).add(n(imc)).add(n(iec));
    }

    private BigDecimal calculateGrandTotal(ComponentBalances balances) {
        return n(balances.getPfMc())
                .add(n(balances.getPfEc()))
                .add(n(balances.getPfImc()))
                .add(n(balances.getPfIec()))
                .add(n(balances.getPMc()))
                .add(n(balances.getPEc()))
                .add(n(balances.getPImc()))
                .add(n(balances.getPIec()))
                .add(n(balances.getGc()))
                .add(n(balances.getVc()));
    }

    private boolean isYearInExcessPeriod(String accountingYear, YearType yearType,
                                          LocalDate excessStart, LocalDate excessEnd) {
        LocalDate yearStart = getYearStartDate(accountingYear, yearType);
        LocalDate yearEnd = getYearEndDate(accountingYear, yearType);
        return !yearEnd.isBefore(excessStart) && !yearStart.isAfter(excessEnd);
    }

    private LocalDate getYearStartDate(String accountingYear, YearType yearType) {
        try {
            int startYear = !accountingYear.contains("-") ?
                    Integer.parseInt(accountingYear) :
                    Integer.parseInt(accountingYear.split("-")[0]);

            switch (yearType) {
                case ACCOUNTING_YEAR:
                case TRANSITION_YEAR:
                    return LocalDate.of(startYear, 7, 1);
                case CALENDAR_YEAR:
                default:
                    return LocalDate.of(startYear, 1, 1);
            }
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private LocalDate getYearEndDate(String accountingYear, YearType yearType) {
        try {
            int startYear = !accountingYear.contains("-") ?
                    Integer.parseInt(accountingYear) :
                    Integer.parseInt(accountingYear.split("-")[0]);
            int endYear = !accountingYear.contains("-") ?
                    startYear :
                    Integer.parseInt(accountingYear.split("-")[1]);

            switch (yearType) {
                case ACCOUNTING_YEAR:
                    return LocalDate.of(endYear, 6, 30);
                case TRANSITION_YEAR:
                    return LocalDate.of(startYear, 12, 31);
                case CALENDAR_YEAR:
                default:
                    return LocalDate.of(startYear, 12, 31);
            }
        } catch (Exception e) {
            return LocalDate.of(LocalDate.now().getYear(), 12, 31);
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

    private YearType getYearType(String accountingYear) {
        try {
            if (!accountingYear.contains("-")) {
                int year = Integer.parseInt(accountingYear);
                if (year == TRANSITION_YEAR) {
                    return YearType.TRANSITION_YEAR;
                }
                return YearType.CALENDAR_YEAR;
            }
            return YearType.ACCOUNTING_YEAR;
        } catch (Exception e) {
            return YearType.CALENDAR_YEAR;
        }
    }

    private List<YearMonth> getMonthsInYear(String accountingYear, YearType yearType) {
        List<YearMonth> months = new ArrayList<>();
        try {
            int startYear = !accountingYear.contains("-") ?
                    Integer.parseInt(accountingYear) :
                    Integer.parseInt(accountingYear.split("-")[0]);
            int endYear = !accountingYear.contains("-") ?
                    startYear :
                    Integer.parseInt(accountingYear.split("-")[1]);

            switch (yearType) {
                case ACCOUNTING_YEAR:
                    for (int month = 7; month <= 12; month++) {
                        months.add(YearMonth.of(startYear, month));
                    }
                    for (int month = 1; month <= 6; month++) {
                        months.add(YearMonth.of(endYear, month));
                    }
                    break;
                case TRANSITION_YEAR:
                    for (int month = 7; month <= 12; month++) {
                        months.add(YearMonth.of(startYear, month));
                    }
                    break;
                case CALENDAR_YEAR:
                default:
                    for (int month = 1; month <= 12; month++) {
                        months.add(YearMonth.of(startYear, month));
                    }
                    break;
            }
        } catch (Exception e) {
            log.warn("Could not parse accounting year: {}", accountingYear);
        }
        return months;
    }

    private ComponentBalances addBalances(ComponentBalances a, ComponentBalances b) {
        return ComponentBalances.builder()
                .pfMc(n(a.getPfMc()).add(n(b.getPfMc())))
                .pfEc(n(a.getPfEc()).add(n(b.getPfEc())))
                .pfImc(n(a.getPfImc()).add(n(b.getPfImc())))
                .pfIec(n(a.getPfIec()).add(n(b.getPfIec())))
                .pMc(n(a.getPMc()).add(n(b.getPMc())))
                .pEc(n(a.getPEc()).add(n(b.getPEc())))
                .pImc(n(a.getPImc()).add(n(b.getPImc())))
                .pIec(n(a.getPIec()).add(n(b.getPIec())))
                .gc(n(a.getGc()).add(n(b.getGc())))
                .gic(n(a.getGic()).add(n(b.getGic())))
                .vc(n(a.getVc()).add(n(b.getVc())))
                .vic(n(a.getVic()).add(n(b.getVic())))
                .ivc(n(a.getIvc()).add(n(b.getIvc())))
                .igc(n(a.getIgc()).add(n(b.getIgc())))
                .build();
    }

    private BigDecimal calculateTotalContribution(ComponentBalances balances) {
        return n(balances.getPfMc())
                .add(n(balances.getPfEc()))
                .add(n(balances.getPMc()))
                .add(n(balances.getPEc()))
                .add(n(balances.getGc()))
                .add(n(balances.getVc()));
    }

    private BigDecimal calculateTotalInterest(ComponentBalances balances) {
        return n(balances.getPfImc())
                .add(n(balances.getPfIec()))
                .add(n(balances.getPImc()))
                .add(n(balances.getPIec()))
                .add(n(balances.getGic()))
                .add(n(balances.getVic()));
    }

    private ArrConfiguration getArrConfigurationWithFallback(String accountingYear) {
        if (accountingYear == null || accountingYear.isBlank()) {
            throw ClaimException.badRequest("Accounting year cannot be null or blank");
        }

        log.debug("Looking for ARR configuration for year: {}", accountingYear);

        try {
            Optional<ArrConfiguration> arrOpt = arrRepo.findByAccountingYear(accountingYear);
            if (arrOpt.isPresent()) {
                return arrOpt.get();
            }

            if (accountingYear.contains("-")) {
                String yearOnly = accountingYear.split("-")[0];
                Optional<ArrConfiguration> yearOnlyOpt = arrRepo.findByAccountingYear(yearOnly);
                if (yearOnlyOpt.isPresent()) {
                    return yearOnlyOpt.get();
                }
            }

            if (!accountingYear.contains("-")) {
                String yearRange = accountingYear + "-" + accountingYear;
                Optional<ArrConfiguration> yearRangeOpt = arrRepo.findByAccountingYear(yearRange);
                if (yearRangeOpt.isPresent()) {
                    return yearRangeOpt.get();
                }
            }

            try {
                int year = Integer.parseInt(accountingYear.split("-")[0]);
                for (int i = 1; i <= 5; i++) {
                    int prevYear = year - i;
                    String prevYearStr = String.valueOf(prevYear);
                    String prevYearRange = prevYearStr + "-" + prevYearStr;

                    Optional<ArrConfiguration> prevOpt = arrRepo.findByAccountingYear(prevYearStr);
                    if (prevOpt.isPresent()) {
                        log.debug("Using ARR from previous year: {}", prevYearStr);
                        return prevOpt.get();
                    }

                    Optional<ArrConfiguration> prevRangeOpt = arrRepo.findByAccountingYear(prevYearRange);
                    if (prevRangeOpt.isPresent()) {
                        log.debug("Using ARR from previous year range: {}", prevYearRange);
                        return prevRangeOpt.get();
                    }
                }
            } catch (Exception e) {
                log.warn("Could not parse year from: {}", accountingYear);
            }

            List<ArrConfiguration> allArr = arrRepo.findAll();
            if (!allArr.isEmpty()) {
                allArr.sort((a, b) -> {
                    try {
                        int aYear = Integer.parseInt(a.getAccountingYear().replace("-", ""));
                        int bYear = Integer.parseInt(b.getAccountingYear().replace("-", ""));
                        return Integer.compare(bYear, aYear);
                    } catch (Exception e) {
                        return b.getAccountingYear().compareTo(a.getAccountingYear());
                    }
                });
                ArrConfiguration latest = allArr.get(0);
                log.warn("Using latest available ARR from year: {} as fallback", latest.getAccountingYear());
                return latest;
            }

            throw ClaimException.badRequest("No ARR configuration found for year: " + accountingYear);

        } catch (Exception e) {
            throw ClaimException.internalError("Error getting ARR configuration: " + e.getMessage());
        }
    }

    private BigDecimal n(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    // ========== INNER CLASS FOR HEADER INFO ==========
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

    // ========== INNER CLASSES FOR RESULTS ==========
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpeningBalanceResult {
        private BigDecimal pensionEc;
        private BigDecimal pensionMc;
        private BigDecimal interestPensionEc;
        private BigDecimal interestPensionMc;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PensionRebuildResult {
        private BigDecimal adjustedPensionEc;
        private BigDecimal adjustedPensionMc;
        private BigDecimal adjustedPensionIec;
        private BigDecimal adjustedPensionImc;
        private BigDecimal excludedPrincipal;
        private BigDecimal excludedInterest;
        private BigDecimal excludedPec;
        private BigDecimal excludedPmc;
        private BigDecimal excludedPiec;
        private BigDecimal excludedPimc;
    }

    // ========== ENUM ==========
    public enum YearType {
        ACCOUNTING_YEAR,
        TRANSITION_YEAR,
        CALENDAR_YEAR
    }

    // ========== INNER CLASS FOR BALANCES ==========
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ComponentBalances {
        private BigDecimal pfMc;
        private BigDecimal pfEc;
        private BigDecimal pfImc;
        private BigDecimal pfIec;
        private BigDecimal pMc;
        private BigDecimal pEc;
        private BigDecimal pImc;
        private BigDecimal pIec;
        private BigDecimal gc;
        private BigDecimal gic;
        private BigDecimal vc;
        private BigDecimal vic;
        private BigDecimal ivc;
        private BigDecimal igc;
    }
}