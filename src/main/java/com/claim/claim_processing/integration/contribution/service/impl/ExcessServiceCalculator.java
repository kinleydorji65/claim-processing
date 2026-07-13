package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.others.AccountingInterestMaster;
import com.claim.claim_processing.common.entities.others.CutoffServiceMaster;
import com.claim.claim_processing.common.repository.others.AccountingInterestMasterRepository;
import com.claim.claim_processing.common.repository.others.CutoffServiceMasterRepository;
import com.claim.claim_processing.integration.contribution.entity.ArrConfiguration;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;
import com.claim.claim_processing.integration.contribution.repository.ArrConfigurationRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationDetailRepository;
import com.claim.claim_processing.integration.contribution.dto.ExcessServiceResultDto;
import com.claim.claim_processing.integration.contribution.dto.ExcessYearDetailDto;
import com.claim.claim_processing.integration.contribution.dto.ExcessMonthlyDetailDto;
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

/**
 * Excess Service Calculator
 * 
 * Uses the EXACT SAME interest calculation logic as InterestCalculationServiceImpl:
 * 
 * Formula (matches InterestCalculationServiceImpl):
 *   factor = daysHeld / yearBasis
 *   interest = contributionAmount × (rate/100) × factor
 *   intOnOpening = openingBalance × (rate/100)
 *   closingBalance = openingBalance + intOnOpening + contributions + interest
 * 
 * All configuration values should come from database:
 *   - yearBasis from ArrConfiguration
 *   - interest rates from AccountingInterestMaster
 *   - cutoff years from CutoffServiceMaster
 *   - transition year from AccountingYearConfig
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExcessServiceCalculator {

    private final ContributionBifurcationDetailRepository contributionDetailRepo;
    private final AccountingInterestMasterRepository interestMasterRepository;
    private final CutoffServiceMasterRepository cutoffServiceMasterRepository;
    private final ArrConfigurationRepository arrRepo;

    // Constants that are always the same
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    
    // TODO: This should come from database (AccountingYearConfig)
    // For now, keep as configurable constant
    private static final int TRANSITION_YEAR = 2022;

    public enum YearType {
        ACCOUNTING_YEAR,
        TRANSITION_YEAR,
        CALENDAR_YEAR
    }

    public ExcessServiceResultDto calculateExcessService(
            MemberDetailResponseDto memberDetail,
            LocalDate relieveDate) {

        log.info("=== START Excess Service Calculation ===");

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

            // ========== 4. GET RELIEVE DATE ==========
            if (relieveDate == null) {
                relieveDate = LocalDate.now();
            }

            // ========== 5. GET CUTOFF CONFIGURATION ==========
            CutoffServiceMaster config = getActiveCutoffConfig();
            if (config == null) {
                return buildErrorResult("NO_CUTOFF_CONFIG", "Cutoff service configuration not found");
            }

            int cutoffYears = config.getNumberOfYears();

            // ========== 6. GET ALL CONTRIBUTIONS ==========
            List<ContributionBifurcationDetail> allContributions = 
                contributionDetailRepo.findByCidAndNppfNumberOrderByCreatedAtAsc(memberDetail.getIdentityNumber(), memberDetail.getNppfNumber());

            if (allContributions.isEmpty()) {
                return buildErrorResult("NO_CONTRIBUTIONS", "No contributions found for member");
            }

            // ========== 7. CALCULATE EOL MONTHS ==========
            int totalEOLMonths = calculateEOLMonthsFromHistory(allContributions, startDate, relieveDate);
            log.info("Total EOL months: {}", totalEOLMonths);

            // ========== 8. CALCULATE CUTOFF DATE ==========
            int totalMonths = (cutoffYears * 12) + totalEOLMonths;
            LocalDate cutoffServiceDate = startDate.plusMonths(totalMonths);

            // ========== 9. CHECK IF EXCESS SERVICE EXISTS ==========
            if (relieveDate.isBefore(cutoffServiceDate)) {
                return ExcessServiceResultDto.builder()
                    .isEligible(false)
                    .cutoffServiceDate(cutoffServiceDate)
                    .cutoffYears(cutoffYears)
                    .totalEOLMonths(totalEOLMonths)
                    .status("NOT_ELIGIBLE")
                    .message("Member has not crossed the cutoff service date")
                    .build();
            }

            // ========== 10. EXCESS PERIOD ==========
            LocalDate excessStart = cutoffServiceDate.plusMonths(1);
            LocalDate excessEnd = relieveDate;

            log.info("Excess period: {} to {}", excessStart, excessEnd);

            // ========== 11. FILTER EXCESS CONTRIBUTIONS ==========
            List<ContributionBifurcationDetail> excessContributions = allContributions.stream()
                .filter(c -> {
                    LocalDate date = c.getCreatedAt().toLocalDate();
                    return !date.isBefore(excessStart) && !date.isAfter(excessEnd);
                })
                .sorted(Comparator.comparing(ContributionBifurcationDetail::getCreatedAt))
                .collect(Collectors.toList());

            log.info("Found {} contributions in excess period", excessContributions.size());

            // Create map for quick lookup by month-year
            Map<String, ContributionBifurcationDetail> contributionMap = excessContributions.stream()
                .collect(Collectors.toMap(
                    c -> getMonthYearKey(c.getCreatedAt().toLocalDate()),
                    c -> c,
                    (existing, replacement) -> existing
                ));

            // Group by accounting year
            Map<String, List<ContributionBifurcationDetail>> contributionsByYear = excessContributions.stream()
                .collect(Collectors.groupingBy(c -> getAccountingYearForDate(c.getCreatedAt().toLocalDate())));

            // ========== 12. CALCULATE YEAR BY YEAR ==========
            BigDecimal previousClosing = BigDecimal.ZERO;
            List<ExcessYearDetailDto> yearDetails = new ArrayList<>();
            List<ExcessMonthlyDetailDto> allMonthlyDetails = new ArrayList<>();
            List<String> accountingYears = getAccountingYearsInPeriod(excessStart, excessEnd);

            int totalEOLMonthsInExcess = 0;
            BigDecimal grandTotalContributions = BigDecimal.ZERO;
            BigDecimal grandTotalInterest = BigDecimal.ZERO;

            for (String year : accountingYears) {
                YearType yearType = getYearType(year);
                List<ContributionBifurcationDetail> yearContributions = 
                    contributionsByYear.getOrDefault(year, new ArrayList<>());

                AccountingInterestMaster interestMaster = getInterestRateDetail(year);
                if (interestMaster == null) {
                    log.warn("Interest rate not found for year: {}, skipping", year);
                    continue;
                }

                BigDecimal rate = interestMaster.getInterestRate();
                LocalDate interestDate = interestMaster.getInterestDate();
                
                // Get year basis from database (should come from ArrConfiguration)
                // For now, using 365 as default
                int yearBasis = getYearBasisForYear(year);

                // Process months
                YearMonthResult yearResult = processYearMonths(
                    year, yearType, interestMaster, contributionMap,
                    yearContributions, previousClosing, excessStart, excessEnd, yearBasis
                );

                totalEOLMonthsInExcess += yearResult.eolMonthsInYear;
                grandTotalContributions = grandTotalContributions.add(yearResult.yearlyContributions);
                grandTotalInterest = grandTotalInterest.add(yearResult.yearlyInterest);

                ExcessYearDetailDto yearDetail = ExcessYearDetailDto.builder()
                    .accountingYear(year)
                    .yearType(yearType.name())
                    .openingBalance(previousClosing)
                    .interestOnOpening(yearResult.iob)
                    .duringTheYear(yearResult.duringTheYear)
                    .closingBalance(yearResult.closingBalance)
                    .interestRate(rate)
                    .interestDate(interestDate)
                    .daysInYear(yearResult.daysForIOB)
                    .eolMonthsInYear(yearResult.eolMonthsInYear)
                    .yearlyContributions(yearResult.yearlyContributions)
                    .yearlyInterest(yearResult.yearlyInterest)
                    .monthlyDetails(yearResult.monthlyDetails)
                    .build();
                yearDetails.add(yearDetail);
                allMonthlyDetails.addAll(yearResult.monthlyDetails);

                previousClosing = yearResult.closingBalance;
            }

            BigDecimal totalExcessAmount = previousClosing.setScale(2, RM);

            return ExcessServiceResultDto.builder()
                .isEligible(true)
                .totalExcessAmount(totalExcessAmount)
                .cutoffServiceDate(cutoffServiceDate)
                .cutoffYears(cutoffYears)
                .excessStartDate(excessStart)
                .excessEndDate(excessEnd)
                .totalEOLMonths(totalEOLMonths)
                .eolMonthsInExcess(totalEOLMonthsInExcess)
                .totalContributionsInExcess(grandTotalContributions)
                .totalInterestInExcess(grandTotalInterest)
                .yearDetails(yearDetails)
                .monthlyDetails(allMonthlyDetails)
                .status("CALCULATED")
                .message("Excess service calculated successfully")
                .build();

        } catch (Exception e) {
            log.error("Error calculating excess service: {}", e.getMessage(), e);
            return buildErrorResult("ERROR", "Error calculating excess service: " + e.getMessage());
        }
    }

    

    // ========== HELPER METHODS ==========

    private YearMonthResult processYearMonths(
            String year,
            YearType yearType,
            AccountingInterestMaster interestMaster,
            Map<String, ContributionBifurcationDetail> contributionMap,
            List<ContributionBifurcationDetail> yearContributions,
            BigDecimal openingBalance,
            LocalDate excessStart,
            LocalDate excessEnd,
            int yearBasis) {

        BigDecimal rate = interestMaster.getInterestRate();
        LocalDate interestDate = interestMaster.getInterestDate();

        List<YearMonth> monthsInYear = getMonthsInYear(year, yearType);
        List<YearMonth> filteredMonths = monthsInYear.stream()
            .filter(ym -> {
                LocalDate monthStart = ym.atDay(1);
                LocalDate monthEnd = ym.atEndOfMonth();
                return !monthEnd.isBefore(excessStart) && !monthStart.isAfter(excessEnd);
            })
            .collect(Collectors.toList());

        yearContributions.sort(Comparator.comparing(ContributionBifurcationDetail::getCreatedAt));

        BigDecimal totalMonthlyCPlusI = BigDecimal.ZERO;
        BigDecimal yearlyContributions = BigDecimal.ZERO;
        BigDecimal yearlyInterest = BigDecimal.ZERO;
        List<ExcessMonthlyDetailDto> monthlyDetails = new ArrayList<>();

        LocalDate previousInvoiceDate = null;
        int monthIndex = 0;
        int eolMonthsInYear = 0;

        for (YearMonth yearMonth : filteredMonths) {
            LocalDate monthStart = yearMonth.atDay(1);
            String monthKey = getMonthYearKey(monthStart);

            ContributionBifurcationDetail detail = contributionMap.get(monthKey);

            BigDecimal mpc = BigDecimal.ZERO;
            BigDecimal epc = BigDecimal.ZERO;
            BigDecimal totalPension = BigDecimal.ZERO;
            BigDecimal interest = BigDecimal.ZERO;
            BigDecimal cPlusI = BigDecimal.ZERO;
            int days = 0;
            boolean isEOL = false;

            if (detail != null) {
                // HAS CONTRIBUTION - Calculate on PENSION components only (matching Excel)
                mpc = detail.getPensionMc() != null ? detail.getPensionMc() : BigDecimal.ZERO;
                epc = detail.getPensionEc() != null ? detail.getPensionEc() : BigDecimal.ZERO;
                totalPension = mpc.add(epc);

                // Calculate days
                if (monthIndex == 0) {
                    days = getDaysFromYearStart(year, yearType, detail.getCreatedAt().toLocalDate());
                } else {
                    days = (int) ChronoUnit.DAYS.between(previousInvoiceDate, detail.getCreatedAt().toLocalDate());
                }

                // SAME FORMULA as InterestCalculationServiceImpl:
                // factor = daysHeld / yearBasis
                // interest = contribution × (rate/100) × factor
                BigDecimal factor = BigDecimal.valueOf(days)
                    .divide(BigDecimal.valueOf(yearBasis), 8, RM);

                interest = totalPension
                    .multiply(rate)
                    .divide(HUNDRED, 10, RM)
                    .multiply(factor)
                    .setScale(2, RM);

                cPlusI = totalPension.add(interest);

                yearlyContributions = yearlyContributions.add(totalPension);
                yearlyInterest = yearlyInterest.add(interest);
                previousInvoiceDate = detail.getCreatedAt().toLocalDate();

            } else {
                // EOL MONTH
                isEOL = true;
                eolMonthsInYear++;
            }

            ExcessMonthlyDetailDto monthlyDetail = ExcessMonthlyDetailDto.builder()
                .dueMonth(String.valueOf(yearMonth.getMonthValue()))
                .invoiceDate(monthStart)
                .mpc(mpc)
                .epc(epc)
                .totalPension(totalPension)
                .days(days)
                .interest(interest)
                .cPlusI(cPlusI)
                .isEOL(isEOL)
                .build();
            monthlyDetails.add(monthlyDetail);

            totalMonthlyCPlusI = totalMonthlyCPlusI.add(cPlusI);
            monthIndex++;
        }

        // IOB = Opening Balance × (rate/100) - SAME as InterestCalculationServiceImpl
        int daysForIOB = getDaysForIOB(year, yearType, interestDate);
        BigDecimal iob = openingBalance
            .multiply(rate)
            .divide(HUNDRED, 2, RM);

        BigDecimal duringTheYear = totalMonthlyCPlusI;
        BigDecimal closingBalance = openingBalance.add(iob).add(duringTheYear);

        return new YearMonthResult(
            iob, duringTheYear, closingBalance, daysForIOB,
            eolMonthsInYear, yearlyContributions, yearlyInterest, monthlyDetails
        );
    }

    /**
     * Get year basis from database (should come from ArrConfiguration)
     * For now, default to 365
     */
    private int getYearBasisForYear(String accountingYear) {
        ArrConfiguration arr = arrRepo.findByAccountingYear(accountingYear)
        .orElseThrow(() -> new RuntimeException("ARR not found for year: " + accountingYear));
    return arr.getYearBasis();
    }

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

    private AccountingInterestMaster getInterestRateDetail(String accountingYear) {
        return interestMasterRepository
            .findByFinancialYear(accountingYear)
            .orElse(null);
    }

    private int calculateEOLMonthsFromHistory(
            List<ContributionBifurcationDetail> allContributions,
            LocalDate startDate,
            LocalDate endDate) {

        if (allContributions.isEmpty() || startDate == null) return 0;

        Set<String> contributionMonths = allContributions.stream()
            .map(c -> getMonthYearKey(c.getCreatedAt().toLocalDate()))
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

    private YearType getYearType(String accountingYear) {
        try {
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
            return YearType.ACCOUNTING_YEAR;
        }
    }

    private List<YearMonth> getMonthsInYear(String accountingYear, YearType yearType) {
        List<YearMonth> months = new ArrayList<>();
        try {
            String[] parts = accountingYear.split("-");
            int startYear = Integer.parseInt(parts[0]);
            int endYear = Integer.parseInt(parts[1]);

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

    private int getDaysFromYearStart(String accountingYear, YearType yearType, LocalDate date) {
        try {
            String[] parts = accountingYear.split("-");
            int startYear = Integer.parseInt(parts[0]);

            LocalDate yearStart;
            switch (yearType) {
                case ACCOUNTING_YEAR:
                case TRANSITION_YEAR:
                    yearStart = LocalDate.of(startYear, 7, 1);
                    break;
                case CALENDAR_YEAR:
                    yearStart = LocalDate.of(startYear, 1, 1);
                    break;
                default:
                    yearStart = LocalDate.of(startYear, 7, 1);
            }
            return (int) ChronoUnit.DAYS.between(yearStart, date);
        } catch (Exception e) {
            return 335;
        }
    }

    private int getDaysForIOB(String accountingYear, YearType yearType, LocalDate interestDate) {
        try {
            String[] parts = accountingYear.split("-");
            int startYear = Integer.parseInt(parts[0]);

            LocalDate yearStart;
            switch (yearType) {
                case ACCOUNTING_YEAR:
                case TRANSITION_YEAR:
                    yearStart = LocalDate.of(startYear, 7, 1);
                    break;
                case CALENDAR_YEAR:
                    yearStart = LocalDate.of(startYear, 1, 1);
                    break;
                default:
                    yearStart = LocalDate.of(startYear, 7, 1);
            }
            return (int) ChronoUnit.DAYS.between(yearStart, interestDate);
        } catch (Exception e) {
            return 365;
        }
    }

    private String getAccountingYearForDate(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        if (year < TRANSITION_YEAR) {
            if (month <= 6) return (year - 1) + "-" + year;
            else return year + "-" + (year + 1);
        } else if (year == TRANSITION_YEAR) {
            if (month <= 6) return (year - 1) + "-" + year;
            else return year + "-" + year;
        } else {
            return year + "-" + year;
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

    private ExcessServiceResultDto buildErrorResult(String status, String message) {
        return ExcessServiceResultDto.builder()
            .isEligible(false)
            .status(status)
            .message(message)
            .build();
    }

    @lombok.AllArgsConstructor
    @lombok.Getter
    private static class YearMonthResult {
        private final BigDecimal iob;
        private final BigDecimal duringTheYear;
        private final BigDecimal closingBalance;
        private final int daysForIOB;
        private final int eolMonthsInYear;
        private final BigDecimal yearlyContributions;
        private final BigDecimal yearlyInterest;
        private final List<ExcessMonthlyDetailDto> monthlyDetails;
    }
}