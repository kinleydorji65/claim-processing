package com.claim.claim_processing.integration.contribution.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.RecalculateMemberRequestDTO;
import com.claim.claim_processing.integration.contribution.dto.RecalculateMemberRequestDTO.NppfAndMonthRequest;
import com.claim.claim_processing.integration.contribution.dto.WrongRemitanceInitionResponse;
import com.claim.claim_processing.integration.contribution.dto.WrongRemitanceInitionResponse.WrongRemitanceInitionResponseDto;
import com.claim.claim_processing.integration.contribution.dto.WrongRemitanceRecalculationResponse;
import com.claim.claim_processing.integration.contribution.dto.WrongRemitanceRecalculationResponse.ClosingBalanceDto;
import com.claim.claim_processing.integration.contribution.dto.WrongRemitanceRecalculationResponse.OpeningBalanceDto;
import com.claim.claim_processing.integration.contribution.dto.WrongRemitanceRecalculationResponse.RecalculatedMonthDto;
import com.claim.claim_processing.integration.contribution.entity.ArrConfiguration;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationHeader;
import com.claim.claim_processing.integration.contribution.repository.ArrConfigurationRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationDetailRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationHeaderRepository;
import com.claim.claim_processing.integration.contribution.service.WrongRemitanceContributionService;
import com.claim.claim_processing.integration.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WrongRemitanceContributionServiceImpl implements WrongRemitanceContributionService {

    private final ContributionBifurcationDetailRepository contributionBifurcationDetailRepository;
    private final ContributionBifurcationHeaderRepository contributionBifurcationHeaderRepository;
    private final ArrConfigurationRepository arrRepo;
    private final MemberService memberService;

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

    // ========== GET CONTRIBUTION DETAILS ==========
    @Override
    public ApiResponseDTO<List<WrongRemitanceInitionResponse>> getContributionDetailOfMembers(
            String year, List<String> nppfNumbers) {

        log.info("========== GET CONTRIBUTION DETAILS ==========");
        log.info("Year: {}, NPPFs: {}", year, nppfNumbers);

        try {
            Integer yearInt = Integer.valueOf(year);
            List<WrongRemitanceInitionResponse> allResults = new ArrayList<>();
            
            for (String nppfNumber : nppfNumbers) {
                // Get details with header info
                List<ContributionBifurcationDetail> contributions = contributionBifurcationDetailRepository
                        .findAllDetailsForMemberForYear(nppfNumber, yearInt);

                if (contributions != null && !contributions.isEmpty()) {
                    // Get header info for these contributions
                    Map<Long, ContributionHeaderInfo> headerInfoMap = getHeaderInfoForContributions(contributions);
                    
                    log.info("Found {} contribution records for NPPF: {} in year: {}",
                            contributions.size(), nppfNumber, year);
                    
                    List<WrongRemitanceInitionResponseDto> converted = contributions.stream()
                            .map(c -> convertToDto(c, headerInfoMap))
                            .collect(Collectors.toList());
                    
                    WrongRemitanceInitionResponse response = WrongRemitanceInitionResponse.builder()
                            .nppfNumber(nppfNumber)
                            .wrongRemitances(converted)
                            .build();
                    
                    allResults.add(response);
                } else {
                    log.warn("No contributions found for NPPF: {} in year: {}", nppfNumber, year);
                }
            }

            if (allResults.isEmpty()) {
                log.warn("No contributions found for any NPPF in year: {}", year);
                return ApiResponseDTO.success(new ArrayList<>());
            }

            return ApiResponseDTO.success(allResults);

        } catch (NumberFormatException e) {
            log.error("Invalid year format: {}", year, e);
            throw new IllegalArgumentException("Invalid year format. Please provide a valid year (e.g., 2023)");
        } catch (Exception e) {
            log.error("Error fetching contribution details for NPPFs: {}, Year: {}", nppfNumbers, year, e);
            throw new RuntimeException("Failed to fetch contribution details", e);
        }
    }

    // ========== RECALCULATE WRONG REMITANCE ==========
    @Override
    public ApiResponseDTO<List<WrongRemitanceRecalculationResponse>> recalculateWrongRemitance(
            RecalculateMemberRequestDTO request) {

        log.info("========== RECALCULATE WRONG REMITANCE ==========");
        log.info("Year: {}, withInterest: {}, Number of NPPFs: {}", 
                request.getYear(),
                request.getWithInterest(),
                request.getNppfAndMonthRequest() != null ? request.getNppfAndMonthRequest().size() : 0);

        if (request.getNppfAndMonthRequest() == null || request.getNppfAndMonthRequest().isEmpty()) {
            throw ClaimException.badRequest("At least one NPPF number with month IDs is required");
        }

        boolean withInterest = request.getWithInterest() != null && request.getWithInterest();

        List<WrongRemitanceRecalculationResponse> allResults = new ArrayList<>();

        for (NppfAndMonthRequest nppfRequest : request.getNppfAndMonthRequest()) {
            String nppfNumber = nppfRequest.getNppfNumber();
            
            List<Long> selectedIds = nppfRequest.getMonthIds().stream()
                    .map(RecalculateMemberRequestDTO.MonthIds::getMonthIds)
                    .collect(Collectors.toList());

            log.info("Processing NPPF: {}, Selected IDs: {}, withInterest: {}", nppfNumber, selectedIds, withInterest);

            ApiResponseDTO<MemberDetailResponseDto> memberResponse = memberService.getMemberDetails(nppfNumber);
            if (memberResponse == null || memberResponse.getData() == null) {
                log.error("Member not found: {}", nppfNumber);
                throw ClaimException.notFound("Member not found with nppf number: " + nppfNumber);
            }

            MemberDetailResponseDto memberDetail = memberResponse.getData();
            
            WrongRemitanceRecalculationResponse memberResult = recalculateWrongRemitanceInternal(
                    nppfNumber, request.getYear(), selectedIds, memberDetail, withInterest);
            
            allResults.add(memberResult);
        }

        log.info("Recalculation completed for {} members", allResults.size());
        return ApiResponseDTO.success(allResults);
    }

    // ========== INTERNAL RECALCULATION METHOD ==========
    private WrongRemitanceRecalculationResponse recalculateWrongRemitanceInternal(
            String nppfNumber,
            String targetYear,
            List<Long> selectedIds,
            MemberDetailResponseDto memberDetail,
            boolean withInterest) {

        log.info("=== START Wrong Remitance Recalculation ===");
        log.info("NPPF: {}, Target Year: {}, Selected IDs: {}, withInterest: {}",
                nppfNumber, targetYear, selectedIds, withInterest);

        try {
            if (memberDetail == null) {
                throw ClaimException.notFound("Member details not found");
            }

            if (selectedIds == null || selectedIds.isEmpty()) {
                throw ClaimException.badRequest("Please specify which months need recalculation");
            }

            LocalDate startDate = getStartDate(memberDetail);
            if (startDate == null) {
                throw ClaimException.notFound("Member start date not found");
            }
            log.info("Start Date: {}", startDate);

            // ========== GET ALL CONTRIBUTIONS ==========
            List<ContributionBifurcationDetail> allContributions = contributionBifurcationDetailRepository
                    .findByNppfNumberOrderByCreatedAtAsc(nppfNumber);

            if (allContributions.isEmpty()) {
                throw ClaimException.notFound("No contributions found for member");
            }

            log.info("Found {} total contribution records", allContributions.size());

            // ========== GET HEADER INFO FOR ALL CONTRIBUTIONS ==========
            Map<Long, ContributionHeaderInfo> headerInfoMap = getHeaderInfoForContributions(allContributions);
            log.info("Found header info for {} contributions", headerInfoMap.size());

            // ========== GET SELECTED CONTRIBUTIONS ==========
            List<ContributionBifurcationDetail> selectedContributions = allContributions.stream()
                    .filter(c -> selectedIds.contains(c.getId()))
                    .collect(Collectors.toList());

            if (selectedContributions.isEmpty()) {
                throw ClaimException.notFound("No contributions found for the selected IDs");
            }

            // Build selected month keys using header info
            Set<String> selectedMonthKeys = selectedContributions.stream()
                    .map(c -> {
                        ContributionHeaderInfo headerInfo = headerInfoMap.get(c.getBifId());
                        if (headerInfo != null) {
                            return headerInfo.getYear() + "-" + String.format("%02d", headerInfo.getMonth());
                        }
                        // Fallback to created_at
                        LocalDate date = c.getCreatedAt().toLocalDate();
                        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                    })
                    .collect(Collectors.toSet());

            List<String> selectedMonthNames = selectedContributions.stream()
                    .map(c -> {
                        ContributionHeaderInfo headerInfo = headerInfoMap.get(c.getBifId());
                        if (headerInfo != null) {
                            return getMonthName(headerInfo.getMonth());
                        }
                        return c.getCreatedAt().toLocalDate().getMonth().toString();
                    })
                    .distinct()
                    .collect(Collectors.toList());

            log.info("Selected {} contributions for months: {}", selectedContributions.size(), selectedMonthNames);

            // ========== GET ACCOUNTING YEARS ==========
            List<String> accountingYears = getAccountingYearsFromStartToTarget(startDate, targetYear);
            log.info("Accounting years to process: {}", accountingYears);

            // ========== INITIALIZE OPENING BALANCES ==========
            ComponentBalances openingBalances = ComponentBalances.builder()
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

            // ========== PROCESS EACH ACCOUNTING YEAR ==========
            ComponentBalances closingBalances = openingBalances;
            OpeningBalanceDto targetYearOpening = null;
            List<RecalculatedMonthDto> recalculatedMonths = new ArrayList<>();
            BigDecimal totalContributions = BigDecimal.ZERO;
            BigDecimal totalInterest = BigDecimal.ZERO;
            
            boolean targetYearFound = false;
            LocalDate currentDate = LocalDate.now();

            for (String accountingYear : accountingYears) {
                YearType yearType = getYearType(accountingYear);
                log.info("==========================================");
                log.info("Processing Year: {}, Type: {}", accountingYear, yearType);

                ArrConfiguration arrConfig = getArrConfigurationWithFallback(accountingYear);
                if (arrConfig == null) {
                    log.warn("No ARR config for year: {}, skipping", accountingYear);
                    continue;
                }

                BigDecimal rate = arrConfig.getArrRate() != null ? arrConfig.getArrRate() : BigDecimal.ZERO;
                int yearBasis = arrConfig.getYearBasis();
                log.info("ARR Rate: {}%, Year Basis: {} days", 
                        rate.multiply(HUNDRED), yearBasis);

                LocalDate yearEndDate = getYearEndDate(accountingYear, yearType);
                log.info("Year End Date: {}", yearEndDate);

                List<YearMonth> monthsInYear = getMonthsInYear(accountingYear, yearType);
                log.info("Months in year: {}", monthsInYear.size());

                boolean isTargetYear = accountingYear.equals(targetYear);
                
                if (isTargetYear) {
                    targetYearOpening = OpeningBalanceDto.builder()
                            .year(targetYear)
                            .pfMc(openingBalances.getPfMc())
                            .pfEc(openingBalances.getPfEc())
                            .pfImc(openingBalances.getPfImc())
                            .pfIec(openingBalances.getPfIec())
                            .pMc(openingBalances.getPMc())
                            .pEc(openingBalances.getPEc())
                            .pImc(openingBalances.getPImc())
                            .pIec(openingBalances.getPIec())
                            .gc(openingBalances.getGc())
                            .gic(openingBalances.getGic())
                            .vc(openingBalances.getVc())
                            .vic(openingBalances.getVic())
                            .ivc(openingBalances.getIvc())
                            .igc(openingBalances.getIgc())
                            .build();
                    
                    log.info("✅ OPENING BALANCE CAPTURED FOR TARGET YEAR {}:", targetYear);
                    targetYearFound = true;
                }

                // ===== CALCULATE INTEREST ON OPENING BALANCES (IOB) =====
                ComponentBalances interestOnOpening;
                if (withInterest) {
                    int daysForOpening = calculateDaysForIOB(accountingYear, yearType);
                    log.info("Days for opening balance interest (IOB): {}", daysForOpening);
                    
                    interestOnOpening = calculateInterestOnBalances(openingBalances, rate, daysForOpening, yearBasis);
                } else {
                    interestOnOpening = ComponentBalances.builder()
                            .pfImc(BigDecimal.ZERO)
                            .pfIec(BigDecimal.ZERO)
                            .pImc(BigDecimal.ZERO)
                            .pIec(BigDecimal.ZERO)
                            .gic(BigDecimal.ZERO)
                            .vic(BigDecimal.ZERO)
                            .ivc(BigDecimal.ZERO)
                            .igc(BigDecimal.ZERO)
                            .build();
                    log.info("⏭️ Skipping IOB for year {} (withInterest=false)", accountingYear);
                }

                ComponentBalances currentBalances = addBalances(openingBalances, interestOnOpening);

                for (YearMonth yearMonth : monthsInYear) {
                    String monthName = yearMonth.getMonth().toString();
                    LocalDate monthStart = yearMonth.atDay(1);
                    
                    // ✅ Find contribution using header info
                    ContributionBifurcationDetail detail = findContributionForMonthFromHeader(
                            allContributions, headerInfoMap, yearMonth);

                    String monthKey = yearMonth.getYear() + "-" + String.format("%02d", yearMonth.getMonthValue());
                    boolean isSelectedMonth = selectedMonthKeys.contains(monthKey);
                    boolean isTargetYearMonth = isTargetYear && isSelectedMonth;

                    ComponentBalances monthContribution = ComponentBalances.builder()
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

                    LocalDate invoiceDate = null;
                    int days = 0;
                    String status = "EOL";

                    if (detail != null) {
                        // Get invoice date from header if available
                        ContributionHeaderInfo headerInfo = headerInfoMap.get(detail.getBifId());
                        if (headerInfo != null) {
                            invoiceDate = LocalDate.of(headerInfo.getYear(), headerInfo.getMonth(), 1);
                        } else {
                            invoiceDate = detail.getCreatedAt().toLocalDate();
                        }

                        monthContribution.setPfMc(n(detail.getPfMc()));
                        monthContribution.setPfEc(n(detail.getPfEc()));
                        monthContribution.setPMc(n(detail.getPensionMc()));
                        monthContribution.setPEc(n(detail.getPensionEc()));
                        monthContribution.setGc(n(detail.getGc()));
                        monthContribution.setVc(n(detail.getVc()));

                        if (withInterest && isSelectedMonth) {
                            days = (int) ChronoUnit.DAYS.between(invoiceDate, currentDate);
                            if (days < 0) days = 0;
                            
                            ComponentBalances interestOnContributions = calculateInterestOnBalances(
                                    monthContribution, rate, days, yearBasis);
                            
                            monthContribution.setPfImc(interestOnContributions.getPfImc());
                            monthContribution.setPfIec(interestOnContributions.getPfIec());
                            monthContribution.setPImc(interestOnContributions.getPImc());
                            monthContribution.setPIec(interestOnContributions.getPIec());
                            monthContribution.setGic(interestOnContributions.getGic());
                            monthContribution.setVic(interestOnContributions.getVic());
                            
                            status = "CONTRIBUTION_WITH_INTEREST";
                            
                            log.info("   📊 SELECTED MONTH WITH INTEREST: {}, Days to current date: {}", 
                                    monthName, days);
                        } else if (!withInterest && isSelectedMonth) {
                            status = "CONTRIBUTION_WITHOUT_INTEREST";
                            log.info("   📋 SELECTED MONTH WITHOUT INTEREST: {}", monthName);
                        } else {
                            status = "CONTRIBUTION_NOT_SELECTED";
                            log.info("   📋 NON-SELECTED MONTH: {}", monthName);
                        }
                    }

                    currentBalances = addBalances(currentBalances, monthContribution);

                    if (isTargetYearMonth) {
                        BigDecimal monthTotalContribution = calculateTotalContribution(monthContribution);
                        BigDecimal monthTotalInterest = calculateTotalInterest(monthContribution);
                        BigDecimal monthTotalAmount = monthTotalContribution.add(monthTotalInterest);
                        
                        RecalculatedMonthDto monthDetail = RecalculatedMonthDto.builder()
                                .month(yearMonth.toString())
                                .monthName(monthName)
                                .invoiceDate(invoiceDate != null ? invoiceDate : monthStart)
                                .daysForInterest(days)
                                .interestRate(rate)
                                .pfMc(monthContribution.getPfMc())
                                .pfEc(monthContribution.getPfEc())
                                .pfImc(monthContribution.getPfImc())
                                .pfIec(monthContribution.getPfIec())
                                .pMc(monthContribution.getPMc())
                                .pEc(monthContribution.getPEc())
                                .pImc(monthContribution.getPImc())
                                .pIec(monthContribution.getPIec())
                                .gc(monthContribution.getGc())
                                .gic(monthContribution.getGic())
                                .vc(monthContribution.getVc())
                                .vic(monthContribution.getVic())
                                .ivc(monthContribution.getIvc())
                                .igc(monthContribution.getIgc())
                                .totalContribution(monthTotalContribution)
                                .totalInterest(monthTotalInterest)
                                .totalAmount(monthTotalAmount)
                                .status(status)
                                .build();

                        recalculatedMonths.add(monthDetail);
                        
                        totalContributions = totalContributions.add(monthTotalContribution);
                        totalInterest = totalInterest.add(monthTotalInterest);
                        
                        log.info("✅ Recalculated month: {} - Contribution: {}, Interest: {}, Total: {}",
                                monthName, monthTotalContribution, monthTotalInterest, monthTotalAmount);
                    }
                }

                openingBalances = currentBalances;
                closingBalances = currentBalances;
            }

            if (!targetYearFound) {
                log.warn("Target year {} not found in accounting years list!", targetYear);
                targetYearOpening = OpeningBalanceDto.builder()
                        .year(targetYear)
                        .pfMc(closingBalances.getPfMc())
                        .pfEc(closingBalances.getPfEc())
                        .pfImc(closingBalances.getPfImc())
                        .pfIec(closingBalances.getPfIec())
                        .pMc(closingBalances.getPMc())
                        .pEc(closingBalances.getPEc())
                        .pImc(closingBalances.getPImc())
                        .pIec(closingBalances.getPIec())
                        .gc(closingBalances.getGc())
                        .gic(closingBalances.getGic())
                        .vc(closingBalances.getVc())
                        .vic(closingBalances.getVic())
                        .ivc(closingBalances.getIvc())
                        .igc(closingBalances.getIgc())
                        .build();
            }

            ClosingBalanceDto closingBalanceDto = ClosingBalanceDto.builder()
                    .pfMc(closingBalances.getPfMc())
                    .pfEc(closingBalances.getPfEc())
                    .pfImc(closingBalances.getPfImc())
                    .pfIec(closingBalances.getPfIec())
                    .pMc(closingBalances.getPMc())
                    .pEc(closingBalances.getPEc())
                    .pImc(closingBalances.getPImc())
                    .pIec(closingBalances.getPIec())
                    .gc(closingBalances.getGc())
                    .gic(closingBalances.getGic())
                    .vc(closingBalances.getVc())
                    .vic(closingBalances.getVic())
                    .ivc(closingBalances.getIvc())
                    .igc(closingBalances.getIgc())
                    .build();

            // ================================================================
            // BUILD RESPONSE
            // ================================================================
            BigDecimal totalAmount = totalContributions.add(totalInterest);

            ArrConfiguration targetArrConfig = getArrConfigurationWithFallback(targetYear);

            WrongRemitanceRecalculationResponse.RecalculatedMonthsList recalculatedMonthsList = 
                    WrongRemitanceRecalculationResponse.RecalculatedMonthsList.builder()
                        .month(targetYear)
                        .openingBalances(targetYearOpening)
                        .recalculatedMonths(recalculatedMonths)
                        .closingBalances(closingBalanceDto)
                        .build();

            String firstYear = accountingYears.isEmpty() ? "N/A" : accountingYears.get(0);
            String lastYear = accountingYears.isEmpty() ? "N/A" : accountingYears.get(accountingYears.size() - 1);
            String currentDateStr = LocalDate.now().toString();
            
            String message;
            if (withInterest) {
                message = String.format(
                        "Recalculation completed for %d selected months in year %s. " +
                        "Opening balance included with interest (IOB). " +
                        "Interest calculated for selected months up to current date (%s). " +
                        "Processed %d years from %s to %s",
                        recalculatedMonths.size(), 
                        targetYear, 
                        currentDateStr,
                        accountingYears.size(),
                        firstYear,
                        lastYear
                );
            } else {
                message = String.format(
                        "Recalculation completed for %d selected months in year %s. " +
                        "Opening balance included without interest (withInterest=false). " +
                        "Processed %d years from %s to %s",
                        recalculatedMonths.size(), 
                        targetYear,
                        accountingYears.size(),
                        firstYear,
                        lastYear
                );
            }

            WrongRemitanceRecalculationResponse response = WrongRemitanceRecalculationResponse.builder()
                    .nppfNumber(nppfNumber)
                    .targetYear(targetYear)
                    .recalculatedMonthsList(List.of(recalculatedMonthsList))
                    .totalRecalculatedContributions(totalContributions)
                    .totalRecalculatedInterest(totalInterest)
                    .totalRecalculatedAmount(totalAmount)
                    .appliedInterestRate(withInterest && targetArrConfig != null ? targetArrConfig.getArrRate() : BigDecimal.ZERO)
                    .yearBasis(targetArrConfig != null ? targetArrConfig.getYearBasis() : 365)
                    .calculationDate(LocalDate.now())
                    .status("SUCCESS")
                    .message(message)
                    .build();

            log.info("=== END Wrong Remitance Recalculation ===");
            log.info("Total Years Processed: {}", accountingYears.size());
            log.info("Total Recalculated Amount: {}", totalAmount);

            return response;

        } catch (Exception e) {
            log.error("Error in wrong remitance recalculation: {}", e.getMessage(), e);
            throw ClaimException.internalError("Error recalculating: " + e.getMessage());
        }
    }

    // ========== HELPER METHODS ==========

    /**
     * Get header info for a list of contributions
     */
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

    /**
     * Find contribution for a month using header info
     */
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
                    // Fallback: use created_at
                    LocalDate date = c.getCreatedAt().toLocalDate();
                    return date.getYear() == yearMonth.getYear() &&
                           date.getMonthValue() == yearMonth.getMonthValue();
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * Convert to DTO with header info
     */
    private WrongRemitanceInitionResponseDto convertToDto(
            ContributionBifurcationDetail entity,
            Map<Long, ContributionHeaderInfo> headerInfoMap) {
        
        String monthName = null;
        ContributionHeaderInfo headerInfo = headerInfoMap.get(entity.getBifId());
        if (headerInfo != null) {
            monthName = getMonthName(headerInfo.getMonth());
        } else if (entity.getCreatedAt() != null) {
            monthName = entity.getCreatedAt().toLocalDate().getMonth().name();
        }
        
        return WrongRemitanceInitionResponseDto.builder()
                .id(entity.getId())
                .pid(entity.getPid())
                .ruleSource(entity.getRuleSource())
                .basicSalary(entity.getBasicSalary())
                .ec(entity.getEc())
                .mc(entity.getMc())
                .gc(entity.getGc())
                .vc(entity.getVc())
                .totalContribution(entity.getTotalContribution())
                .pensionMc(entity.getPensionMc())
                .pensionEc(entity.getPensionEc())
                .pfMc(entity.getPfMc())
                .pfEc(entity.getPfEc())
                .month(monthName)
                .build();
    }

    /**
     * Get month name from month number
     */
    private String getMonthName(int month) {
        String[] monthNames = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                               "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        return monthNames[month - 1];
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


    private ComponentBalances calculateInterestOnBalances(
            ComponentBalances balances,
            BigDecimal rate,
            int days,
            int yearBasis) {

        log.debug("Calculating interest - Rate: {}, Days: {}, YearBasis: {}", rate, days, yearBasis);

        if (days <= 0 || rate.compareTo(BigDecimal.ZERO) == 0) {
            log.debug("No interest calculated - days: {}, rate: {}", days, rate);
            return ComponentBalances.builder()
                    .pfImc(BigDecimal.ZERO)
                    .pfIec(BigDecimal.ZERO)
                    .pImc(BigDecimal.ZERO)
                    .pIec(BigDecimal.ZERO)
                    .gic(BigDecimal.ZERO)
                    .vic(BigDecimal.ZERO)
                    .ivc(BigDecimal.ZERO)
                    .igc(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
        BigDecimal daysFactor = BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(yearBasis), 10, RM);

        ComponentBalances result = ComponentBalances.builder()
                .pfImc(n(balances.getPfMc())
                        .multiply(rateFactor)
                        .multiply(daysFactor)
                        .setScale(2, RM))
                .pfIec(n(balances.getPfEc())
                        .multiply(rateFactor)
                        .multiply(daysFactor)
                        .setScale(2, RM))
                .pImc(n(balances.getPMc())
                        .multiply(rateFactor)
                        .multiply(daysFactor)
                        .setScale(2, RM))
                .pIec(n(balances.getPEc())
                        .multiply(rateFactor)
                        .multiply(daysFactor)
                        .setScale(2, RM))
                .gic(n(balances.getGc())
                        .multiply(rateFactor)
                        .multiply(daysFactor)
                        .setScale(2, RM))
                .vic(n(balances.getVc())
                        .multiply(rateFactor)
                        .multiply(daysFactor)
                        .setScale(2, RM))
                .ivc(BigDecimal.ZERO)
                .igc(BigDecimal.ZERO)
                .build();

        return result;
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
                .add(n(balances.getVc()))
                .add(n(balances.getIvc()))
                .add(n(balances.getIgc()));
    }

    private BigDecimal calculateTotalInterest(ComponentBalances balances) {
        return n(balances.getPfImc())
                .add(n(balances.getPfIec()))
                .add(n(balances.getPImc()))
                .add(n(balances.getPIec()))
                .add(n(balances.getGic()))
                .add(n(balances.getVic()));
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

    private List<String> getAccountingYearsFromStartToTarget(LocalDate startDate, String targetYear) {
        List<String> years = new ArrayList<>();
        int startYear = startDate.getYear();
        int targetYearInt = Integer.parseInt(targetYear);

        for (int year = startYear; year <= targetYearInt; year++) {
            if (year == TRANSITION_YEAR) {
                years.add(String.valueOf(year));
            } else if (year < TRANSITION_YEAR) {
                years.add(year + "-" + (year + 1));
            } else {
                years.add(String.valueOf(year));
            }
        }
        return years;
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

    private int calculateDaysForIOB(String accountingYear, YearType yearType) {
        try {
            int startYear = !accountingYear.contains("-") ?
                    Integer.parseInt(accountingYear) :
                    Integer.parseInt(accountingYear.split("-")[0]);
            int endYear = !accountingYear.contains("-") ?
                    startYear :
                    Integer.parseInt(accountingYear.split("-")[1]);

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

    private ArrConfiguration getArrConfigurationWithFallback(String accountingYear) {
        if (accountingYear == null || accountingYear.isBlank()) {
            throw ClaimException.badRequest("Accounting year cannot be null or blank");
        }

        log.info("   Looking for ARR configuration for year: {}", accountingYear);

        try {
            Optional<ArrConfiguration> arrOpt = arrRepo.findByAccountingYear(accountingYear);
            if (arrOpt.isPresent()) {
                log.info("   ✅ Found ARR configuration for year: {}", accountingYear);
                return arrOpt.get();
            }

            log.warn("   No exact match for: {}", accountingYear);

            if (accountingYear.contains("-")) {
                String yearOnly = accountingYear.split("-")[0];
                Optional<ArrConfiguration> yearOnlyOpt = arrRepo.findByAccountingYear(yearOnly);
                if (yearOnlyOpt.isPresent()) {
                    log.info("   ✅ Found ARR using year only: {}", yearOnly);
                    return yearOnlyOpt.get();
                }
            }

            if (!accountingYear.contains("-")) {
                String yearRange = accountingYear + "-" + accountingYear;
                Optional<ArrConfiguration> yearRangeOpt = arrRepo.findByAccountingYear(yearRange);
                if (yearRangeOpt.isPresent()) {
                    log.info("   ✅ Found ARR using year range: {}", yearRange);
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
                        log.info("   ✅ Using ARR from previous year: {} ({} years back)", prevYearStr, i);
                        return prevOpt.get();
                    }

                    Optional<ArrConfiguration> prevRangeOpt = arrRepo.findByAccountingYear(prevYearRange);
                    if (prevRangeOpt.isPresent()) {
                        log.info("   ✅ Using ARR from previous year range: {} ({} years back)", prevYearRange, i);
                        return prevRangeOpt.get();
                    }
                }
            } catch (Exception e) {
                log.warn("   Could not parse year from: {}", accountingYear);
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
                log.warn("   ⚠️ Using latest available ARR from year: {} as fallback", latest.getAccountingYear());
                return latest;
            }

            String errorMsg = String.format(
                    "❌ No ARR configuration found for accounting year: %s. " +
                    "Please configure ARR rates in the database.", 
                    accountingYear);
            
            log.error(errorMsg);
            throw ClaimException.badRequest(errorMsg);

        } catch (ClaimException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format(
                    "Error getting ARR configuration for year: %s - %s", 
                    accountingYear, e.getMessage());
            log.error(errorMsg, e);
            throw ClaimException.internalError(errorMsg);
        }
    }

    private BigDecimal n(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    // ========== ENUM ==========
    public enum YearType {
        ACCOUNTING_YEAR,
        TRANSITION_YEAR,
        CALENDAR_YEAR
    }

    // ========== INNER CLASS FOR BALANCES ==========
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
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