package com.claim.claim_processing.integration.contribution.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
import com.claim.claim_processing.integration.contribution.repository.ArrConfigurationRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationDetailRepository;
import com.claim.claim_processing.integration.contribution.service.WrongRemitanceContributionService;
import com.claim.claim_processing.integration.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WrongRemitanceContributionServiceImpl implements WrongRemitanceContributionService {

    private final ContributionBifurcationDetailRepository contributionBifurcationDetailRepository;
    private final ArrConfigurationRepository arrRepo;
    private final MemberService memberService;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final int TRANSITION_YEAR = 2022;

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
                List<ContributionBifurcationDetail> contributions = contributionBifurcationDetailRepository
                        .findAllDetailsForMemberForYear(nppfNumber, yearInt);

                if (contributions != null && !contributions.isEmpty()) {
                    log.info("Found {} contribution records for NPPF: {} in year: {}",
                            contributions.size(), nppfNumber, year);
                    
                    List<WrongRemitanceInitionResponseDto> converted = contributions.stream()
                            .map(this::convertToDto)
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
        log.info("Year: {}, Number of NPPFs: {}", 
                request.getYear(), 
                request.getNppfAndMonthRequest() != null ? request.getNppfAndMonthRequest().size() : 0);

        if (request.getNppfAndMonthRequest() == null || request.getNppfAndMonthRequest().isEmpty()) {
            throw ClaimException.badRequest("At least one NPPF number with month IDs is required");
        }

        List<WrongRemitanceRecalculationResponse> allResults = new ArrayList<>();

        for (NppfAndMonthRequest nppfRequest : request.getNppfAndMonthRequest()) {
            String nppfNumber = nppfRequest.getNppfNumber();
            
            // Extract month IDs
            List<Long> selectedIds = nppfRequest.getMonthIds().stream()
                    .map(RecalculateMemberRequestDTO.MonthIds::getMonthIds)
                    .collect(Collectors.toList());

            log.info("Processing NPPF: {}, Selected IDs: {}", nppfNumber, selectedIds);

            // Get member details
            ApiResponseDTO<MemberDetailResponseDto> memberResponse = memberService.getMemberDetails(nppfNumber);
            if (memberResponse == null || memberResponse.getData() == null) {
                log.error("Member not found: {}", nppfNumber);
                throw ClaimException.notFound("Member not found with nppf number: " + nppfNumber);
            }

            MemberDetailResponseDto memberDetail = memberResponse.getData();
            
            // Process this member
            WrongRemitanceRecalculationResponse memberResult = recalculateWrongRemitanceInternal(
                    nppfNumber, request.getYear(), selectedIds, memberDetail);
            
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
            MemberDetailResponseDto memberDetail) {

        log.info("=== START Wrong Remitance Recalculation ===");
        log.info("NPPF: {}, Target Year: {}, Selected IDs: {}",
                nppfNumber, targetYear, selectedIds);

        try {
            // ========== 1. VALIDATE INPUT ==========
            if (memberDetail == null) {
                throw ClaimException.notFound("Member details not found");
            }

            if (selectedIds == null || selectedIds.isEmpty()) {
                throw ClaimException.badRequest("Please specify which months need recalculation");
            }

            // ========== 2. GET START DATE ==========
            LocalDate startDate = getStartDate(memberDetail);
            if (startDate == null) {
                throw ClaimException.notFound("Member start date not found");
            }
            log.info("Start Date: {}", startDate);

            // ========== 3. GET ALL CONTRIBUTIONS ==========
            List<ContributionBifurcationDetail> allContributions = contributionBifurcationDetailRepository
                    .findByNppfNumberOrderByCreatedAtAsc(nppfNumber);

            if (allContributions.isEmpty()) {
                throw ClaimException.notFound("No contributions found for member");
            }

            log.info("Found {} total contribution records", allContributions.size());

            // ========== 4. GET SELECTED CONTRIBUTIONS ==========
            List<ContributionBifurcationDetail> selectedContributions = allContributions.stream()
                    .filter(c -> selectedIds.contains(c.getId()))
                    .collect(Collectors.toList());

            if (selectedContributions.isEmpty()) {
                throw ClaimException.notFound("No contributions found for the selected IDs");
            }

            Set<String> selectedMonthKeys = selectedContributions.stream()
                    .map(c -> {
                        LocalDate date = c.getCreatedAt().toLocalDate();
                        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                    })
                    .collect(Collectors.toSet());

            List<String> selectedMonthNames = selectedContributions.stream()
                    .map(c -> c.getCreatedAt().toLocalDate().getMonth().toString())
                    .distinct()
                    .collect(Collectors.toList());

            log.info("Selected {} contributions for months: {}", selectedContributions.size(), selectedMonthNames);

            // ========== 5. GET ACCOUNTING YEARS ==========
            List<String> accountingYears = getAccountingYearsFromStartToTarget(startDate, targetYear);
            log.info("Accounting years to process: {}", accountingYears);

            // ========== 6. INITIALIZE OPENING BALANCES ==========
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

            // ========== 7. PROCESS EACH ACCOUNTING YEAR ==========
            ComponentBalances closingBalances = openingBalances;
            OpeningBalanceDto targetYearOpening = null;
            List<RecalculatedMonthDto> recalculatedMonths = new ArrayList<>();
            BigDecimal totalContributions = BigDecimal.ZERO;
            BigDecimal totalInterest = BigDecimal.ZERO;
            
            boolean targetYearFound = false;

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

                int daysForOpening = calculateDaysForIOB(accountingYear, yearType);
                log.info("Days for opening balance interest: {}", daysForOpening);

                boolean isTargetYear = accountingYear.equals(targetYear);
                
                // Capture opening balances for target year
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
                    
                    log.info("✅ OPENING BALANCES CAPTURED FOR TARGET YEAR {}:", targetYear);
                    log.info("  PF_MC: {}, PF_EC: {}", 
                            targetYearOpening.getPfMc(), targetYearOpening.getPfEc());
                    log.info("  P_MC: {}, P_EC: {}", 
                            targetYearOpening.getPMc(), targetYearOpening.getPEc());
                    
                    targetYearFound = true;
                }

                // Calculate interest on opening balances
                ComponentBalances interestOnOpening = calculateInterestOnBalances(
                        openingBalances, rate, daysForOpening, yearBasis);

                ComponentBalances afterOpeningInterest = addBalances(openingBalances, interestOnOpening);

                // Process each month
                ComponentBalances currentBalances = afterOpeningInterest;

                for (YearMonth yearMonth : monthsInYear) {
                    String monthName = yearMonth.getMonth().toString();
                    LocalDate monthStart = yearMonth.atDay(1);
                    String monthKey = yearMonth.getYear() + "-" + String.format("%02d", yearMonth.getMonthValue());

                    boolean shouldReturn = isTargetYear && selectedMonthKeys.contains(monthKey);

                    ContributionBifurcationDetail detail = findContributionForMonth(
                            allContributions, yearMonth);

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
                        monthContribution.setPfMc(n(detail.getPfMc()));
                        monthContribution.setPfEc(n(detail.getPfEc()));
                        monthContribution.setPMc(n(detail.getPensionMc()));
                        monthContribution.setPEc(n(detail.getPensionEc()));
                        monthContribution.setGc(n(detail.getGc()));
                        monthContribution.setVc(n(detail.getVc()));

                        invoiceDate = detail.getCreatedAt().toLocalDate();
                        days = (int) ChronoUnit.DAYS.between(invoiceDate, yearEndDate);
                        if (days < 0) days = 0;

                        ComponentBalances interestOnContributions = calculateInterestOnBalances(
                                monthContribution, rate, days, yearBasis);

                        monthContribution.setPfImc(interestOnContributions.getPfImc());
                        monthContribution.setPfIec(interestOnContributions.getPfIec());
                        monthContribution.setPImc(interestOnContributions.getPImc());
                        monthContribution.setPIec(interestOnContributions.getPIec());
                        monthContribution.setGic(interestOnContributions.getGic());
                        monthContribution.setVic(interestOnContributions.getVic());

                        status = "CONTRIBUTION";
                    }

                    currentBalances = addBalances(currentBalances, monthContribution);

                    if (shouldReturn) {
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
                                .totalContribution(calculateTotalContribution(monthContribution))
                                .totalInterest(calculateTotalInterest(monthContribution))
                                .totalAmount(calculateTotalAmount(monthContribution))
                                .status(status)
                                .build();

                        recalculatedMonths.add(monthDetail);
                        
                        totalContributions = totalContributions.add(monthDetail.getTotalContribution());
                        totalInterest = totalInterest.add(monthDetail.getTotalInterest());
                        
                        log.info("✅ Recalculated month: {} - Total Amount: {}",
                                monthName, monthDetail.getTotalAmount());
                    }
                }

                openingBalances = currentBalances;
                closingBalances = currentBalances;

                log.info("Year {} - Closing Balances:", accountingYear);
                log.info("  PF_MC: {}, PF_EC: {}", 
                        closingBalances.getPfMc(), closingBalances.getPfEc());
                log.info("  P_MC: {}, P_EC: {}", 
                        closingBalances.getPMc(), closingBalances.getPEc());
            }

            // Ensure opening balances is not null
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

            // Build closing balances
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

            WrongRemitanceRecalculationResponse response = WrongRemitanceRecalculationResponse.builder()
                    .nppfNumber(nppfNumber)
                    .targetYear(targetYear)
                    .recalculatedMonthsList(List.of(recalculatedMonthsList))
                    .totalRecalculatedContributions(totalContributions)
                    .totalRecalculatedInterest(totalInterest)
                    .totalRecalculatedAmount(totalAmount)
                    .appliedInterestRate(targetArrConfig != null ? targetArrConfig.getArrRate() : BigDecimal.ZERO)
                    .yearBasis(targetArrConfig != null ? targetArrConfig.getYearBasis() : 365)
                    .calculationDate(LocalDate.now())
                    .status("SUCCESS")
                    .message(String.format("Recalculation completed for %d months in year %s. Processed %d years from %s to %s",
                            recalculatedMonths.size(), targetYear, accountingYears.size(), 
                            accountingYears.isEmpty() ? "N/A" : accountingYears.get(0),
                            accountingYears.isEmpty() ? "N/A" : accountingYears.get(accountingYears.size() - 1)))
                    .build();

            log.info("=== END Wrong Remitance Recalculation ===");
            log.info("Total Years Processed: {}", accountingYears.size());
            log.info("Total Recalculated Amount: {}", totalAmount);
            log.info("Months Returned: {}", recalculatedMonths.size());

            return response;

        } catch (Exception e) {
            log.error("Error in wrong remitance recalculation: {}", e.getMessage(), e);
            throw ClaimException.internalError("Error recalculating: " + e.getMessage());
        }
    }

    // ========== HELPER METHODS ==========

    private WrongRemitanceInitionResponseDto convertToDto(ContributionBifurcationDetail entity) {
        String monthName = entity.getCreatedAt() != null 
                ? entity.getCreatedAt().toLocalDate().getMonth().name() 
                : null;
        
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

    private ContributionBifurcationDetail findContributionForMonth(
            List<ContributionBifurcationDetail> contributions,
            YearMonth yearMonth) {

        String monthKey = yearMonth.getYear() + "-" +
                String.format("%02d", yearMonth.getMonthValue());

        return contributions.stream()
                .filter(c -> c.getCreatedAt() != null)
                .filter(c -> {
                    LocalDate date = c.getCreatedAt().toLocalDate();
                    String key = date.getYear() + "-" +
                            String.format("%02d", date.getMonthValue());
                    return key.equals(monthKey);
                })
                .findFirst()
                .orElse(null);
    }

    private ComponentBalances calculateInterestOnBalances(
            ComponentBalances balances,
            BigDecimal rate,
            int days,
            int yearBasis) {

        if (days <= 0 || rate.compareTo(BigDecimal.ZERO) == 0) {
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

        return ComponentBalances.builder()
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

    private BigDecimal calculateTotalAmount(ComponentBalances balances) {
        return calculateTotalContribution(balances).add(calculateTotalInterest(balances));
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
            // 1. TRY EXACT MATCH
            Optional<ArrConfiguration> arrOpt = arrRepo.findByAccountingYear(accountingYear);
            if (arrOpt.isPresent()) {
                log.info("   ✅ Found ARR configuration for year: {}", accountingYear);
                return arrOpt.get();
            }

            log.warn("   No exact match for: {}", accountingYear);

            // 2. IF YEAR CONTAINS "-", TRY THE YEAR ONLY
            if (accountingYear.contains("-")) {
                String yearOnly = accountingYear.split("-")[0];
                Optional<ArrConfiguration> yearOnlyOpt = arrRepo.findByAccountingYear(yearOnly);
                if (yearOnlyOpt.isPresent()) {
                    log.info("   ✅ Found ARR using year only: {}", yearOnly);
                    return yearOnlyOpt.get();
                }
            }

            // 3. IF YEAR DOESN'T CONTAIN "-", TRY WITH "-"
            if (!accountingYear.contains("-")) {
                String yearRange = accountingYear + "-" + accountingYear;
                Optional<ArrConfiguration> yearRangeOpt = arrRepo.findByAccountingYear(yearRange);
                if (yearRangeOpt.isPresent()) {
                    log.info("   ✅ Found ARR using year range: {}", yearRange);
                    return yearRangeOpt.get();
                }
            }

            // 4. TRY PREVIOUS YEARS
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

            // 5. GET LATEST AVAILABLE ARR
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

            // 6. NO ARR FOUND - THROW EXCEPTION
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