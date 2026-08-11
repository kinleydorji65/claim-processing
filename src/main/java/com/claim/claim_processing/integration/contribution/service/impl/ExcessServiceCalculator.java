package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.others.CutoffServiceMaster;
import com.claim.claim_processing.common.repository.others.CutoffServiceMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.entity.ArrConfiguration;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationHeader;
import com.claim.claim_processing.integration.contribution.repository.ArrConfigurationRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationDetailRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationHeaderRepository;
import com.claim.claim_processing.integration.contribution.dto.ExcessServiceResultDto;
import com.claim.claim_processing.integration.contribution.dto.ExcessYearDetailDto;
import com.claim.claim_processing.integration.contribution.dto.CategoryInfoDto;
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
    private final ContributionBifurcationHeaderRepository contributionHeaderRepo;
    private final CutoffServiceMasterRepository cutoffServiceMasterRepository;
    private final ArrConfigurationRepository arrRepo;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final int TRANSITION_YEAR = 2022;

    // ================================================================
    // SECURITY FORCES CUTOFF DATE - July 1, 2024
    // ================================================================
    private static final LocalDate SECURITY_FORCES_CUTOFF_DATE = LocalDate.of(2024, 7, 1);

    // ================================================================
    // CATEGORY CONSTANTS
    // ================================================================
    private static final String CATEGORY_CIVIL = "01";
    private static final String CATEGORY_SECURITY_FORCES = "03";
    private static final String CATEGORY_PENSION_INELIGIBLE = "04";

    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

    public enum YearType {
        ACCOUNTING_YEAR,
        TRANSITION_YEAR,
        CALENDAR_YEAR
    }

    // ================================================================
    // CATEGORY TYPE ENUM
    // ================================================================
    public enum CategoryType {
        CIVIL,
        SECURITY_FORCES,
        PENSION_INELIGIBLE
    }

    // ================================================================
    // CATEGORY DETECTION METHODS
    // ================================================================

    private CategoryType getCategoryType(String categoryId) {
        if (categoryId == null) {
            return CategoryType.PENSION_INELIGIBLE;
        }

        String trimmedId = categoryId.trim();

        if (CATEGORY_PENSION_INELIGIBLE.equals(trimmedId)) {
            return CategoryType.PENSION_INELIGIBLE;
        } else if (CATEGORY_SECURITY_FORCES.equals(trimmedId)) {
            return CategoryType.SECURITY_FORCES;
        } else {
            return CategoryType.CIVIL;
        }
    }

    private boolean isSecurityForces(String categoryId) {
        if (categoryId == null) {
            return false;
        }
        return CATEGORY_SECURITY_FORCES.equals(categoryId.trim());
    }

    private String getCategoryDisplayName(String categoryId) {
        if (categoryId == null) {
            return "Unknown";
        }

        String trimmedId = categoryId.trim();

        switch (trimmedId) {
            case CATEGORY_CIVIL:
                return "Civil";
            case CATEGORY_SECURITY_FORCES:
                return "Security Forces (Armed Forces/Police/Royal Body Guard)";
            case CATEGORY_PENSION_INELIGIBLE:
                return "Pension Ineligible";
            default:
                return "Unknown (" + trimmedId + ")";
        }
    }

    // ========== MAIN METHOD ==========
    public ExcessServiceResultDto calculateExcessService(
            MemberDetailResponseDto memberDetail) {

        log.info("=== START Excess Service Calculation ===");
        log.info("Member: {}",
                memberDetail != null ? memberDetail.getNppfNumber() : "null");

        try {
            if (memberDetail == null) {
                return null;
            }

            String categoryId = memberDetail.getMemberCategoryId();
            CategoryType categoryType = getCategoryType(categoryId);
            String categoryDisplayName = getCategoryDisplayName(categoryId);

            log.info("========================================");
            log.info("Category ID: {}", categoryId);
            log.info("Category Type: {}", categoryType);
            log.info("Category Display Name: {}", categoryDisplayName);
            log.info("========================================");

            // Check pension eligibility
            if (categoryType == CategoryType.PENSION_INELIGIBLE) {
                log.info("Member is not pension eligible (Category: {})", categoryDisplayName);
                return null;
            }

            LocalDate startDate = getStartDate(memberDetail);
            if (startDate == null) {
                return null;
            }

            LocalDate currentDate = LocalDate.now();

            CutoffServiceMaster config = getActiveCutoffConfig(categoryId);
            if (config == null) {
                log.warn("No cutoff configuration found for category: {}", categoryId);
                return null;
            }

            int cutoffYears = config.getNumberOfYears();
            int cutoffMonths = cutoffYears * 12;
            log.info("Cutoff Years: {}, Cutoff Months: {}", cutoffYears, cutoffMonths);

            String cid = memberDetail.getIdentityNumber();
            String nppfNumber = memberDetail.getNppfNumber();

            List<ContributionBifurcationDetail> allContributions = contributionDetailRepo
                    .findByCidAndNppfNumberAndPostingStatusOrderByCreatedAtAsc(cid, nppfNumber, "POSTED");

            if (allContributions.isEmpty()) {
                log.info("No contributions found for member");
                return null;
            }

            // ========== GET HEADER INFO FOR ALL CONTRIBUTIONS ==========
            Map<Long, ContributionHeaderInfo> headerInfoMap = getHeaderInfoForContributions(allContributions);
            log.info("Found header info for {} contributions", headerInfoMap.size());

            int allContributionInInt = allContributions.size();
            if (allContributionInInt < cutoffMonths) {
                log.info("Total contributions ({}) less than cutoff months ({}), no excess service",
                        allContributionInInt, cutoffMonths);
                return null;
            }

            // ========== CALCULATE EOL PERIODS ==========
            List<EOLPeriodDTO> eolPeriods = calculateEOLPeriodsFromHistory(allContributions, headerInfoMap, startDate, currentDate);
            log.info("Total EOL periods found: {}", eolPeriods.size());

            if (!eolPeriods.isEmpty()) {
                log.info("EOL Period Details:");
                eolPeriods.forEach(period -> log.info("  • {} : {} months (Rule: {})",
                        period.getPeriod(), period.getEolMonths(), period.getRuleType()));
            } else {
                log.info("No EOL periods found");
            }

            int totalEOLMonths = eolPeriods.stream()
                    .mapToInt(EOLPeriodDTO::getEolMonths)
                    .sum();
            log.info("Total EOL Months (all periods): {}", totalEOLMonths);

            int cutoffYear = currentDate.getYear();
            LocalDate cutoffYearStart = LocalDate.of(cutoffYear, 1, 1);
            LocalDate cutoffYearEnd = LocalDate.of(cutoffYear, 12, 31);

            log.info("Cutoff Year: {}, Cutoff Year Start: {}, Cutoff Year End: {}",
                    cutoffYear, cutoffYearStart, cutoffYearEnd);

            int eolMonthsBeforeCutoffYear = getEOLMonthsBeforeDate(eolPeriods, cutoffYearStart);
            int eolMonthsDuringCutoffYear = getEOLMonthsInDateRange(eolPeriods, cutoffYearStart, cutoffYearEnd);
            int eolMonthsAfterCutoffYear = getEOLMonthsAfterDate(eolPeriods, cutoffYearEnd);

            int additionalMonthsForEOL = eolMonthsBeforeCutoffYear;
            int totalMonthsNeeded = (cutoffYears * 12) + additionalMonthsForEOL;

            LocalDate cutoffServiceDate = startDate.plusMonths(totalMonthsNeeded);

            if (currentDate.isBefore(cutoffServiceDate)) {
                log.info("Current date {} is before cutoff service date {}, no excess service",
                        currentDate, cutoffServiceDate);
                return null;
            }

            LocalDate excessStart = cutoffServiceDate.plusMonths(1);
            LocalDate excessEnd = currentDate;

            log.info("Excess period: {} to {}", excessStart, excessEnd);

            // ================================================================
            // SECURITY FORCES SPECIAL HANDLING
            // ================================================================
            boolean isSecurityForcesCategory = isSecurityForces(categoryId);
            
            // Check if excess starts on or after July 1, 2024
            boolean isExcessAfterCutoff = !excessStart.isBefore(SECURITY_FORCES_CUTOFF_DATE);
            
            // Security Forces should follow Civil rules ONLY IF excess starts BEFORE July 1, 2024
            boolean followCivilRules = !isSecurityForcesCategory || !isExcessAfterCutoff;
            
            // Pension should be retained ONLY IF Security Forces AND excess starts ON or AFTER July 1, 2024
            boolean pensionRetained = isSecurityForcesCategory && isExcessAfterCutoff;

            String pensionRetainedReason;
            if (pensionRetained) {
                pensionRetainedReason = "Security Forces: Excess started ON or AFTER July 1, 2024. " +
                        "Pension components MUST BE RETAINED (cannot be taken).";
            } else if (isSecurityForcesCategory) {
                pensionRetainedReason = "Security Forces: Excess started BEFORE July 1, 2024. " +
                        "Following Civil rules - Pension components CAN be taken.";
            } else {
                pensionRetainedReason = "Civil category - Standard rules apply. Pension components CAN be taken.";
            }

            log.info("========================================");
            log.info("=== SECURITY FORCES RULE CHECK ===");
            log.info("Category: {} ({})", categoryDisplayName, categoryId);
            log.info("Is Security Forces (Category 03): {}", isSecurityForcesCategory);
            log.info("Excess Start Date: {}", excessStart);
            log.info("Cutoff Date (July 1, 2024): {}", SECURITY_FORCES_CUTOFF_DATE);
            log.info("Is Excess After Cutoff: {}", isExcessAfterCutoff);
            log.info("Follow Civil Rules: {}", followCivilRules);
            log.info("Pension Retained: {}", pensionRetained);
            log.info("Reason: {}", pensionRetainedReason);
            log.info("========================================");

            // ========== FILTER EXCESS CONTRIBUTIONS ==========
            List<ContributionBifurcationDetail> excessContributions = allContributions.stream()
                    .filter(c -> {
                        ContributionHeaderInfo headerInfo = headerInfoMap.get(c.getBifId());
                        if (headerInfo != null) {
                            LocalDate date = LocalDate.of(headerInfo.getYear(), headerInfo.getMonth(), 1);
                            return !date.isBefore(excessStart) && !date.isAfter(excessEnd);
                        }
                        LocalDate date = c.getCreatedAt().toLocalDate();
                        return !date.isBefore(excessStart) && !date.isAfter(excessEnd);
                    })
                    .sorted(Comparator.comparing(ContributionBifurcationDetail::getCreatedAt))
                    .collect(Collectors.toList());

            log.info("Found {} contributions in excess period", excessContributions.size());

            // Create map using header info or fallback to created_at
            Map<String, ContributionBifurcationDetail> contributionMap = excessContributions.stream()
                    .collect(Collectors.toMap(
                            c -> {
                                ContributionHeaderInfo headerInfo = headerInfoMap.get(c.getBifId());
                                if (headerInfo != null) {
                                    return headerInfo.getYear() + "-" + String.format("%02d", headerInfo.getMonth());
                                }
                                return getMonthYearKey(c.getCreatedAt().toLocalDate());
                            },
                            c -> c,
                            (existing, replacement) -> existing));

            // ========== GET ACCOUNTING YEARS IN EXCESS PERIOD ==========
            List<String> accountingYears = getAccountingYearsInPeriod(excessStart, excessEnd);
            log.info("Accounting Years in excess period: {}", accountingYears);

            // ========== INITIALIZE OPENING BALANCES ==========
            BigDecimal openingPmcPrincipal = BigDecimal.ZERO;
            BigDecimal openingPecPrincipal = BigDecimal.ZERO;
            BigDecimal openingPmcInterest = BigDecimal.ZERO;
            BigDecimal openingPecInterest = BigDecimal.ZERO;

            List<ExcessYearDetailDto> yearDetails = new ArrayList<>();
            List<ExcessMonthlyDetailDto> allMonthlyDetails = new ArrayList<>();

            BigDecimal grandTotalContributions = BigDecimal.ZERO;
            BigDecimal grandTotalInterest = BigDecimal.ZERO;
            int totalEOLMonthsInExcess = 0;

            // ========== PROCESS EACH YEAR ==========
            for (String year : accountingYears) {
                YearType yearType = getYearType(year);

                log.info("Processing Year: {}, Type: {}", year, yearType);

                ArrConfiguration arrConfig = getArrConfigurationWithFallback(year);

                if (arrConfig == null) {
                    log.warn("ARR configuration not found for year: {}, skipping", year);
                    continue;
                }

                BigDecimal rate = arrConfig.getArrRate() != null ? arrConfig.getArrRate() : BigDecimal.ZERO;
                int yearBasis = arrConfig.getYearBasis();

                log.info("Year: {}, Rate: {}%, Year Basis: {} days",
                        year, rate.multiply(HUNDRED), yearBasis);

                LocalDate yearStartDate = getYearStartDate(year, yearType);
                LocalDate yearEndDate = getYearEndDate(year, yearType);

                log.info("Year Start: {}, Year End: {}", yearStartDate, yearEndDate);

                int daysForOpening;
                if (yearDetails.isEmpty()) {
                    LocalDate effectiveStart = excessStart.isAfter(yearStartDate) ? excessStart : yearStartDate;
                    daysForOpening = (int) ChronoUnit.DAYS.between(effectiveStart, yearEndDate) + 1;
                    log.info("First year - days for opening balance interest: {} (from {} to {})",
                            daysForOpening, effectiveStart, yearEndDate);
                } else {
                    daysForOpening = calculateDaysForIOB(year, yearType);
                    log.info("Subsequent year - days for opening balance interest: {} (full year)", daysForOpening);
                }

                if (daysForOpening < 0) {
                    daysForOpening = 0;
                }

                List<YearMonth> monthsInYear = getMonthsInYear(year, yearType);

                List<YearMonth> filteredMonths = monthsInYear.stream()
                        .filter(ym -> {
                            LocalDate monthStart = ym.atDay(1);
                            LocalDate monthEnd = ym.atEndOfMonth();
                            return !monthEnd.isBefore(excessStart) && !monthStart.isAfter(excessEnd);
                        })
                        .collect(Collectors.toList());

                log.info("Months in excess period for year {}: {}", year, filteredMonths.size());

                List<ExcessMonthlyDetailDto> monthlyDetails = new ArrayList<>();

                BigDecimal yearlyPmc = BigDecimal.ZERO;
                BigDecimal yearlyPec = BigDecimal.ZERO;
                BigDecimal yearlyPimc = BigDecimal.ZERO;
                BigDecimal yearlyPiec = BigDecimal.ZERO;

                int eolMonthsInYear = 0;

                for (YearMonth yearMonth : filteredMonths) {
                    String monthKey = yearMonth.getYear() + "-" + String.format("%02d", yearMonth.getMonthValue());

                    ContributionBifurcationDetail detail = contributionMap.get(monthKey);

                    BigDecimal pmc = BigDecimal.ZERO;
                    BigDecimal pec = BigDecimal.ZERO;
                    BigDecimal pimc = BigDecimal.ZERO;
                    BigDecimal piec = BigDecimal.ZERO;
                    int days = 0;
                    LocalDate invoiceDate = null;

                    log.debug("Processing Month: {}, MonthKey: {}, Has Contribution: {}",
                            yearMonth, monthKey, detail != null);

                    if (detail != null) {
                        pmc = n(detail.getPensionMc());
                        pec = n(detail.getPensionEc());

                        // Get invoice date from header or fallback
                        ContributionHeaderInfo headerInfo = headerInfoMap.get(detail.getBifId());
                        if (headerInfo != null) {
                            invoiceDate = LocalDate.of(headerInfo.getYear(), headerInfo.getMonth(), 1);
                        } else {
                            invoiceDate = detail.getCreatedAt().toLocalDate();
                        }

                        days = (int) ChronoUnit.DAYS.between(invoiceDate, yearEndDate);

                        if (days < 0) {
                            days = 0;
                        }

                        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
                        BigDecimal factor = BigDecimal.valueOf(days)
                                .divide(BigDecimal.valueOf(yearBasis), 10, RM);

                        pimc = pmc.multiply(rateFactor).multiply(factor).setScale(2, RM);
                        piec = pec.multiply(rateFactor).multiply(factor).setScale(2, RM);

                        yearlyPmc = yearlyPmc.add(pmc);
                        yearlyPec = yearlyPec.add(pec);
                        yearlyPimc = yearlyPimc.add(pimc);
                        yearlyPiec = yearlyPiec.add(piec);

                    } else {
                        eolMonthsInYear++;
                    }

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
                // CALCULATE YEAR SUMMARY
                // ================================================================

                BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
                BigDecimal daysFactor = BigDecimal.valueOf(daysForOpening)
                        .divide(BigDecimal.valueOf(yearBasis), 10, RM);

                BigDecimal interestOnPmcOpening = openingPmcPrincipal
                        .multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
                BigDecimal interestOnPecOpening = openingPecPrincipal
                        .multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
                BigDecimal interestOnPimcOpening = openingPmcInterest
                        .multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
                BigDecimal interestOnPiecOpening = openingPecInterest
                        .multiply(rateFactor).multiply(daysFactor).setScale(2, RM);

                BigDecimal duringTheYearPmc = yearlyPmc;
                BigDecimal duringTheYearPec = yearlyPec;
                BigDecimal duringTheYearPimc = yearlyPimc;
                BigDecimal duringTheYearPiec = yearlyPiec;

                BigDecimal closingPmcBalance = openingPmcPrincipal
                        .add(interestOnPmcOpening).add(duringTheYearPmc);
                BigDecimal closingPecBalance = openingPecPrincipal
                        .add(interestOnPecOpening).add(duringTheYearPec);
                BigDecimal closingPimcBalance = openingPmcInterest
                        .add(interestOnPimcOpening).add(duringTheYearPimc);
                BigDecimal closingPiecBalance = openingPecInterest
                        .add(interestOnPiecOpening).add(duringTheYearPiec);

                BigDecimal yearlyContributions = yearlyPmc.add(yearlyPec);
                BigDecimal yearlyInterest = yearlyPimc.add(yearlyPiec);

                log.info("========== Year {} Summary ==========", year);
                log.info("Opening Balances:");
                log.info("  PMC: {}, PEC: {}, PIMC: {}, PIEC: {}",
                        openingPmcPrincipal, openingPecPrincipal, openingPmcInterest, openingPecInterest);
                log.info("Closing Balances:");
                log.info("  PMC: {}, PEC: {}, PIMC: {}, PIEC: {}",
                        closingPmcBalance, closingPecBalance, closingPimcBalance, closingPiecBalance);

                // ================================================================
                // BUILD YEAR DETAIL DTO
                // ================================================================

                ExcessYearDetailDto yearDetail = ExcessYearDetailDto.builder()
                        .accountingYear(year)
                        .yearType(yearType.name())
                        .openingPmcBalance(openingPmcPrincipal)
                        .openingPecBalance(openingPecPrincipal)
                        .openingPimcBalance(openingPmcInterest)
                        .openingPiecBalance(openingPecInterest)
                        .interestOnPmcOpening(interestOnPmcOpening)
                        .interestOnPecOpening(interestOnPecOpening)
                        .interestOnPimcOpening(interestOnPimcOpening)
                        .interestOnPiecOpening(interestOnPiecOpening)
                        .duringTheYearPmc(duringTheYearPmc)
                        .duringTheYearPec(duringTheYearPec)
                        .duringTheYearPimc(duringTheYearPimc)
                        .duringTheYearPiec(duringTheYearPiec)
                        .closingPmcBalance(closingPmcBalance)
                        .closingPecBalance(closingPecBalance)
                        .closingPimcBalance(closingPimcBalance)
                        .closingPiecBalance(closingPiecBalance)
                        .interestPmcRate(rate)
                        .interestPecRate(rate)
                        .interestPimcRate(rate)
                        .interestPiecRate(rate)
                        .interestDate(yearStartDate)
                        .yearBasis(yearBasis)
                        .daysInYear(daysForOpening)
                        .eolMonthsInYear(eolMonthsInYear)
                        .yearlyContributions(yearlyContributions)
                        .yearlyInterest(yearlyInterest)
                        .monthlyDetails(monthlyDetails)
                        .build();

                yearDetails.add(yearDetail);

                openingPmcPrincipal = closingPmcBalance;
                openingPecPrincipal = closingPecBalance;
                openingPmcInterest = closingPimcBalance;
                openingPecInterest = closingPiecBalance;

                grandTotalContributions = grandTotalContributions.add(yearlyContributions);
                grandTotalInterest = grandTotalInterest.add(yearlyInterest);
                totalEOLMonthsInExcess += eolMonthsInYear;
            }

            // ========== FINAL TOTAL ==========
            BigDecimal totalExcessAmount = grandTotalContributions.add(grandTotalInterest);
            totalExcessAmount = totalExcessAmount.setScale(2, RM);

            ExcessYearDetailDto lastYear = yearDetails.isEmpty() ? null : yearDetails.get(yearDetails.size() - 1);

            log.info("========== FINAL EXCESS SERVICE RESULT ==========");
            log.info("Total Excess Amount: {}", totalExcessAmount);
            log.info("Total Years Processed: {}", yearDetails.size());
            log.info("Pension Retained: {}", pensionRetained);
            log.info("Reason: {}", pensionRetainedReason);

            // ================================================================
            // BUILD CATEGORY INFO
            // ================================================================
            CategoryInfoDto categoryInfo = CategoryInfoDto.builder()
                    .categoryId(categoryId)
                    .categoryType(categoryType.name())
                    .displayName(categoryDisplayName)
                    .isSecurityForces(isSecurityForcesCategory)
                    .followCivilRules(followCivilRules)
                    .pensionRetained(pensionRetained)
                    .pensionRetainedReason(pensionRetainedReason)
                    .cutoffDate(SECURITY_FORCES_CUTOFF_DATE)
                    .build();

            // ========== BUILD RESPONSE ==========
            ExcessServiceResultDto result = ExcessServiceResultDto.builder()
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
                    .eolPeriods(eolPeriods)
                    .calculationDate(LocalDate.now())
                    .calculationMethod("STANDARD_WITH_EOL_ADJUSTMENT")
                    .status("CALCULATED")
                    .message("Excess service calculated successfully as of " + excessEnd)
                    .categoryInfo(categoryInfo)
                    .build();

            return result;

        } catch (Exception e) {
            log.error("Error calculating excess service: {}", e.getMessage(), e);
            throw ClaimException.internalError("Server error occurred: " + e.getMessage());
        }
    }

    // ========== GET HEADER INFO ==========
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

    private CutoffServiceMaster getActiveCutoffConfig(String categoryId) {
        return cutoffServiceMasterRepository.findAll().stream()
                .filter(config -> "Y".equals(config.getStatus()))
                .filter(config -> categoryId != null && categoryId.equals(config.getCategory().getCategoryId()))
                .findFirst()
                .orElse(null);
    }

    public List<EOLPeriodDTO> calculateEOLPeriodsFromHistory(
            List<ContributionBifurcationDetail> allContributions,
            Map<Long, ContributionHeaderInfo> headerInfoMap,
            LocalDate startDate,
            LocalDate endDate) {

        List<EOLPeriodDTO> eolPeriods = new ArrayList<>();

        if (allContributions.isEmpty() || startDate == null || endDate == null) {
            return eolPeriods;
        }

        // ✅ Use header info for contribution months
        Set<String> contributionMonths = allContributions.stream()
                .filter(c -> c.getCreatedAt() != null)
                .map(c -> {
                    ContributionHeaderInfo headerInfo = headerInfoMap.get(c.getBifId());
                    if (headerInfo != null) {
                        return headerInfo.getYear() + "-" + String.format("%02d", headerInfo.getMonth());
                    }
                    LocalDate date = c.getCreatedAt().toLocalDate();
                    return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                })
                .collect(Collectors.toSet());

        // Get transaction year from header or fallback
        int transactionYear = allContributions.stream()
                .filter(c -> c.getCreatedAt() != null)
                .map(c -> {
                    ContributionHeaderInfo headerInfo = headerInfoMap.get(c.getBifId());
                    if (headerInfo != null) {
                        return headerInfo.getYear();
                    }
                    return c.getCreatedAt().toLocalDate().getYear();
                })
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

    private ArrConfiguration getArrConfigurationWithFallback(String accountingYear) {
        if (accountingYear == null || accountingYear.isBlank()) {
            return null;
        }

        log.debug("Looking for ARR configuration for year: {}", accountingYear);

        try {
            Optional<ArrConfiguration> arrOpt = arrRepo.findByAccountingYear(accountingYear);
            if (arrOpt.isPresent()) {
                log.debug("✅ Found exact ARR configuration for year: {}", accountingYear);
                return arrOpt.get();
            }

            log.warn("No exact ARR configuration found for year: {}, trying fallback options", accountingYear);

            if (accountingYear.contains("-")) {
                String[] parts = accountingYear.split("-");
                String yearOnly = parts[0];

                Optional<ArrConfiguration> yearOnlyOpt = arrRepo.findByAccountingYear(yearOnly);
                if (yearOnlyOpt.isPresent()) {
                    log.info("✅ Found ARR configuration using year only: {}", yearOnly);
                    return yearOnlyOpt.get();
                }
            }

            if (!accountingYear.contains("-")) {
                String yearRange = accountingYear + "-" + accountingYear;
                Optional<ArrConfiguration> yearRangeOpt = arrRepo.findByAccountingYear(yearRange);
                if (yearRangeOpt.isPresent()) {
                    log.info("✅ Found ARR configuration using year range: {}", yearRange);
                    return yearRangeOpt.get();
                }
            }

            try {
                int year = Integer.parseInt(accountingYear.split("-")[0]);

                for (int i = 1; i <= 5; i++) {
                    int previousYear = year - i;
                    String previousYearStr = String.valueOf(previousYear);
                    String previousYearRange = previousYearStr + "-" + previousYearStr;

                    Optional<ArrConfiguration> prevOpt = arrRepo.findByAccountingYear(previousYearStr);
                    if (prevOpt.isPresent()) {
                        log.info("✅ Using ARR config from previous year: {} ({} years back)",
                                previousYearStr, i);
                        return prevOpt.get();
                    }

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

            List<ArrConfiguration> allArr = arrRepo.findAll();
            if (!allArr.isEmpty()) {
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

            log.warn("❌ No ARR configuration found in database, using default values");
            return null;

        } catch (Exception e) {
            log.error("Error getting ARR config for {}: {}", accountingYear, e.getMessage());
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
}