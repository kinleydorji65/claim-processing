package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.others.AccountingInterestMaster;
import com.claim.claim_processing.common.entities.others.CutoffServiceMaster;
import com.claim.claim_processing.common.repository.others.AccountingInterestMasterRepository;
import com.claim.claim_processing.common.repository.others.CutoffServiceMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;
import com.claim.claim_processing.integration.contribution.entity.MemberBalanceSnapshot;
import com.claim.claim_processing.integration.contribution.entity.MemberInterestRecord;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationDetailRepository;
import com.claim.claim_processing.integration.contribution.repository.MemberBalanceSnapshotRepository;
import com.claim.claim_processing.integration.contribution.repository.MemberInterestRecordRepository;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.member.service.MemberService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
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
    private final MemberInterestRecordRepository mirRepo;
    private final ContributionBifurcationDetailRepository contributionDetailRepo;
    private final AccountingInterestMasterRepository interestMasterRepository;
    private final CutoffServiceMasterRepository cutoffServiceMasterRepository;

    // Only these are constant (always the same)
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    
    // TODO: This should come from database (AccountingYearConfig)
    private static final int TRANSITION_YEAR = 2022;

    @Override
    public MemberContributionSummary getContributionSummary(
        MemberDetailResponseDto memberDetail,
        LocalDate relieveDate) {
    
        log.info("=== START getContributionSummary ===");
        log.info("nppfNumber: {}, cid: {}", memberDetail.getNppfNumber(), memberDetail.getIdentityNumber());
        
        try {
            // ========== 1. GET PF BALANCE FROM SNAPSHOTS ==========
            List<MemberBalanceSnapshot> snapshots = getSnapshots(memberDetail.getIdentityNumber(), memberDetail.getNppfNumber());
            
            if (snapshots.isEmpty()) {
                throw ClaimException.notFound("No snapshots found for member: " + memberDetail.getNppfNumber());
            }

            // Aggregate PF totals from snapshots (already calculated by system)
            PFTotals pfTotals = aggregatePFTotals(snapshots);
            
            // Get interest records for component breakdown
            List<MemberInterestRecord> interestRecords = getInterestRecords(memberDetail.getIdentityNumber(), memberDetail.getNppfNumber());
            
            // Build component groups
            List<MemberContributionSummary.ComponentGroup> componentGroups = 
                    buildComponentGroups(snapshots, interestRecords);
            
            // Calculate contribution months
            int contributionMonths = calculateContributionMonths(snapshots);

            // ========== 2. BUILD SUMMARY WITH PF BALANCE ==========
            MemberContributionSummary.MemberContributionSummaryBuilder builder = 
                MemberContributionSummary.builder()
                    .nppfNumber(memberDetail.getIdentityNumber())
                    .schemeTypeId(getSchemeTypeId(snapshots))
                    .totalContributionMonths(contributionMonths)
                    .totalContributionYears(contributionMonths / 12)
                    .totalNonContributionMonths(0)
                    .contributionEndDate(pfTotals.getLatestDate())
                    .totalPrincipalAmount(pfTotals.getTotalPrincipal())
                    .totalInterestAmount(pfTotals.getTotalInterest())
                    .totalBalance(pfTotals.getTotalBalance())
                    .componentGroups(componentGroups);

            // ========== 3. CALCULATE EXCESS SERVICE (PENSION ONLY) ==========
            if (relieveDate != null && isPensionEligible(memberDetail)) {
                try {
                    LocalDate startDate = getStartDate(memberDetail);
                    
                    if (startDate == null) {
                        log.warn("Start date is null for member: {}", memberDetail.getNppfNumber());
                        builder.excessStatus("ERROR")
                               .excessMessage("Start date not available for member");
                    } else {
                        // Get cutoff config
                        CutoffServiceMaster config = getActiveCutoffConfig();
                        if (config != null) {
                            int cutoffYears = config.getNumberOfYears();
                            
                            // Get all contributions for EOL calculation
                            List<ContributionBifurcationDetail> allContributions = 
                                contributionDetailRepo.findByCidAndNppfNumberOrderByCreatedAtAsc(
                                    memberDetail.getIdentityNumber(), 
                                    memberDetail.getNppfNumber());
                            
                            // Calculate EOL months
                            int totalEOLMonths = calculateEOLMonthsFromHistory(allContributions, startDate, relieveDate);
                            
                            // Calculate cutoff date
                            int totalMonths = (cutoffYears * 12) + totalEOLMonths;
                            LocalDate cutoffServiceDate = startDate.plusMonths(totalMonths);
                            
                            // CHECK: Has member completed cutoff years?
                            if (relieveDate.isAfter(cutoffServiceDate)) {
                                // YES - Calculate excess service
                                ExcessCalculationResult excessResult = calculateExcessService(
                                    memberDetail.getIdentityNumber(),
                                    memberDetail.getNppfNumber(),
                                    relieveDate,
                                    cutoffServiceDate,
                                    cutoffYears,
                                    totalEOLMonths
                                );
                                
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
                                           .excessMessage("Excess service calculated successfully");
                                    
                                    // Set year details
                                    if (excessResult.getYearDetails() != null) {
                                        builder.excessYearDetails(excessResult.getYearDetails());
                                    }
                                    
                                    // Set monthly details
                                    if (excessResult.getMonthlyDetails() != null) {
                                        builder.excessMonthlyDetails(excessResult.getMonthlyDetails());
                                    }
                                    
                                    log.info("Excess service calculated: {}", excessResult.getTotalExcessAmount());
                                }
                            } else {
                                // NO - Member has not completed cutoff years
                                log.info("Member has NOT completed {} years of service. Cutoff date: {}, Relieve date: {}", 
                                    cutoffYears, cutoffServiceDate, relieveDate);
                                builder.excessStatus("NOT_ELIGIBLE")
                                       .excessMessage("Member has not completed " + cutoffYears + " years of service");
                            }
                        } else {
                            log.warn("No active cutoff config found");
                            builder.excessStatus("ERROR")
                                   .excessMessage("Cutoff configuration not found");
                        }
                    }
                } catch (Exception e) {
                    log.error("Error calculating excess service: {}", e.getMessage(), e);
                    builder.excessStatus("ERROR")
                           .excessMessage("Error calculating excess: " + e.getMessage());
                }
            }

            MemberContributionSummary summary = builder.build();
            
            log.info("=== END getContributionSummary ===");
            log.info("PF Balance: {}, Excess Amount: {}, Total Payable: {}", 
                pfTotals.getTotalBalance(), 
                summary.hasExcessService() ? summary.getExcessServiceAmount() : BigDecimal.ZERO,
                pfTotals.getTotalBalance().add(summary.hasExcessService() ? summary.getExcessServiceAmount() : BigDecimal.ZERO));
            return summary;

        } catch (Exception e) {
            log.error("Error fetching contribution summary: {}", e.getMessage(), e);
            throw ClaimException.internalError("Failed to fetch contribution summary: " + e.getMessage());
        }
    }

    // ========== EXCESS SERVICE CALCULATION (PENSION ONLY) ==========

    private ExcessCalculationResult calculateExcessService(
            String cid,
            String nppfNumber,
            LocalDate relieveDate,
            LocalDate cutoffServiceDate,
            int cutoffYears,
            int totalEOLMonths) {

        log.info("=== START Excess Service Calculation ===");
        log.info("CID: {}, NPPF: {}, Relieve Date: {}", cid, nppfNumber, relieveDate);
        
        List<ContributionBifurcationDetail> allContributions = 
            contributionDetailRepo.findByCidAndNppfNumberOrderByCreatedAtAsc(cid, nppfNumber);

        if (allContributions.isEmpty()) {
            log.warn("No contributions found for member");
            return buildEmptyExcessResult();
        }

        LocalDate excessStart = cutoffServiceDate.plusMonths(1);
        LocalDate excessEnd = relieveDate;

        log.info("Excess period: {} to {}", excessStart, excessEnd);

        List<ContributionBifurcationDetail> excessContributions = allContributions.stream()
            .filter(c -> {
                LocalDate date = c.getCreatedAt().toLocalDate();
                return !date.isBefore(excessStart) && !date.isAfter(excessEnd);
            })
            .sorted(Comparator.comparing(ContributionBifurcationDetail::getCreatedAt))
            .collect(Collectors.toList());

        log.info("Found {} contributions in excess period", excessContributions.size());

        return calculateExcessBalance(
            excessContributions,
            excessStart,
            excessEnd,
            cutoffServiceDate,
            cutoffYears,
            totalEOLMonths
        );
    }

    private ExcessCalculationResult calculateExcessBalance(
            List<ContributionBifurcationDetail> excessContributions,
            LocalDate excessStart,
            LocalDate excessEnd,
            LocalDate cutoffServiceDate,
            int cutoffYears,
            int totalEOLMonths) {

        Map<String, List<ContributionBifurcationDetail>> byYear = excessContributions.stream()
            .collect(Collectors.groupingBy(c -> getAccountingYearForDate(c.getCreatedAt().toLocalDate())));

        BigDecimal openingBalance = BigDecimal.ZERO;
        BigDecimal totalExcessContributions = BigDecimal.ZERO;
        BigDecimal totalExcessInterest = BigDecimal.ZERO;
        int totalEOLMonthsInExcess = 0;
        
        List<MemberContributionSummary.ExcessYearDetail> yearDetails = new ArrayList<>();
        List<MemberContributionSummary.ExcessMonthlyDetail> allMonthlyDetails = new ArrayList<>();

        List<String> accountingYears = getAccountingYearsInPeriod(excessStart, excessEnd);
        log.info("Accounting years in excess period: {}", accountingYears);

        for (String year : accountingYears) {
            log.info("--- Processing year: {} ---", year);

            List<ContributionBifurcationDetail> yearContribs = byYear.getOrDefault(year, new ArrayList<>());
            
            AccountingInterestMaster interestMaster = getInterestRateDetail(year);
            if (interestMaster == null) {
                log.warn("Interest rate not found for year: {}, skipping", year);
                continue;
            }

            BigDecimal arrRate = interestMaster.getInterestRate();
            LocalDate yearEndDate = interestMaster.getInterestDate();
            int yearBasis = getYearBasisForYear(year);

            log.info("Rate: {}%, Year End Date: {}, Year Basis: {}, Records: {}", 
                arrRate.multiply(HUNDRED), yearEndDate, yearBasis, yearContribs.size());

            String yearType = getYearType(year);
            List<YearMonth> monthsInYear = getMonthsInYear(year);
            List<YearMonth> filteredMonths = monthsInYear.stream()
                .filter(ym -> {
                    LocalDate monthStart = ym.atDay(1);
                    LocalDate monthEnd = ym.atEndOfMonth();
                    return !monthEnd.isBefore(excessStart) && !monthStart.isAfter(excessEnd);
                })
                .collect(Collectors.toList());

            yearContribs.sort(Comparator.comparing(ContributionBifurcationDetail::getCreatedAt));

            BigDecimal yearlyContributions = BigDecimal.ZERO;
            BigDecimal yearlyInterest = BigDecimal.ZERO;
            int eolMonthsInYear = 0;
            List<MemberContributionSummary.ExcessMonthlyDetail> monthlyDetails = new ArrayList<>();

            Map<String, ContributionBifurcationDetail> monthMap = yearContribs.stream()
                .collect(Collectors.toMap(
                    c -> getMonthYearKey(c.getCreatedAt().toLocalDate()),
                    c -> c,
                    (existing, replacement) -> existing
                ));

            for (YearMonth yearMonth : filteredMonths) {
                LocalDate monthStart = yearMonth.atDay(1);
                String monthKey = getMonthYearKey(monthStart);

                ContributionBifurcationDetail detail = monthMap.get(monthKey);

                BigDecimal mpc = BigDecimal.ZERO;
                BigDecimal epc = BigDecimal.ZERO;
                BigDecimal totalPension = BigDecimal.ZERO;
                BigDecimal interest = BigDecimal.ZERO;
                int daysHeld = 0;
                boolean isEOL = false;

                if (detail != null) {
                    mpc = detail.getPensionMc() != null ? detail.getPensionMc() : BigDecimal.ZERO;
                    epc = detail.getPensionEc() != null ? detail.getPensionEc() : BigDecimal.ZERO;
                    totalPension = mpc.add(epc);

                    LocalDate depositDate = detail.getCreatedAt().toLocalDate();
                    daysHeld = (int) ChronoUnit.DAYS.between(depositDate, yearEndDate);
                    if (daysHeld < 0) daysHeld = 0;

                    BigDecimal factor = BigDecimal.valueOf(daysHeld)
                        .divide(BigDecimal.valueOf(yearBasis), 8, RM);

                    interest = totalPension
                        .multiply(arrRate)
                        .divide(HUNDRED, 10, RM)
                        .multiply(factor)
                        .setScale(2, RM);

                    yearlyContributions = yearlyContributions.add(totalPension);
                    yearlyInterest = yearlyInterest.add(interest);

                } else {
                    isEOL = true;
                    eolMonthsInYear++;
                    totalEOLMonthsInExcess++;
                }

                MemberContributionSummary.ExcessMonthlyDetail monthlyDetail = 
                    MemberContributionSummary.ExcessMonthlyDetail.builder()
                        .dueMonth(String.valueOf(yearMonth.getMonthValue()))
                        .invoiceDate(monthStart)
                        .mpc(mpc)
                        .epc(epc)
                        .totalPension(totalPension)
                        .days(daysHeld)
                        .interest(interest)
                        .cPlusI(totalPension.add(interest))
                        .isEOL(isEOL)
                        .build();
                monthlyDetails.add(monthlyDetail);
                allMonthlyDetails.add(monthlyDetail);
            }

            BigDecimal intOnOpening = openingBalance
                .multiply(arrRate)
                .divide(HUNDRED, 2, RM);

            BigDecimal duringTheYear = yearlyContributions.add(yearlyInterest);
            BigDecimal closingBalance = openingBalance
                .add(intOnOpening)
                .add(duringTheYear);

            log.info("Year {} - Opening: {}, IOB: {}, Contributions: {}, Interest: {}, Closing: {}", 
                year,
                openingBalance.setScale(2, RM),
                intOnOpening.setScale(2, RM),
                yearlyContributions.setScale(2, RM),
                yearlyInterest.setScale(2, RM),
                closingBalance.setScale(2, RM));

            totalExcessContributions = totalExcessContributions.add(yearlyContributions);
            totalExcessInterest = totalExcessInterest.add(yearlyInterest).add(intOnOpening);

            MemberContributionSummary.ExcessYearDetail yearDetail = 
                MemberContributionSummary.ExcessYearDetail.builder()
                    .accountingYear(year)
                    .yearType(yearType)
                    .openingBalance(openingBalance)
                    .interestOnOpening(intOnOpening)
                    .duringTheYear(duringTheYear)
                    .closingBalance(closingBalance)
                    .interestRate(arrRate)
                    .interestDate(yearEndDate)
                    .daysInYear(0)
                    .eolMonthsInYear(eolMonthsInYear)
                    .yearlyContributions(yearlyContributions)
                    .yearlyInterest(yearlyInterest)
                    .monthlyDetails(monthlyDetails)
                    .build();
            yearDetails.add(yearDetail);

            openingBalance = closingBalance;
        }

        BigDecimal totalExcessAmount = openingBalance;

        log.info("========== EXCESS SERVICE SUMMARY ==========");
        log.info("Total Excess Contributions: {}", totalExcessContributions.setScale(2, RM));
        log.info("Total Excess Interest: {}", totalExcessInterest.setScale(2, RM));
        log.info("Total Excess Amount: {}", totalExcessAmount.setScale(2, RM));
        log.info("EOL Months in Excess: {}", totalEOLMonthsInExcess);
        log.info("===========================================");

        return ExcessCalculationResult.builder()
            .isEligible(true)
            .totalExcessAmount(totalExcessAmount)
            .totalExcessContributions(totalExcessContributions)
            .totalExcessInterest(totalExcessInterest)
            .eolMonthsInExcess(totalEOLMonthsInExcess)
            .cutoffServiceDate(cutoffServiceDate)
            .cutoffYears(cutoffYears)
            .excessStartDate(excessStart)
            .excessEndDate(excessEnd)
            .totalEOLMonths(totalEOLMonths)
            .yearDetails(yearDetails)
            .monthlyDetails(allMonthlyDetails)
            .build();
    }

    // ========== PRIVATE HELPER METHODS ==========

    private List<MemberBalanceSnapshot> getSnapshots(String cid, String nppfNumber) {
        if (cid == null || cid.isEmpty()) {
            return snapshotRepo.findByNppfNumberOrderByAccountingYearDesc(nppfNumber);
        } else {
            return snapshotRepo.findByCidAndNppfNumberOrderByAccountingYearDesc(cid, nppfNumber);
        }
    }

    private List<MemberInterestRecord> getInterestRecords(String cid, String nppfNumber) {
        if (cid == null || cid.isEmpty()) {
            return mirRepo.findByNppfNumberOrderByAccountingYearDesc(nppfNumber);
        } else {
            return mirRepo.findByCidAndNppfNumberOrderByAccountingYearDesc(cid, nppfNumber);
        }
    }

    private PFTotals aggregatePFTotals(List<MemberBalanceSnapshot> snapshots) {
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;
        LocalDate latestDate = null;

        for (MemberBalanceSnapshot snapshot : snapshots) {
            totalPrincipal = totalPrincipal.add(n(snapshot.getTotalContributions()));
            totalInterest = totalInterest.add(n(snapshot.getTotalInterest()));
            if (snapshot.getLastUpdatedAt() != null) {
                latestDate = snapshot.getLastUpdatedAt().toLocalDate();
            }
        }

        BigDecimal totalBalance = totalPrincipal.add(totalInterest);
        return new PFTotals(totalPrincipal, totalInterest, totalBalance, latestDate);
    }

    private List<MemberContributionSummary.ComponentGroup> buildComponentGroups(
            List<MemberBalanceSnapshot> snapshots,
            List<MemberInterestRecord> interestRecords) {
        
        Map<String, BigDecimal> interestByComponent = new HashMap<>();
        for (MemberInterestRecord mir : interestRecords) {
            if ("CREDITED".equals(mir.getStatus()) || "ADJUSTED".equals(mir.getStatus())) {
                String code = mir.getComponentCode();
                BigDecimal current = interestByComponent.getOrDefault(code, BigDecimal.ZERO);
                interestByComponent.put(code, current.add(n(mir.getTotalInterest())));
            }
        }

        Map<String, BigDecimal> contributionByComponent = new HashMap<>();
        for (MemberBalanceSnapshot snap : snapshots) {
            addToMap(contributionByComponent, "IEC", snap.getPfEc());
            addToMap(contributionByComponent, "IMC", snap.getPfMc());
            addToMap(contributionByComponent, "IPC", snap.getPensionEc());
            addToMap(contributionByComponent, "IGC", snap.getGc());
            addToMap(contributionByComponent, "IVC", snap.getVc());
        }

        List<MemberContributionSummary.ComponentGroup> groups = new ArrayList<>();
        groups.add(createComponentGroup("IEC", "Employee PF Contribution", 
                contributionByComponent.getOrDefault("IEC", BigDecimal.ZERO),
                interestByComponent.getOrDefault("IEC", BigDecimal.ZERO)));
        groups.add(createComponentGroup("IMC", "Employer PF Contribution", 
                contributionByComponent.getOrDefault("IMC", BigDecimal.ZERO),
                interestByComponent.getOrDefault("IMC", BigDecimal.ZERO)));
        groups.add(createComponentGroup("IPC", "Pension Contribution", 
                contributionByComponent.getOrDefault("IPC", BigDecimal.ZERO),
                interestByComponent.getOrDefault("IPC", BigDecimal.ZERO)));
        groups.add(createComponentGroup("IGC", "Government Contribution", 
                contributionByComponent.getOrDefault("IGC", BigDecimal.ZERO),
                interestByComponent.getOrDefault("IGC", BigDecimal.ZERO)));
        groups.add(createComponentGroup("IVC", "Voluntary Contribution", 
                contributionByComponent.getOrDefault("IVC", BigDecimal.ZERO),
                interestByComponent.getOrDefault("IVC", BigDecimal.ZERO)));

        return groups;
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

    private void addToMap(Map<String, BigDecimal> map, String key, BigDecimal value) {
        if (value != null) {
            BigDecimal current = map.getOrDefault(key, BigDecimal.ZERO);
            map.put(key, current.add(value));
        }
    }

    private int calculateContributionMonths(List<MemberBalanceSnapshot> snapshots) {
        if (snapshots.isEmpty()) return 0;
        Set<String> uniqueYears = new HashSet<>();
        for (MemberBalanceSnapshot snap : snapshots) {
            if (snap.getAccountingYear() != null && !snap.getAccountingYear().isEmpty()) {
                uniqueYears.add(snap.getAccountingYear());
            }
        }
        return uniqueYears.size() * 12;
    }

    private boolean isPensionEligible(MemberDetailResponseDto memberDetail) {
        try {
            String categoryId = memberDetail.getMemberCategoryId();
            return categoryId != null && !"04".equals(categoryId.trim());
        } catch (Exception e) {
            log.warn("Error checking pension eligibility for {}: {}", memberDetail.getNppfNumber(), e.getMessage());
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

    private AccountingInterestMaster getInterestRateDetail(String accountingYear) {
        return interestMasterRepository
            .findByFinancialYear(accountingYear)
            .orElse(null);
    }

    private int getYearBasisForYear(String accountingYear) {
        // TODO: Fetch from ArrConfiguration table
        return 365;
    }

    private int calculateEOLMonthsFromHistory(
            List<ContributionBifurcationDetail> allContributions,
            LocalDate startDate,
            LocalDate endDate) {

        if (allContributions.isEmpty() || startDate == null || endDate == null) return 0;

        Set<String> contributionMonths = allContributions.stream()
            .filter(c -> c.getCreatedAt() != null)
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

    private String getYearType(String accountingYear) {
        try {
            String[] parts = accountingYear.split("-");
            if (parts.length < 2) {
                return "ACCOUNTING_YEAR";
            }
            int startYear = Integer.parseInt(parts[0]);
            int endYear = Integer.parseInt(parts[1]);

            if (startYear == endYear) {
                if (startYear == TRANSITION_YEAR) {
                    return "TRANSITION_YEAR";
                }
                return "CALENDAR_YEAR";
            }
            return "ACCOUNTING_YEAR";
        } catch (Exception e) {
            log.warn("Error parsing accounting year: {}, defaulting to ACCOUNTING_YEAR", accountingYear);
            return "ACCOUNTING_YEAR";
        }
    }

    private List<YearMonth> getMonthsInYear(String accountingYear) {
        List<YearMonth> months = new ArrayList<>();
        try {
            String[] parts = accountingYear.split("-");
            if (parts.length < 2) {
                return months;
            }
            int startYear = Integer.parseInt(parts[0]);
            int endYear = Integer.parseInt(parts[1]);

            String yearType = getYearType(accountingYear);
            
            if ("ACCOUNTING_YEAR".equals(yearType)) {
                for (int month = 7; month <= 12; month++) {
                    months.add(YearMonth.of(startYear, month));
                }
                for (int month = 1; month <= 6; month++) {
                    months.add(YearMonth.of(endYear, month));
                }
            } else if ("TRANSITION_YEAR".equals(yearType)) {
                for (int month = 7; month <= 12; month++) {
                    months.add(YearMonth.of(startYear, month));
                }
            } else {
                for (int month = 1; month <= 12; month++) {
                    months.add(YearMonth.of(startYear, month));
                }
            }
        } catch (Exception e) {
            log.warn("Could not parse accounting year: {}", accountingYear);
        }
        return months;
    }

    private String getAccountingYearForDate(LocalDate date) {
        if (date == null) {
            return String.valueOf(LocalDate.now().getYear());
        }
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
        if (start == null || end == null) {
            return years;
        }
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
        if (date == null) {
            return String.valueOf(YearMonth.now());
        }
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }

    private BigDecimal n(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private Long getSchemeTypeId(List<MemberBalanceSnapshot> snapshots) {
        return snapshots.isEmpty() ? 1L : 1L;
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
            .build();
    }

    // ========== INNER CLASSES ==========

    @lombok.AllArgsConstructor
    @lombok.Getter
    private static class PFTotals {
        private final BigDecimal totalPrincipal;
        private final BigDecimal totalInterest;
        private final BigDecimal totalBalance;
        private final LocalDate latestDate;
    }

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