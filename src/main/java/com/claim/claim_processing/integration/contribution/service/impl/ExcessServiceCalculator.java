package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.others.CutoffServiceMaster;
import com.claim.claim_processing.common.repository.others.CutoffServiceMasterRepository;
import com.claim.claim_processing.integration.contribution.entity.ArrConfiguration;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;
import com.claim.claim_processing.integration.contribution.repository.ArrConfigurationRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationDetailRepository;
import com.claim.claim_processing.integration.contribution.dto.ExcessServiceResultDto;
import com.claim.claim_processing.integration.contribution.dto.ExcessYearDetailDto;
import com.claim.claim_processing.integration.contribution.dto.EOLPeriodDTO;
import com.claim.claim_processing.integration.contribution.dto.ExcessMonthlyDetailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcessServiceCalculator {

    private final ContributionBifurcationDetailRepository contributionDetailRepo;
    private final CutoffServiceMasterRepository cutoffServiceMasterRepository;
    private final ArrConfigurationRepository arrRepo;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final int TRANSITION_YEAR = 2022;

    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public enum YearType {
        ACCOUNTING_YEAR,
        TRANSITION_YEAR,
        CALENDAR_YEAR
    }

    // ========== MAIN METHOD ==========
    public ExcessServiceResultDto calculateExcessService(
            MemberDetailResponseDto memberDetail) {

        log.info("=== START Excess Service Calculation ===");
        log.info("Member: {}",
                memberDetail != null ? memberDetail.getNppfNumber() : "null");

        try {
            // ========== 1. VALIDATE INPUT ==========
            if (memberDetail == null) {
                return buildErrorResult("NO_MEMBER_DETAIL", "Member details not found");
            }

            // ========== 2. CHECK PENSION ELIGIBILITY ==========
            if (!isPensionEligible(memberDetail.getMemberCategoryId())) {
                return buildErrorResult("NOT_PENSION_ELIGIBLE",
                        "Member is not pension-eligible (Private sector)");
            }

            // ========== 3. GET START DATE ==========
            LocalDate startDate = getStartDate(memberDetail);
            if (startDate == null) {
                return buildErrorResult("NO_START_DATE", "Member start date not found");
            }

            // ========== 4. Set Current DATE ==========
            LocalDate currentDate = LocalDate.now();

            // ========== 5. GET CUTOFF CONFIGURATION ==========
            CutoffServiceMaster config = getActiveCutoffConfig();
            if (config == null) {
                return buildErrorResult("NO_CUTOFF_CONFIG", "Cutoff service configuration not found");
            }

            int cutoffYears = config.getNumberOfYears();

            // ========== 6. GET ALL CONTRIBUTIONS ==========
            String cid = memberDetail.getIdentityNumber();
            String nppfNumber = memberDetail.getNppfNumber();

            List<ContributionBifurcationDetail> allContributions = contributionDetailRepo
                    .findByCidAndNppfNumberOrderByCreatedAtAsc(cid, nppfNumber);

            if (allContributions.isEmpty()) {
                return buildErrorResult("NO_CONTRIBUTIONS", "No contributions found for member");
            }

            // ========== 7. CALCULATE EOL PERIODS ==========
            List<EOLPeriodDTO> eolPeriods = calculateEOLPeriodsFromHistory(allContributions, startDate, currentDate);
            log.info("Total EOL periods found: {}", eolPeriods.size());

            if (!eolPeriods.isEmpty()) {
                log.info("EOL Period Details:");
                eolPeriods.forEach(period -> log.info("  • {} : {} months (Rule: {})",
                        period.getPeriod(), period.getEolMonths(), period.getRuleType()));
            } else {
                log.info("No EOL periods found");
            }

            // ========== 8. CALCULATE TOTAL EOL MONTHS ==========
            int totalEOLMonths = eolPeriods.stream()
                    .mapToInt(EOLPeriodDTO::getEolMonths)
                    .sum();
            log.info("Total EOL Months (all periods): {}", totalEOLMonths);

            // ========== 9. CALCULATE CUTOFF SERVICE DATE WITH EOL CONSIDERATION ==========
            int cutoffYear = currentDate.getYear();
            LocalDate cutoffYearStart = LocalDate.of(cutoffYear, 1, 1);
            LocalDate cutoffYearEnd = LocalDate.of(cutoffYear, 12, 31);

            log.info("Cutoff Year: {}, Cutoff Year Start: {}, Cutoff Year End: {}",
                    cutoffYear, cutoffYearStart, cutoffYearEnd);

            int eolMonthsBeforeCutoffYear = getEOLMonthsBeforeDate(eolPeriods, cutoffYearStart);
            log.info("EOL Months Before Cutoff Year (before {}): {}", cutoffYearStart, eolMonthsBeforeCutoffYear);

            int eolMonthsDuringCutoffYear = getEOLMonthsInDateRange(eolPeriods, cutoffYearStart, cutoffYearEnd);
            log.info("EOL Months During Cutoff Year ({}): {} (NOT deducted from cutoff)",
                    cutoffYear, eolMonthsDuringCutoffYear);

            int eolMonthsAfterCutoffYear = getEOLMonthsAfterDate(eolPeriods, cutoffYearEnd);
            log.info("EOL Months After Cutoff Year: {}", eolMonthsAfterCutoffYear);

            int additionalMonthsForEOL = eolMonthsBeforeCutoffYear;
            int totalMonthsNeeded = (cutoffYears * 12) + additionalMonthsForEOL;

            log.info("Cutoff Years: {} months, EOL Before Cutoff: {} months, Total Months Needed: {}",
                    (cutoffYears * 12), additionalMonthsForEOL, totalMonthsNeeded);

            LocalDate cutoffServiceDate = startDate.plusMonths(totalMonthsNeeded);
            log.info("Cutoff Service Date (with EOL adjustment): {}", cutoffServiceDate);
            log.info("Original Cutoff (without EOL): {}", startDate.plusYears(cutoffYears));

            // ========== 10. CHECK IF ELIGIBLE FOR EXCESS SERVICE ==========
            if (currentDate.isBefore(cutoffServiceDate)) {
                long monthsShort = calculateTotalMonths(currentDate, cutoffServiceDate);

                return ExcessServiceResultDto.builder()
                        .isEligible(false)
                        .cutoffServiceDate(cutoffServiceDate)
                        .cutoffYears(cutoffYears)
                        .totalEOLMonths(totalEOLMonths)
                        .eolMonthsBeforeCutoffYear(eolMonthsBeforeCutoffYear)
                        .eolMonthsDuringCutoffYear(eolMonthsDuringCutoffYear)
                        .eolMonthsAfterCutoffYear(eolMonthsAfterCutoffYear)
                        .monthsShort(monthsShort)
                        .status("NOT_ELIGIBLE")
                        .message(String.format(
                                "Member has not crossed the cutoff service date. Need %d more months of service",
                                monthsShort))
                        .build();
            }

            // ========== 11. EXCESS PERIOD ==========
            LocalDate excessStart = cutoffServiceDate.plusMonths(1);
            LocalDate excessEnd = currentDate;

            log.info("Excess period: {} to {}", excessStart, excessEnd);

            // ========== 12. FILTER EXCESS CONTRIBUTIONS ==========
            List<ContributionBifurcationDetail> excessContributions = allContributions.stream()
                    .filter(c -> {
                        LocalDate date = c.getCreatedAt().toLocalDate();
                        return !date.isBefore(excessStart) && !date.isAfter(excessEnd);
                    })
                    .sorted(Comparator.comparing(ContributionBifurcationDetail::getCreatedAt))
                    .collect(Collectors.toList());

            log.info("Found {} contributions in excess period", excessContributions.size());

            Map<String, ContributionBifurcationDetail> contributionMap = excessContributions.stream()
                    .collect(Collectors.toMap(
                            c -> getMonthYearKey(c.getCreatedAt().toLocalDate()),
                            c -> c,
                            (existing, replacement) -> existing));

            // ========== 13. GET ACCOUNTING YEARS IN EXCESS PERIOD ==========
            List<String> accountingYears = getAccountingYearsInPeriod(excessStart, excessEnd);
            log.info("Accounting Years in excess period: {}", accountingYears);

            // ========== 14. INITIALIZE OPENING BALANCES ==========
            // Component-wise opening balances start at ZERO
            BigDecimal openingPmcPrincipal = BigDecimal.ZERO; // P_MC Principal
            BigDecimal openingPecPrincipal = BigDecimal.ZERO; // P_EC Principal
            BigDecimal openingPmcInterest = BigDecimal.ZERO; // P_IMC (Interest on P_MC)
            BigDecimal openingPecInterest = BigDecimal.ZERO; // P_IEC (Interest on P_EC)

            List<ExcessYearDetailDto> yearDetails = new ArrayList<>();
            List<ExcessMonthlyDetailDto> allMonthlyDetails = new ArrayList<>();

            BigDecimal grandTotalContributions = BigDecimal.ZERO;
            BigDecimal grandTotalInterest = BigDecimal.ZERO;
            int totalEOLMonthsInExcess = 0;

            // ========== 15. PROCESS EACH YEAR ==========
            // ========== 15. PROCESS EACH YEAR ==========
            for (String year : accountingYears) {
                YearType yearType = getYearType(year);

                log.info("Processing Year: {}, Type: {}", year, yearType);

                // ================================================================
                // ✅ GET ARR CONFIGURATION WITH ENHANCED FALLBACK LOGIC
                // ================================================================
                ArrConfiguration arrConfig = getArrConfigurationWithFallback(year);

                if (arrConfig == null) {
                    log.warn("ARR configuration not found for year: {}, skipping", year);
                    continue;
                }

                BigDecimal rate = arrConfig.getArrRate() != null ? arrConfig.getArrRate() : BigDecimal.ZERO;
                int yearBasis = arrConfig.getYearBasis();

                log.info("Year: {}, Rate: {}%, Year Basis: {} days",
                        year, rate.multiply(HUNDRED), yearBasis);

                // Get year start and end dates
                LocalDate yearStartDate = getYearStartDate(year, yearType);
                LocalDate yearEndDate = getYearEndDate(year, yearType);

                log.info("Year Start: {}, Year End: {}", yearStartDate, yearEndDate);

                // ========== CALCULATE DAYS FOR OPENING BALANCE INTEREST ==========
                int daysForOpening;
                if (yearDetails.isEmpty()) {
                    // First year of excess - from excessStart to yearEnd
                    LocalDate effectiveStart = excessStart.isAfter(yearStartDate) ? excessStart : yearStartDate;
                    daysForOpening = (int) ChronoUnit.DAYS.between(effectiveStart, yearEndDate) + 1;
                    log.info("First year - days for opening balance interest: {} (from {} to {})",
                            daysForOpening, effectiveStart, yearEndDate);
                } else {
                    // Subsequent years - full year
                    daysForOpening = calculateDaysForIOB(year, yearType);
                    log.info("Subsequent year - days for opening balance interest: {} (full year)", daysForOpening);
                }

                // Ensure daysForOpening is not negative
                if (daysForOpening < 0) {
                    daysForOpening = 0;
                }

                // ========== GET MONTHS FOR THIS YEAR WITHIN EXCESS PERIOD ==========
                List<YearMonth> monthsInYear = getMonthsInYear(year, yearType);

                List<YearMonth> filteredMonths = monthsInYear.stream()
                        .filter(ym -> {
                            LocalDate monthStart = ym.atDay(1);
                            LocalDate monthEnd = ym.atEndOfMonth();
                            return !monthEnd.isBefore(excessStart) && !monthStart.isAfter(excessEnd);
                        })
                        .collect(Collectors.toList());

                log.info("Months in excess period for year {}: {}", year, filteredMonths.size());

                // ================================================================
                // PROCESS EACH MONTH
                // ================================================================
                List<ExcessMonthlyDetailDto> monthlyDetails = new ArrayList<>();

                // Yearly accumulators
                BigDecimal yearlyPmc = BigDecimal.ZERO;
                BigDecimal yearlyPec = BigDecimal.ZERO;
                BigDecimal yearlyPimc = BigDecimal.ZERO;
                BigDecimal yearlyPiec = BigDecimal.ZERO;

                int eolMonthsInYear = 0;

                for (YearMonth yearMonth : filteredMonths) {
                    String monthKey = getMonthYearKey(yearMonth.atDay(1));

                    ContributionBifurcationDetail detail = contributionMap.get(monthKey);

                    BigDecimal pmc = BigDecimal.ZERO;
                    BigDecimal pec = BigDecimal.ZERO;
                    BigDecimal pimc = BigDecimal.ZERO;
                    BigDecimal piec = BigDecimal.ZERO;
                    int days = 0;
                    LocalDate invoiceDate = null;

                    log.info("Processing Month: {}, MonthKey: {}, Has Contribution: {}",
                            yearMonth, monthKey, detail != null);

                    if (detail != null) {
                        pmc = n(detail.getPensionMc());
                        pec = n(detail.getPensionEc());

                        invoiceDate = detail.getCreatedAt().toLocalDate();

                        log.info("  Invoice Date: {}", invoiceDate);
                        log.info("  Year End Date: {}", yearEndDate);

                        // Calculate days from invoice date to year end date
                        days = (int) ChronoUnit.DAYS.between(invoiceDate, yearEndDate);

                        log.info("  Days between invoice date and year end: {}", days);

                        if (days < 0) {
                            log.warn("  Negative days detected! Setting to 0");
                            days = 0;
                        }

                        // Calculate interest for PMC and PEC separately
                        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
                        BigDecimal factor = BigDecimal.valueOf(days)
                                .divide(BigDecimal.valueOf(yearBasis), 10, RM);

                        pimc = pmc
                                .multiply(rateFactor)
                                .multiply(factor)
                                .setScale(2, RM);

                        piec = pec
                                .multiply(rateFactor)
                                .multiply(factor)
                                .setScale(2, RM);

                        // Accumulate yearly totals
                        yearlyPmc = yearlyPmc.add(pmc);
                        yearlyPec = yearlyPec.add(pec);
                        yearlyPimc = yearlyPimc.add(pimc);
                        yearlyPiec = yearlyPiec.add(piec);

                        log.info("  Final: Days={}, PMC={}, PEC={}, PIMC={}, PIEC={}",
                                days, pmc, pec, pimc, piec);

                    } else {
                        eolMonthsInYear++;
                        log.info("  Month {}: EOL Month", yearMonth);
                    }

                    // Build monthly detail
                    ExcessMonthlyDetailDto monthlyDetail = ExcessMonthlyDetailDto.builder()
                            .dueMonth(String.valueOf(yearMonth.getMonthValue()))
                            .invoiceDate(invoiceDate != null ? invoiceDate : yearMonth.atDay(1))
                            .pmc(pmc)
                            .pec(pec)
                            .pimc(pimc)
                            .piec(piec)
                            .days(days)
                            .interestRate(rate)
                            .yearBasis(yearBasis)
                            .build();

                    monthlyDetails.add(monthlyDetail);
                    allMonthlyDetails.add(monthlyDetail);
                }

                // ================================================================
                // CALCULATE YEAR SUMMARY WITH COMPONENT-WISE OPENING BALANCES
                // ================================================================

                // Rate factor for interest calculations
                BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
                BigDecimal daysFactor = BigDecimal.valueOf(daysForOpening)
                        .divide(BigDecimal.valueOf(yearBasis), 10, RM);

                // Interest on Opening Balances (Component-wise)
                BigDecimal interestOnPmcOpening = openingPmcPrincipal
                        .multiply(rateFactor)
                        .multiply(daysFactor)
                        .setScale(2, RM);

                BigDecimal interestOnPecOpening = openingPecPrincipal
                        .multiply(rateFactor)
                        .multiply(daysFactor)
                        .setScale(2, RM);

                BigDecimal interestOnPimcOpening = openingPmcInterest
                        .multiply(rateFactor)
                        .multiply(daysFactor)
                        .setScale(2, RM);

                BigDecimal interestOnPiecOpening = openingPecInterest
                        .multiply(rateFactor)
                        .multiply(daysFactor)
                        .setScale(2, RM);

                // During The Year totals (already accumulated from monthly processing)
                BigDecimal duringTheYearPmc = yearlyPmc;
                BigDecimal duringTheYearPec = yearlyPec;
                BigDecimal duringTheYearPimc = yearlyPimc;
                BigDecimal duringTheYearPiec = yearlyPiec;

                // Closing Balances (Component-wise)
                BigDecimal closingPmcBalance = openingPmcPrincipal
                        .add(interestOnPmcOpening)
                        .add(duringTheYearPmc);

                BigDecimal closingPecBalance = openingPecPrincipal
                        .add(interestOnPecOpening)
                        .add(duringTheYearPec);

                BigDecimal closingPimcBalance = openingPmcInterest
                        .add(interestOnPimcOpening)
                        .add(duringTheYearPimc);

                BigDecimal closingPiecBalance = openingPecInterest
                        .add(interestOnPiecOpening)
                        .add(duringTheYearPiec);

                // Total yearly contributions and interest
                BigDecimal yearlyContributions = yearlyPmc.add(yearlyPec);
                BigDecimal yearlyInterest = yearlyPimc.add(yearlyPiec);

                log.info("========== Year {} Summary ==========", year);
                log.info("Opening Balances:");
                log.info("  PMC: {}, PEC: {}, PIMC: {}, PIEC: {}",
                        openingPmcPrincipal, openingPecPrincipal, openingPmcInterest, openingPecInterest);
                log.info("Interest on Opening ({} days):", daysForOpening);
                log.info("  PMC: {}, PEC: {}, PIMC: {}, PIEC: {}",
                        interestOnPmcOpening, interestOnPecOpening, interestOnPimcOpening, interestOnPiecOpening);
                log.info("During The Year:");
                log.info("  PMC: {}, PEC: {}, PIMC: {}, PIEC: {}",
                        duringTheYearPmc, duringTheYearPec, duringTheYearPimc, duringTheYearPiec);
                log.info("Closing Balances:");
                log.info("  PMC: {}, PEC: {}, PIMC: {}, PIEC: {}",
                        closingPmcBalance, closingPecBalance, closingPimcBalance, closingPiecBalance);

                // ================================================================
                // BUILD YEAR DETAIL DTO
                // ================================================================

                ExcessYearDetailDto yearDetail = ExcessYearDetailDto.builder()
                        .accountingYear(year)
                        .yearType(yearType.name())

                        // Opening balances
                        .openingPmcBalance(openingPmcPrincipal)
                        .openingPecBalance(openingPecPrincipal)
                        .openingPimcBalance(openingPmcInterest)
                        .openingPiecBalance(openingPecInterest)

                        // Interest on opening
                        .interestOnPmcOpening(interestOnPmcOpening)
                        .interestOnPecOpening(interestOnPecOpening)
                        .interestOnPimcOpening(interestOnPimcOpening)
                        .interestOnPiecOpening(interestOnPiecOpening)

                        // During the year
                        .duringTheYearPmc(duringTheYearPmc)
                        .duringTheYearPec(duringTheYearPec)
                        .duringTheYearPimc(duringTheYearPimc)
                        .duringTheYearPiec(duringTheYearPiec)

                        // Closing balances
                        .closingPmcBalance(closingPmcBalance)
                        .closingPecBalance(closingPecBalance)
                        .closingPimcBalance(closingPimcBalance)
                        .closingPiecBalance(closingPiecBalance)

                        // Interest rates
                        .interestPmcRate(rate)
                        .interestPecRate(rate)
                        .interestPimcRate(rate)
                        .interestPiecRate(rate)

                        // Calculation details
                        .interestDate(yearStartDate)
                        .yearBasis(yearBasis)
                        .daysInYear(daysForOpening)

                        // EOL
                        .eolMonthsInYear(eolMonthsInYear)

                        // Yearly totals
                        .yearlyContributions(yearlyContributions)
                        .yearlyInterest(yearlyInterest)

                        // Monthly details
                        .monthlyDetails(monthlyDetails)
                        .build();

                yearDetails.add(yearDetail);

                // ================================================================
                // UPDATE OPENING BALANCES FOR NEXT YEAR (Component-wise)
                // ================================================================
                openingPmcPrincipal = closingPmcBalance;
                openingPecPrincipal = closingPecBalance;
                openingPmcInterest = closingPimcBalance;
                openingPecInterest = closingPiecBalance;

                grandTotalContributions = grandTotalContributions.add(yearlyContributions);
                grandTotalInterest = grandTotalInterest.add(yearlyInterest);
                totalEOLMonthsInExcess += eolMonthsInYear;
            }
            // ========== 16. FINAL TOTAL ==========
            BigDecimal totalExcessAmount = grandTotalContributions.add(grandTotalInterest);
            totalExcessAmount = totalExcessAmount.setScale(2, RM);

            // Get last year's closing balances for the main summary
            ExcessYearDetailDto lastYear = yearDetails.isEmpty() ? null : yearDetails.get(yearDetails.size() - 1);

            log.info("========== FINAL EXCESS SERVICE RESULT ==========");
            log.info("Total Excess Amount: {}", totalExcessAmount);
            log.info("Total Years Processed: {}", yearDetails.size());
            if (lastYear != null) {
                log.info("Final Closing Balances - PMC: {}, PEC: {}, PIMC: {}, PIEC: {}",
                        lastYear.getClosingPmcBalance(),
                        lastYear.getClosingPecBalance(),
                        lastYear.getClosingPimcBalance(),
                        lastYear.getClosingPiecBalance());
            }

            // ========== 17. BUILD RESPONSE ==========
            ExcessServiceResultDto.ExcessServiceResultDtoBuilder builder = ExcessServiceResultDto.builder()
                    .isEligible(true)
                    .totalExcessAmount(totalExcessAmount)
                    .cutoffServiceDate(cutoffServiceDate)
                    .cutoffYears(cutoffYears)
                    .excessStartDate(excessStart)
                    .excessEndDate(excessEnd)
                    .totalEOLMonths(totalEOLMonths)
                    .eolMonthsBeforeCutoffYear(eolMonthsBeforeCutoffYear)
                    .eolMonthsDuringCutoffYear(eolMonthsDuringCutoffYear)
                    .eolMonthsAfterCutoffYear(eolMonthsAfterCutoffYear)
                    .eolMonthsInExcess(totalEOLMonthsInExcess)
                    .totalContributionsInExcess(grandTotalContributions)
                    .totalInterestInExcess(grandTotalInterest)
                    .yearDetails(yearDetails)
                    .monthlyDetails(allMonthlyDetails)
                    .status("CALCULATED")
                    .message("Excess service calculated successfully as of " + excessEnd);

            return builder.build();

        } catch (Exception e) {
            log.error("Error calculating excess service: {}", e.getMessage(), e);
            return buildErrorResult("ERROR", "Error calculating excess service: " + e.getMessage());
        }
    }

    /**
     * Get ARR configuration with enhanced fallback logic.
     * Tries multiple formats and fallback options.
     */
    private ArrConfiguration getArrConfigurationWithFallback(String accountingYear) {
        if (accountingYear == null || accountingYear.isBlank()) {
            return null;
        }

        log.debug("Looking for ARR configuration for year: {}", accountingYear);

        try {
            // ================================================================
            // 1. TRY EXACT MATCH FIRST
            // ================================================================
            Optional<ArrConfiguration> arrOpt = arrRepo.findByAccountingYear(accountingYear);
            if (arrOpt.isPresent()) {
                log.debug("✅ Found exact ARR configuration for year: {}", accountingYear);
                return arrOpt.get();
            }

            log.warn("No exact ARR configuration found for year: {}, trying fallback options", accountingYear);

            // ================================================================
            // 2. TRY DIFFERENT FORMATS
            // ================================================================

            // If it's in format "YYYY-YYYY", try "YYYY"
            if (accountingYear.contains("-")) {
                String[] parts = accountingYear.split("-");
                String yearOnly = parts[0];

                Optional<ArrConfiguration> yearOnlyOpt = arrRepo.findByAccountingYear(yearOnly);
                if (yearOnlyOpt.isPresent()) {
                    log.info("✅ Found ARR configuration using year only: {}", yearOnly);
                    return yearOnlyOpt.get();
                }
            }

            // If it's in format "YYYY", try "YYYY-YYYY"
            if (!accountingYear.contains("-")) {
                String yearRange = accountingYear + "-" + accountingYear;
                Optional<ArrConfiguration> yearRangeOpt = arrRepo.findByAccountingYear(yearRange);
                if (yearRangeOpt.isPresent()) {
                    log.info("✅ Found ARR configuration using year range: {}", yearRange);
                    return yearRangeOpt.get();
                }
            }

            // ================================================================
            // 3. TRY PREVIOUS YEARS (UP TO 5 YEARS BACK)
            // ================================================================
            try {
                int year = Integer.parseInt(accountingYear.split("-")[0]);

                for (int i = 1; i <= 5; i++) {
                    int previousYear = year - i;
                    String previousYearStr = String.valueOf(previousYear);
                    String previousYearRange = previousYearStr + "-" + previousYearStr;

                    // Try "YYYY" format
                    Optional<ArrConfiguration> prevOpt = arrRepo.findByAccountingYear(previousYearStr);
                    if (prevOpt.isPresent()) {
                        log.info("✅ Using ARR config from previous year: {} ({} years back)",
                                previousYearStr, i);
                        return prevOpt.get();
                    }

                    // Try "YYYY-YYYY" format
                    Optional<ArrConfiguration> prevRangeOpt = arrRepo.findByAccountingYear(previousYearRange);
                    if (prevRangeOpt.isPresent()) {
                        log.info("✅ Using ARR config from previous year range: {} ({} years back)",
                                previousYearRange, i);
                        return prevRangeOpt.get();
                    }
                }
            } catch (Exception e) {
                log.warn("Could not parse year from: {}", accountingYear);
            }

            // ================================================================
            // 4. GET THE LATEST AVAILABLE ARR CONFIGURATION
            // ================================================================
            List<ArrConfiguration> allArr = arrRepo.findAll();
            if (!allArr.isEmpty()) {
                // Sort by year descending (latest first)
                allArr.sort((a, b) -> {
                    String yearA = a.getAccountingYear();
                    String yearB = b.getAccountingYear();
                    try {
                        int aYear = Integer.parseInt(yearA.replace("-", ""));
                        int bYear = Integer.parseInt(yearB.replace("-", ""));
                        return Integer.compare(bYear, aYear);
                    } catch (Exception e) {
                        return b.getAccountingYear().compareTo(a.getAccountingYear());
                    }
                });

                ArrConfiguration latest = allArr.get(0);
                log.info("✅ Using latest available ARR config from year: {}", latest.getAccountingYear());
                return latest;
            }

            // ================================================================
            // 5. USE DEFAULT VALUES
            // ================================================================
            log.warn("❌ No ARR configuration found in database, using default values");
            return null;

        } catch (Exception e) {
            log.error("Error getting ARR config for {}: {}", accountingYear, e.getMessage());
            return null;
        }
    }
    // ========== EOL HELPER METHODS ==========

    private List<EOLPeriodDTO> getEOLPeriodsBeforeDate(List<EOLPeriodDTO> eolPeriods, LocalDate date) {
        if (eolPeriods.isEmpty() || date == null) {
            return Collections.emptyList();
        }

        return eolPeriods.stream()
                .filter(period -> {
                    LocalDate periodEndDate = LocalDate.parse(period.getEndDate());
                    return periodEndDate.isBefore(date);
                })
                .collect(Collectors.toList());
    }

    private List<EOLPeriodDTO> getEOLPeriodsInDateRange(List<EOLPeriodDTO> eolPeriods, LocalDate start, LocalDate end) {
        if (eolPeriods.isEmpty() || start == null || end == null) {
            return Collections.emptyList();
        }

        return eolPeriods.stream()
                .filter(period -> {
                    LocalDate periodStart = LocalDate.parse(period.getStartDate());
                    LocalDate periodEnd = LocalDate.parse(period.getEndDate());
                    return (periodStart.isAfter(start) || periodStart.isEqual(start))
                            && (periodEnd.isBefore(end) || periodEnd.isEqual(end));
                })
                .collect(Collectors.toList());
    }

    private List<EOLPeriodDTO> getEOLPeriodsAfterDate(List<EOLPeriodDTO> eolPeriods, LocalDate date) {
        if (eolPeriods.isEmpty() || date == null) {
            return Collections.emptyList();
        }

        return eolPeriods.stream()
                .filter(period -> {
                    LocalDate periodStartDate = LocalDate.parse(period.getStartDate());
                    return periodStartDate.isAfter(date);
                })
                .collect(Collectors.toList());
    }

    private int getEOLMonthsBeforeDate(List<EOLPeriodDTO> eolPeriods, LocalDate date) {
        return getEOLPeriodsBeforeDate(eolPeriods, date).stream()
                .mapToInt(EOLPeriodDTO::getEolMonths)
                .sum();
    }

    private int getEOLMonthsInDateRange(List<EOLPeriodDTO> eolPeriods, LocalDate start, LocalDate end) {
        return getEOLPeriodsInDateRange(eolPeriods, start, end).stream()
                .mapToInt(EOLPeriodDTO::getEolMonths)
                .sum();
    }

    private int getEOLMonthsAfterDate(List<EOLPeriodDTO> eolPeriods, LocalDate date) {
        return getEOLPeriodsAfterDate(eolPeriods, date).stream()
                .mapToInt(EOLPeriodDTO::getEolMonths)
                .sum();
    }

    private long calculateTotalMonths(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return 0;
        }
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);
        return (endMonth.getYear() - startMonth.getYear()) * 12L
                + (endMonth.getMonthValue() - startMonth.getMonthValue());
    }

    // ========== HELPER METHODS ==========

    private boolean isPensionEligible(String memberCategoryId) {
        return memberCategoryId != null && !"04".equals(memberCategoryId.trim());
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

    public List<EOLPeriodDTO> calculateEOLPeriodsFromHistory(
            List<ContributionBifurcationDetail> allContributions,
            LocalDate startDate,
            LocalDate endDate) {

        List<EOLPeriodDTO> eolPeriods = new ArrayList<>();

        if (allContributions.isEmpty() || startDate == null || endDate == null) {
            return eolPeriods;
        }

        Set<String> contributionMonths = allContributions.stream()
                .filter(c -> c.getCreatedAt() != null)
                .map(c -> c.getCreatedAt().toLocalDate().getYear() + "-" +
                        String.format("%02d", c.getCreatedAt().toLocalDate().getMonthValue()))
                .collect(Collectors.toSet());

        int transactionYear = allContributions.stream()
                .filter(c -> c.getCreatedAt() != null)
                .map(c -> c.getCreatedAt().toLocalDate().getYear())
                .min(Integer::compareTo)
                .orElse(2022);

        boolean isNewRule = transactionYear >= 2022;
        String ruleType = isNewRule ? "Calendar Year (Jan-Dec)" : "Financial Year (Jul-Jun)";

        YearMonth current = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        YearMonth periodStart = null;
        int eolMonthsInPeriod = 0;

        while (!current.isAfter(endMonth)) {
            String monthKey = current.getYear() + "-" + String.format("%02d", current.getMonthValue());

            boolean isEOL = false;
            if (!contributionMonths.contains(monthKey)) {
                if (!shouldBeExcludedFromEOL(current, transactionYear, isNewRule)) {
                    isEOL = true;
                }
            }

            if (isEOL) {
                if (periodStart == null) {
                    periodStart = current;
                    eolMonthsInPeriod = 1;
                } else {
                    eolMonthsInPeriod++;
                }
            } else {
                if (periodStart != null && eolMonthsInPeriod > 0) {
                    eolPeriods.add(createEOLPeriodDTO(periodStart, current.minusMonths(1),
                            eolMonthsInPeriod, ruleType));
                    periodStart = null;
                    eolMonthsInPeriod = 0;
                }
            }

            current = current.plusMonths(1);
        }

        if (periodStart != null && eolMonthsInPeriod > 0) {
            eolPeriods.add(createEOLPeriodDTO(periodStart, current.minusMonths(1),
                    eolMonthsInPeriod, ruleType));
        }

        return eolPeriods;
    }

    private EOLPeriodDTO createEOLPeriodDTO(YearMonth start, YearMonth end, int eolMonths, String ruleType) {
        LocalDate startDate = start.atDay(1);
        LocalDate endDate = end.atEndOfMonth();

        String periodStr = start.format(MONTH_YEAR_FORMATTER) + " - " + end.format(MONTH_YEAR_FORMATTER);

        return EOLPeriodDTO.builder()
                .period(periodStr)
                .eolMonths(eolMonths)
                .startDate(startDate.format(DATE_FORMATTER))
                .endDate(endDate.format(DATE_FORMATTER))
                .ruleType(ruleType)
                .build();
    }

    private boolean shouldBeExcludedFromEOL(YearMonth month, int transactionYear, boolean isNewRule) {
        if (isNewRule) {
            return month.getYear() != transactionYear;
        } else {
            if (month.getYear() < transactionYear) {
                return true;
            }
            if (month.getYear() > transactionYear) {
                if (month.getYear() == transactionYear + 1 && month.getMonthValue() <= 6) {
                    return false;
                }
                return true;
            }
            if (month.getMonthValue() < 7) {
                return true;
            }
            return false;
        }
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

            String[] parts = accountingYear.split("-");
            int startYear = Integer.parseInt(parts[0]);
            int endYear = Integer.parseInt(parts[1]);

            if (startYear == endYear) {
                if (startYear == TRANSITION_YEAR) {
                    return YearType.TRANSITION_YEAR;
                }
                return YearType.CALENDAR_YEAR;
            }
            return YearType.ACCOUNTING_YEAR;
        } catch (Exception e) {
            return YearType.CALENDAR_YEAR;
        }
    }

    private LocalDate getYearStartDate(String accountingYear, YearType yearType) {
        try {
            int startYear;
            if (!accountingYear.contains("-")) {
                startYear = Integer.parseInt(accountingYear);
            } else {
                startYear = Integer.parseInt(accountingYear.split("-")[0]);
            }

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
            int startYear;
            int endYear;

            if (!accountingYear.contains("-")) {
                startYear = Integer.parseInt(accountingYear);
                endYear = startYear;
            } else {
                String[] parts = accountingYear.split("-");
                startYear = Integer.parseInt(parts[0]);
                endYear = Integer.parseInt(parts[1]);
            }

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

    private List<YearMonth> getMonthsInYear(String accountingYear, YearType yearType) {
        List<YearMonth> months = new ArrayList<>();
        try {
            int startYear;
            int endYear;

            if (!accountingYear.contains("-")) {
                startYear = Integer.parseInt(accountingYear);
                endYear = startYear;
            } else {
                String[] parts = accountingYear.split("-");
                startYear = Integer.parseInt(parts[0]);
                endYear = Integer.parseInt(parts[1]);
            }

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

    private String getAccountingYearForDate(LocalDate date) {
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

    private List<String> getAccountingYearsInPeriod(LocalDate start, LocalDate end) {
        List<String> years = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            String year = getAccountingYearForDate(current);
            if (!years.contains(year)) {
                years.add(year);
            }
            current = current.plusMonths(1);
        }
        return years;
    }

    private String getMonthYearKey(LocalDate date) {
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }

    private ArrConfiguration getArrConfiguration(String accountingYear) {
        try {
            Optional<ArrConfiguration> arrOpt = arrRepo.findByAccountingYear(accountingYear);
            if (arrOpt.isPresent()) {
                return arrOpt.get();
            } else {
                log.warn("No ARR configuration found for year: {}", accountingYear);
                return null;
            }
        } catch (Exception e) {
            log.warn("Error getting ARR config for {}: {}", accountingYear, e.getMessage());
            return null;
        }
    }

    private int calculateDaysForIOB(String accountingYear, YearType yearType) {
        try {
            int startYear;
            int endYear;

            if (!accountingYear.contains("-")) {
                startYear = Integer.parseInt(accountingYear);
                endYear = startYear;
            } else {
                String[] parts = accountingYear.split("-");
                startYear = Integer.parseInt(parts[0]);
                endYear = Integer.parseInt(parts[1]);
            }

            switch (yearType) {
                case ACCOUNTING_YEAR:
                    LocalDate yearStart = LocalDate.of(startYear, 7, 1);
                    LocalDate yearEnd = LocalDate.of(endYear, 6, 30);
                    return (int) ChronoUnit.DAYS.between(yearStart, yearEnd) + 1;

                case TRANSITION_YEAR:
                    LocalDate transStart = LocalDate.of(startYear, 7, 1);
                    LocalDate transEnd = LocalDate.of(startYear, 12, 31);
                    return (int) ChronoUnit.DAYS.between(transStart, transEnd) + 1;

                case CALENDAR_YEAR:
                default:
                    LocalDate calStart = LocalDate.of(startYear, 1, 1);
                    LocalDate calEnd = LocalDate.of(startYear, 12, 31);
                    return (int) ChronoUnit.DAYS.between(calStart, calEnd) + 1;
            }
        } catch (Exception e) {
            return 365;
        }
    }

    private BigDecimal n(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private ExcessServiceResultDto buildErrorResult(String status, String message) {
        return ExcessServiceResultDto.builder()
                .isEligible(false)
                .status(status)
                .message(message)
                .build();
    }
}