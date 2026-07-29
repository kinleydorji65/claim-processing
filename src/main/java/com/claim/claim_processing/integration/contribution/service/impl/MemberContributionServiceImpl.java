package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.repository.others.CutoffServiceMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.dto.ExcessServiceResultDto;
import com.claim.claim_processing.integration.contribution.dto.ExcessYearDetailDto;
import com.claim.claim_processing.integration.contribution.entity.ArrConfiguration;
import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;
import com.claim.claim_processing.integration.contribution.entity.MemberBalanceSnapshot;
import com.claim.claim_processing.integration.contribution.repository.ArrConfigurationRepository;
import com.claim.claim_processing.integration.contribution.repository.ContributionBifurcationDetailRepository;
import com.claim.claim_processing.integration.contribution.repository.MemberBalanceSnapshotRepository;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import lombok.Builder;
import lombok.Data;
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
    
    private final ExcessServiceCalculator excessServiceCalculator;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final int TRANSITION_YEAR = 2022;

    // ================================================================
    // MAIN METHOD
    // ================================================================

    @Override
    public MemberContributionSummary getContributionSummary(
            MemberDetailResponseDto memberDetail,
            LocalDate relieveDate) {

        LocalDate asOfDate = LocalDate.now();
        log.info("🔍 Calculating contributions as of current date: {}", asOfDate);
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
                log.error("❌ No contributions found for member: {}", nppfNumber);
                throw ClaimException.notFound("No contributions found for member: " + nppfNumber);
            }

            log.info("✅ Found {} contribution records", allContributions.size());

            // ================================================================
            // STEP 2: GET LAST CONTRIBUTION DATE
            // ================================================================
            LocalDate lastContributionDate = getLastContributionDate(allContributions);
            log.info("Last Contribution Date: {}", lastContributionDate);

            // ================================================================
            // STEP 3: GET RATE AND YEAR BASIS
            // ================================================================
            String currentAccountingYear = getAccountingYearForDate(asOfDate);
            log.info("Current Accounting Year: {}", currentAccountingYear);
            
            ArrConfiguration arrConfig = getArrConfiguration(currentAccountingYear);

            log.info("ARR Config found: {}", arrConfig != null ? "✅ YES" : "❌ NO");
            if (arrConfig != null) {
                log.info("  Rate: {}", arrConfig.getArrRate());
                log.info("  Year Basis: {}", arrConfig.getYearBasis());
                log.info("  Year Start: {}", arrConfig.getYearStartDate());
                log.info("  Year End: {}", arrConfig.getYearEndDate());
            }
            
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
                log.warn("⚠️ No ARR configuration found for year: {}, using defaults", currentAccountingYear);
                rate = BigDecimal.valueOf(6.5);
                yearBasis = 365;
            }

            log.info("✅ Final Interest Rate: {}%, Year Basis: {}", rate, yearBasis);

            // ================================================================
            // STEP 4: GET PF OPENING BALANCES (FROM SNAPSHOT OR CALCULATE)
            // ================================================================
            MemberBalanceSnapshot previousSnapshot = getPreviousYearSnapshot(cid, nppfNumber, asOfDate.getYear());

            // PF Opening balances
            BigDecimal openingBalancePfEc = BigDecimal.ZERO;
            BigDecimal openingBalancePfMc = BigDecimal.ZERO;
            BigDecimal previousInterestPfEc = BigDecimal.ZERO;
            BigDecimal previousInterestPfMc = BigDecimal.ZERO;

            // GC & VC Opening balances
            BigDecimal openingBalanceGc = BigDecimal.ZERO;
            BigDecimal openingBalanceVc = BigDecimal.ZERO;
            BigDecimal previousInterestGc = BigDecimal.ZERO;
            BigDecimal previousInterestVc = BigDecimal.ZERO;

            boolean useSnapshot = false;

            if (previousSnapshot != null) {
                // Check if snapshot has meaningful PF data
                BigDecimal snapshotTotal = n(previousSnapshot.getPfEc())
                        .add(n(previousSnapshot.getPfMc()));
                
                if (snapshotTotal.compareTo(BigDecimal.ZERO) > 0) {
                    log.info("Using previous years' data from snapshot: {}", previousSnapshot.getAccountingYear());
                    useSnapshot = true;

                    openingBalancePfEc = n(previousSnapshot.getPfEc());
                    openingBalancePfMc = n(previousSnapshot.getPfMc());
                    openingBalanceGc = n(previousSnapshot.getGc());
                    openingBalanceVc = n(previousSnapshot.getVc());

                    previousInterestPfEc = n(previousSnapshot.getInterestEc());
                    previousInterestPfMc = n(previousSnapshot.getInterestMc());
                    previousInterestGc = n(previousSnapshot.getInterestGc());
                    previousInterestVc = n(previousSnapshot.getInterestVc());

                    log.info("PF Opening Balance from snapshot - PF_EC: {}, PF_MC: {}, PF_IEC: {}, PF_IMC: {}",
                            openingBalancePfEc, openingBalancePfMc, previousInterestPfEc, previousInterestPfMc);
                    log.info("GC/VC Opening Balance from snapshot - GC: {}, VC: {}", openingBalanceGc, openingBalanceVc);
                }
            }

            // ✅ If snapshot is not available or has zero balance, calculate from contributions
            if (!useSnapshot) {
                log.info("Snapshot has no PF data - calculating PF opening balance from historical contributions");
                PfOpeningBalanceResult pfResult = calculatePfOpeningBalanceFromContributions(
                    allContributions, asOfDate.getYear(), rate, yearBasis);
                
                openingBalancePfEc = pfResult.getPfEc();
                openingBalancePfMc = pfResult.getPfMc();
                previousInterestPfEc = pfResult.getInterestPfEc();
                previousInterestPfMc = pfResult.getInterestPfMc();
                
                log.info("PF Opening Balance from calculation - PF_EC: {}, PF_MC: {}, PF_IEC: {}, PF_IMC: {}",
                        openingBalancePfEc, openingBalancePfMc, previousInterestPfEc, previousInterestPfMc);
            }

            // ================================================================
            // STEP 5: CALCULATE CURRENT YEAR INTEREST ON OPENING BALANCE (PF, GC, VC)
            // ================================================================
            LocalDate yearStart = LocalDate.of(asOfDate.getYear(), 1, 1);
            long daysInCurrentYear = ChronoUnit.DAYS.between(yearStart, asOfDate);

            log.info("Days in current year (Jan 1 to asOfDate): {}", daysInCurrentYear);

            BigDecimal interestOnOpeningPfEc = calculateInterestOnBalance(
                    openingBalancePfEc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningPfMc = calculateInterestOnBalance(
                    openingBalancePfMc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningGc = calculateInterestOnBalance(
                    openingBalanceGc, rate, daysInCurrentYear, yearBasis);
            BigDecimal interestOnOpeningVc = calculateInterestOnBalance(
                    openingBalanceVc, rate, daysInCurrentYear, yearBasis);

            log.info("Interest on Opening Balances for current year:");
            log.info("  PF_EC: {}", interestOnOpeningPfEc);
            log.info("  PF_MC: {}", interestOnOpeningPfMc);
            log.info("  GC: {}", interestOnOpeningGc);
            log.info("  VC: {}", interestOnOpeningVc);

            // ================================================================
            // STEP 6: CALCULATE CURRENT YEAR CONTRIBUTIONS AND THEIR INTEREST
            // ================================================================
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

            long daysForCurrentYearContributions = daysInCurrentYear;

            log.info("=== Processing Current Year ({}) Contributions ===", asOfDate.getYear());
            
            int totalPosted = 0;
            int totalCurrentYear = 0;
            int totalSkipped = 0;

            for (ContributionBifurcationDetail contrib : allContributions) {
                String status = contrib.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    totalSkipped++;
                    continue;
                }
                totalPosted++;

                LocalDate contribDate = contrib.getCreatedAt().toLocalDate();

                if (contribDate.getYear() != asOfDate.getYear()) {
                    continue;
                }
                totalCurrentYear++;

                if (log.isDebugEnabled()) {
                    log.debug("Processing contribution for date: {}, ID: {}", contribDate, contrib.getId());
                }

                // PF_EC
                BigDecimal pfEc = contrib.getPfEc() != null ? contrib.getPfEc() : BigDecimal.ZERO;
                if (pfEc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearPfEc = currentYearPfEc.add(pfEc);
                    BigDecimal interest = calculateInterestOnBalance(pfEc, rate, daysForCurrentYearContributions, yearBasis);
                    interestOnCurrentYearPfEc = interestOnCurrentYearPfEc.add(interest);
                }

                // PF_MC
                BigDecimal pfMc = contrib.getPfMc() != null ? contrib.getPfMc() : BigDecimal.ZERO;
                if (pfMc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearPfMc = currentYearPfMc.add(pfMc);
                    BigDecimal interest = calculateInterestOnBalance(pfMc, rate, daysForCurrentYearContributions, yearBasis);
                    interestOnCurrentYearPfMc = interestOnCurrentYearPfMc.add(interest);
                }

                // PENSION_EC (P_EC)
                BigDecimal pensionEc = contrib.getPensionEc() != null ? contrib.getPensionEc() : BigDecimal.ZERO;
                if (pensionEc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearPensionEc = currentYearPensionEc.add(pensionEc);
                    BigDecimal interest = calculateInterestOnBalance(pensionEc, rate, daysForCurrentYearContributions, yearBasis);
                    interestOnCurrentYearPensionEc = interestOnCurrentYearPensionEc.add(interest);
                }

                // PENSION_MC (P_MC)
                BigDecimal pensionMc = contrib.getPensionMc() != null ? contrib.getPensionMc() : BigDecimal.ZERO;
                if (pensionMc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearPensionMc = currentYearPensionMc.add(pensionMc);
                    BigDecimal interest = calculateInterestOnBalance(pensionMc, rate, daysForCurrentYearContributions, yearBasis);
                    interestOnCurrentYearPensionMc = interestOnCurrentYearPensionMc.add(interest);
                }

                // GC
                BigDecimal gc = contrib.getGc() != null ? contrib.getGc() : BigDecimal.ZERO;
                if (gc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearGc = currentYearGc.add(gc);
                    BigDecimal interest = calculateInterestOnBalance(gc, rate, daysForCurrentYearContributions, yearBasis);
                    interestOnCurrentYearGc = interestOnCurrentYearGc.add(interest);
                }

                // VC
                BigDecimal vc = contrib.getVc() != null ? contrib.getVc() : BigDecimal.ZERO;
                if (vc.compareTo(BigDecimal.ZERO) > 0) {
                    currentYearVc = currentYearVc.add(vc);
                    BigDecimal interest = calculateInterestOnBalance(vc, rate, daysForCurrentYearContributions, yearBasis);
                    interestOnCurrentYearVc = interestOnCurrentYearVc.add(interest);
                }
            }

            log.info("Total POSTED contributions: {}", totalPosted);
            log.info("Total SKIPPED (not POSTED): {}", totalSkipped);
            log.info("Total contributions for {}: {}", asOfDate.getYear(), totalCurrentYear);
            log.info("Current Year PF_EC: {}", currentYearPfEc);
            log.info("Current Year PF_MC: {}", currentYearPfMc);
            log.info("Current Year P_EC: {}", currentYearPensionEc);
            log.info("Current Year P_MC: {}", currentYearPensionMc);
            log.info("Current Year GC: {}", currentYearGc);
            log.info("Current Year VC: {}", currentYearVc);
            log.info("Interest on Current Year PF_EC: {}", interestOnCurrentYearPfEc);
            log.info("Interest on Current Year PF_MC: {}", interestOnCurrentYearPfMc);
            log.info("Interest on Current Year P_EC: {}", interestOnCurrentYearPensionEc);
            log.info("Interest on Current Year P_MC: {}", interestOnCurrentYearPensionMc);
            log.info("Interest on Current Year GC: {}", interestOnCurrentYearGc);
            log.info("Interest on Current Year VC: {}", interestOnCurrentYearVc);

            // ================================================================
            // STEP 7: CHECK FOR EXCESS SERVICE AND CALCULATE PENSION OPENING BALANCE
            // ================================================================
            ExcessServiceResultDto excessResult = null;
            List<YearMonth> monthsToExclude = new ArrayList<>();
            PensionRebuildResult rebuildResult = null;
            boolean hasExcessService = false;

            // Pension opening balances - will be calculated differently based on excess service
            BigDecimal openingBalancePensionEc = BigDecimal.ZERO;
            BigDecimal openingBalancePensionMc = BigDecimal.ZERO;
            BigDecimal openingBalancePensionIec = BigDecimal.ZERO;
            BigDecimal openingBalancePensionImc = BigDecimal.ZERO;

            if (relieveDate != null && isPensionEligible(memberDetail)) {
                log.info("=== Checking for Excess Service ===");
                try {
                    excessResult = excessServiceCalculator.calculateExcessService(memberDetail);
                    if (excessResult != null && excessResult.isEligible()) {
                        log.info("✅ Excess Service found: {}", excessResult.getTotalExcessAmount());
                        hasExcessService = true;
                        
                        LocalDate excessStartDate = excessResult.getExcessStartDate();
                        log.info("Excess Start Date: {}", excessStartDate);
                        
                        // ✅ CALCULATE OPENING BALANCE FROM ALL CONTRIBUTIONS BEFORE EXCESS PERIOD
                        OpeningBalanceResult openingResult = calculateOpeningBalanceFromAllContributions(
                            allContributions,
                            excessStartDate,
                            rate,
                            yearBasis,
                            asOfDate
                        );
                        
                        openingBalancePensionEc = openingResult.getPensionEc();
                        openingBalancePensionMc = openingResult.getPensionMc();
                        openingBalancePensionIec = openingResult.getInterestPensionEc();
                        openingBalancePensionImc = openingResult.getInterestPensionMc();
                        
                        log.info("=== Opening Balance BEFORE Excess Period ===");
                        log.info("  PEC: {}", openingBalancePensionEc);
                        log.info("  PMC: {}", openingBalancePensionMc);
                        log.info("  PIEC: {}", openingBalancePensionIec);
                        log.info("  PIMC: {}", openingBalancePensionImc);
                        log.info("  Total Principal: {}", openingBalancePensionEc.add(openingBalancePensionMc));
                        log.info("  Total Interest: {}", openingBalancePensionIec.add(openingBalancePensionImc));
                        
                        monthsToExclude = getMonthsToExcludeForExcess(excessResult, asOfDate);
                        log.info("Months to exclude from pension: {}", monthsToExclude.size());
                        
                        // ONLY rebuild if there are months to exclude
                        if (!monthsToExclude.isEmpty()) {
                            rebuildResult = rebuildPensionWithoutExcess(
                                allContributions,
                                monthsToExclude,
                                openingBalancePensionEc,
                                openingBalancePensionMc,
                                openingBalancePensionIec,
                                openingBalancePensionImc,
                                rate,
                                yearBasis,
                                asOfDate
                            );
                            
                            log.info("=== Pension Rebuild Complete ===");
                            log.info("Adjusted PEC: {}", rebuildResult.getAdjustedPensionEc());
                            log.info("Adjusted PMC: {}", rebuildResult.getAdjustedPensionMc());
                            log.info("Adjusted PIEC: {}", rebuildResult.getAdjustedPensionIec());
                            log.info("Adjusted PIMC: {}", rebuildResult.getAdjustedPensionImc());
                            log.info("Excluded Principal: {}", rebuildResult.getExcludedPrincipal());
                            log.info("Excluded Interest: {}", rebuildResult.getExcludedInterest());
                        }
                    } else {
                        log.info("❌ No excess service for this member");
                    }
                } catch (Exception e) {
                    log.error("❌ Error calculating excess service: {}", e.getMessage(), e);
                }
            } else {
                log.info("❌ Excess service not applicable (relieveDate: {}, pensionEligible: {})", 
                        relieveDate, isPensionEligible(memberDetail));
            }

            // If no excess service, try snapshot first, then calculate from contributions
            if (!hasExcessService) {
                boolean pensionSnapshotUsed = false;
                if (previousSnapshot != null) {
                    BigDecimal snapshotPensionTotal = n(previousSnapshot.getPensionEc())
                            .add(n(previousSnapshot.getInterestPension()));
                    
                    if (snapshotPensionTotal.compareTo(BigDecimal.ZERO) > 0) {
                        log.info("Using snapshot for pension opening - PEC: {}, PMC: {}, PIEC: {}, PIMC: {}",
                                previousSnapshot.getPensionEc(), 0, 
                                previousSnapshot.getInterestPension(), previousSnapshot.getInterestPension());
                        
                        openingBalancePensionEc = n(previousSnapshot.getPensionEc());
                        openingBalancePensionMc = n(BigDecimal.valueOf(0.0));
                        openingBalancePensionIec = n(previousSnapshot.getInterestPension());
                        openingBalancePensionImc = n(previousSnapshot.getInterestPension());
                        pensionSnapshotUsed = true;
                    }
                }
                
                // If no snapshot pension data, calculate from contributions
                if (!pensionSnapshotUsed) {
                    log.info("No snapshot pension data - calculating pension opening from contributions before current year");
                    
                    // Calculate pension opening from contributions BEFORE current year
                    LocalDate currentYearStart = LocalDate.of(asOfDate.getYear(), 1, 1);
                    OpeningBalanceResult pensionResult = calculateOpeningBalanceFromAllContributions(
                        allContributions,
                        currentYearStart,
                        rate,
                        yearBasis,
                        asOfDate
                    );
                    
                    openingBalancePensionEc = pensionResult.getPensionEc();
                    openingBalancePensionMc = pensionResult.getPensionMc();
                    openingBalancePensionIec = pensionResult.getInterestPensionEc();
                    openingBalancePensionImc = pensionResult.getInterestPensionMc();
                    
                    log.info("Calculated pension opening from contributions - PEC: {}, PMC: {}, PIEC: {}, PIMC: {}",
                            openingBalancePensionEc, openingBalancePensionMc, 
                            openingBalancePensionIec, openingBalancePensionImc);
                }
            }

            // ================================================================
            // STEP 8: CALCULATE FINAL TOTALS
            // ================================================================
            
            // PF components - ALWAYS from snapshot or calculated
            BigDecimal finalPfEc = openingBalancePfEc.add(currentYearPfEc);
            BigDecimal finalPfMc = openingBalancePfMc.add(currentYearPfMc);
            
            // Pension components - use adjusted values if excess exists
            BigDecimal finalPensionEc;
            BigDecimal finalPensionMc;
            BigDecimal finalInterestPensionEc;
            BigDecimal finalInterestPensionMc;
            
            if (hasExcessService && rebuildResult != null) {
                // Use adjusted values (excess months excluded from opening balance)
                finalPensionEc = rebuildResult.getAdjustedPensionEc();
                finalPensionMc = rebuildResult.getAdjustedPensionMc();
                finalInterestPensionEc = rebuildResult.getAdjustedPensionIec();
                finalInterestPensionMc = rebuildResult.getAdjustedPensionImc();
                log.info("✅ Using ADJUSTED pension values (excess excluded from opening balance)");
            } else if (hasExcessService && rebuildResult == null) {
                // Excess exists but no rebuild needed (no months to exclude)
                finalPensionEc = openingBalancePensionEc.add(currentYearPensionEc);
                finalPensionMc = openingBalancePensionMc.add(currentYearPensionMc);
                finalInterestPensionEc = openingBalancePensionIec.add(interestOnCurrentYearPensionEc);
                finalInterestPensionMc = openingBalancePensionImc.add(interestOnCurrentYearPensionMc);
                log.info("✅ Using pension values (excess exists but no months to exclude)");
            } else {
                // No excess service - use original values
                finalPensionEc = openingBalancePensionEc.add(currentYearPensionEc);
                finalPensionMc = openingBalancePensionMc.add(currentYearPensionMc);
                finalInterestPensionEc = openingBalancePensionIec.add(interestOnCurrentYearPensionEc);
                finalInterestPensionMc = openingBalancePensionImc.add(interestOnCurrentYearPensionMc);
                log.info("✅ Using ORIGINAL pension values (no excess adjustment)");
            }
            
            // GC & VC components - ALWAYS from snapshot or calculated
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
            // STEP 9: BUILD COMPONENT GROUPS
            // ================================================================
            List<MemberContributionSummary.ComponentGroup> componentGroups = new ArrayList<>();

            // PF Components (ALWAYS included)
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
            
            // Pension Components (ALWAYS included, but values may be adjusted)
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
            
            // GC & VC Components (ALWAYS included)
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
            // STEP 10: CALCULATE TOTALS
            // ================================================================
            BigDecimal totalPrincipal = finalPfEc.add(finalPfMc)
                    .add(finalPensionEc).add(finalPensionMc)
                    .add(finalGc).add(finalVc);
                    
            BigDecimal totalInterest = finalInterestPfEc.add(finalInterestPfMc)
                    .add(finalInterestPensionEc).add(finalInterestPensionMc)
                    .add(finalInterestGc).add(finalInterestVc);
                    
            BigDecimal totalBalance = totalPrincipal.add(totalInterest);

            log.info("=== FINAL TOTALS ===");
            log.info("Total Principal: {}", totalPrincipal);
            log.info("Total Interest: {}", totalInterest);
            log.info("Total Balance: {}", totalBalance);
            if (hasExcessService) {
                log.info("⚠️ Note: Pension values have been adjusted to exclude excess service");
            }

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
                    .rate(rate)
                    
                    // ============================================================
                    // PF OPENING BALANCES
                    // ============================================================
                    .openingPfMc(openingBalancePfMc)          // PF Member Contribution
                    .openingPfEc(openingBalancePfEc)          // PF Employer Contribution
                    .openingPfImc(previousInterestPfMc)       // PF Interest on Member Contribution
                    .openingPfIec(previousInterestPfEc)       // PF Interest on Employer Contribution
                    
                    // ============================================================
                    // PENSION OPENING BALANCES
                    // ============================================================
                    .openingPMc(hasExcessService && rebuildResult != null 
                            ? rebuildResult.getAdjustedOpeningPmc() 
                            : openingBalancePensionMc)        // P Member Contribution
                    
                    .openingPEc(hasExcessService && rebuildResult != null 
                            ? rebuildResult.getAdjustedOpeningPec() 
                            : openingBalancePensionEc)        // P Employer Contribution
                    
                    .openingPImc(hasExcessService && rebuildResult != null 
                            ? rebuildResult.getAdjustedOpeningPimc() 
                            : openingBalancePensionImc)       // P Interest on Member Contribution
                    
                    .openingPIec(hasExcessService && rebuildResult != null 
                            ? rebuildResult.getAdjustedOpeningPiec() 
                            : openingBalancePensionIec)       // P Interest on Employer Contribution
                    
                    // Excess Service Result (if available)
                    .excessService(excessResult);

            return builder.build();

        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage(), e);
            throw ClaimException.internalError("Failed to fetch contribution summary: " + e.getMessage());
        }
    }

    // ================================================================
    // CALCULATE PF OPENING BALANCE FROM CONTRIBUTIONS
    // ================================================================

    /**
     * Calculate PF opening balances from all contributions BEFORE current year
     */
    private PfOpeningBalanceResult calculatePfOpeningBalanceFromContributions(
            List<ContributionBifurcationDetail> allContributions,
            int currentYear,
            BigDecimal rate,
            int yearBasis) {
        
        log.info("=== Calculating PF Opening Balance from Contributions ===");
        
        // Filter contributions BEFORE current year
        List<ContributionBifurcationDetail> historicalContribs = allContributions.stream()
            .filter(c -> {
                if (c.getCreatedAt() == null) return false;
                String status = c.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    return false;
                }
                return c.getCreatedAt().toLocalDate().getYear() < currentYear;
            })
            .sorted(Comparator.comparing(c -> c.getCreatedAt().toLocalDate()))
            .collect(Collectors.toList());
        
        log.info("Found {} historical contributions before {}", historicalContribs.size(), currentYear);
        
        // Initialize running balances
        BigDecimal runningPfEc = BigDecimal.ZERO;
        BigDecimal runningPfMc = BigDecimal.ZERO;
        BigDecimal runningPfIec = BigDecimal.ZERO;
        BigDecimal runningPfImc = BigDecimal.ZERO;
        
        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
        
        for (ContributionBifurcationDetail contrib : historicalContribs) {
            LocalDate contribDate = contrib.getCreatedAt().toLocalDate();
            
            // Calculate days from contribution date to year end
            LocalDate yearEnd = getYearEndForDate(contribDate);
            long days = ChronoUnit.DAYS.between(contribDate, yearEnd);
            if (days < 0) days = 0;
            
            BigDecimal daysFactor = BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(yearBasis), 10, RM);
            
            // Get PF contribution amounts
            BigDecimal pfEc = n(contrib.getPfEc());
            BigDecimal pfMc = n(contrib.getPfMc());
            
            // Interest on existing balances
            BigDecimal interestOnRunningPfEc = runningPfEc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningPfMc = runningPfMc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningPfIec = runningPfIec.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningPfImc = runningPfImc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            // Interest on new contributions
            BigDecimal interestOnPfEc = pfEc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnPfMc = pfMc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            // Update running balances
            runningPfEc = runningPfEc.add(interestOnRunningPfEc).add(pfEc);
            runningPfMc = runningPfMc.add(interestOnRunningPfMc).add(pfMc);
            runningPfIec = runningPfIec.add(interestOnRunningPfIec).add(interestOnPfEc);
            runningPfImc = runningPfImc.add(interestOnRunningPfImc).add(interestOnPfMc);
        }
        
        log.info("=== PF Opening Balance Complete ===");
        log.info("  PF_EC: {}, PF_MC: {}, PF_IEC: {}, PF_IMC: {}",
                runningPfEc, runningPfMc, runningPfIec, runningPfImc);
        
        return PfOpeningBalanceResult.builder()
            .pfEc(runningPfEc)
            .pfMc(runningPfMc)
            .interestPfEc(runningPfIec)
            .interestPfMc(runningPfImc)
            .contributionsProcessed(historicalContribs.size())
            .build();
    }

    // ================================================================
    // CALCULATE OPENING BALANCE FROM ALL CONTRIBUTIONS BEFORE EXCESS PERIOD
    // ================================================================

    /**
     * Calculate opening balance from all contributions before the excess period starts.
     * This builds a running balance with compound interest from the beginning.
     */
    private OpeningBalanceResult calculateOpeningBalanceFromAllContributions(
            List<ContributionBifurcationDetail> allContributions,
            LocalDate excessStartDate,
            BigDecimal rate,
            int yearBasis,
            LocalDate asOfDate) {
        
        log.info("=== Calculating Opening Balance from ALL Historical Contributions ===");
        log.info("Excess starts from: {}", excessStartDate);
        
        // Filter contributions BEFORE the excess period
        List<ContributionBifurcationDetail> preExcessContribs = allContributions.stream()
            .filter(c -> {
                if (c.getCreatedAt() == null) return false;
                String status = c.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    return false;
                }
                return c.getCreatedAt().toLocalDate().isBefore(excessStartDate);
            })
            .sorted(Comparator.comparing(c -> c.getCreatedAt().toLocalDate()))
            .collect(Collectors.toList());
        
        log.info("Found {} contributions BEFORE excess period", preExcessContribs.size());
        
        // Initialize running balances (start from ZERO)
        BigDecimal runningPensionEc = BigDecimal.ZERO;
        BigDecimal runningPensionMc = BigDecimal.ZERO;
        BigDecimal runningPensionIec = BigDecimal.ZERO;
        BigDecimal runningPensionImc = BigDecimal.ZERO;
        
        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
        
        // Process each contribution with compound interest
        for (ContributionBifurcationDetail contrib : preExcessContribs) {
            LocalDate contribDate = contrib.getCreatedAt().toLocalDate();
            
            // Get contribution amounts
            BigDecimal pec = n(contrib.getPensionEc());
            BigDecimal pmc = n(contrib.getPensionMc());
            
            // Calculate days from contribution date to excess start date
            long days = ChronoUnit.DAYS.between(contribDate, excessStartDate);
            if (days < 0) days = 0;
            
            BigDecimal daysFactor = BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(yearBasis), 10, RM);
            
            // Calculate interest on existing balances (compound)
            BigDecimal interestOnRunningPec = runningPensionEc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningPmc = runningPensionMc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningIec = runningPensionIec.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnRunningImc = runningPensionImc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            // Interest on new contributions
            BigDecimal interestOnPec = pec.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            BigDecimal interestOnPmc = pmc.multiply(rateFactor).multiply(daysFactor).setScale(2, RM);
            
            // Update running balances (add interest and new contributions)
            runningPensionEc = runningPensionEc.add(interestOnRunningPec).add(pec);
            runningPensionMc = runningPensionMc.add(interestOnRunningPmc).add(pmc);
            runningPensionIec = runningPensionIec.add(interestOnRunningIec).add(interestOnPec);
            runningPensionImc = runningPensionImc.add(interestOnRunningImc).add(interestOnPmc);
            
            if (log.isDebugEnabled()) {
                log.debug("Contrib on {}: PEC={}, PMC={}, Running PEC={}, PMC={}",
                        contribDate, pec, pmc, runningPensionEc, runningPensionMc);
            }
        }
        
        log.info("=== Opening Balance BEFORE Excess Period ===");
        log.info("  PEC: {}", runningPensionEc);
        log.info("  PMC: {}", runningPensionMc);
        log.info("  PIEC: {}", runningPensionIec);
        log.info("  PIMC: {}", runningPensionImc);
        log.info("  Total Principal: {}", runningPensionEc.add(runningPensionMc));
        log.info("  Total Interest: {}", runningPensionIec.add(runningPensionImc));
        
        return OpeningBalanceResult.builder()
            .pensionEc(runningPensionEc)
            .pensionMc(runningPensionMc)
            .interestPensionEc(runningPensionIec)
            .interestPensionMc(runningPensionImc)
            .contributionsProcessed(preExcessContribs.size())
            .build();
    }

    // ================================================================
    // EXCESS SERVICE REBUILD METHODS
    // ================================================================

    /**
     * Get all months that should be excluded from pension calculation
     * ONLY called when excess service exists
     */
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

    /**
     * Rebuild pension components by EXCLUDING excess months from the opening balance.
     * This starts from the opening balance (calculated from all contributions before excess)
     * and subtracts what was in the excess months.
     */
    private PensionRebuildResult rebuildPensionWithoutExcess(
            List<ContributionBifurcationDetail> allContributions,
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
        log.info("Opening balances (BEFORE Excess):");
        log.info("  PEC: {}, PMC: {}, PIEC: {}, PIMC: {}",
                openingPensionEc, openingPensionMc, openingPensionIec, openingPensionImc);
        
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
                .excludedPec(BigDecimal.ZERO)
                .excludedPmc(BigDecimal.ZERO)
                .excludedPiec(BigDecimal.ZERO)
                .excludedPimc(BigDecimal.ZERO)
                .build();
        }
        
        Set<YearMonth> excludeSet = new HashSet<>(monthsToExclude);
        
        // ================================================================
        // 1. Find contributions that are in the excess months (to exclude)
        // ================================================================
        List<ContributionBifurcationDetail> excludedContributions = allContributions.stream()
            .filter(c -> {
                if (c.getCreatedAt() == null) return false;
                String status = c.getPostingStatus();
                if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                    return false;
                }
                YearMonth ym = YearMonth.from(c.getCreatedAt().toLocalDate());
                return excludeSet.contains(ym);
            })
            .collect(Collectors.toList());
        
        log.info("Found {} contributions to exclude (excess months)", excludedContributions.size());
        
        // ================================================================
        // 2. Calculate total principal and interest to exclude
        // ================================================================
        BigDecimal excludedPec = BigDecimal.ZERO;
        BigDecimal excludedPmc = BigDecimal.ZERO;
        BigDecimal excludedPiec = BigDecimal.ZERO;
        BigDecimal excludedPimc = BigDecimal.ZERO;
        
        BigDecimal rateFactor = rate.divide(HUNDRED, 10, RM);
        
        for (ContributionBifurcationDetail excl : excludedContributions) {
            LocalDate date = excl.getCreatedAt().toLocalDate();
            
            // Get contribution amounts
            BigDecimal pec = n(excl.getPensionEc());
            BigDecimal pmc = n(excl.getPensionMc());
            
            // Calculate interest that was earned on these contributions
            // From contribution date to asOfDate
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
        log.info("  Total Excluded Principal: {}", excludedPec.add(excludedPmc));
        log.info("  Total Excluded Interest: {}", excludedPiec.add(excludedPimc));
        
        // ================================================================
        // 3. Calculate adjusted opening balances (opening - excluded)
        // ================================================================
        BigDecimal adjustedOpeningPec = openingPensionEc.subtract(excludedPec);
        BigDecimal adjustedOpeningPmc = openingPensionMc.subtract(excludedPmc);
        BigDecimal adjustedOpeningPiec = openingPensionIec.subtract(excludedPiec);
        BigDecimal adjustedOpeningPimc = openingPensionImc.subtract(excludedPimc);
        
        log.info("Adjusted opening balances (after subtracting excess):");
        log.info("  PEC: {} - {} = {}", openingPensionEc, excludedPec, adjustedOpeningPec);
        log.info("  PMC: {} - {} = {}", openingPensionMc, excludedPmc, adjustedOpeningPmc);
        log.info("  PIEC: {} - {} = {}", openingPensionIec, excludedPiec, adjustedOpeningPiec);
        log.info("  PIMC: {} - {} = {}", openingPensionImc, excludedPimc, adjustedOpeningPimc);
        
        // ================================================================
        // 4. Calculate interest on adjusted opening balances for current year
        // ================================================================
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
        
        log.info("Interest on adjusted opening balances for current year:");
        log.info("  PEC: {}", interestOnAdjustedPec);
        log.info("  PMC: {}", interestOnAdjustedPmc);
        log.info("  PIEC: {}", interestOnAdjustedPiec);
        log.info("  PIMC: {}", interestOnAdjustedPimc);
        
        // ================================================================
        // 5. Calculate current year contributions (excluding excess months)
        // ================================================================
        BigDecimal currentYearPec = BigDecimal.ZERO;
        BigDecimal currentYearPmc = BigDecimal.ZERO;
        BigDecimal currentYearInterestPec = BigDecimal.ZERO;
        BigDecimal currentYearInterestPmc = BigDecimal.ZERO;
        
        for (ContributionBifurcationDetail contrib : allContributions) {
            String status = contrib.getPostingStatus();
            if (status == null || !"POSTED".equals(status.trim().toUpperCase())) {
                continue;
            }
            
            LocalDate date = contrib.getCreatedAt().toLocalDate();
            YearMonth ym = YearMonth.from(date);
            
            // Skip if in excess months
            if (excludeSet.contains(ym)) {
                continue;
            }
            
            // Only process current year contributions
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
        
        log.info("Current year contributions (excess excluded):");
        log.info("  PEC: {}, PMC: {}", currentYearPec, currentYearPmc);
        log.info("  Interest - PIEC: {}, PIMC: {}", currentYearInterestPec, currentYearInterestPmc);
        
        // ================================================================
        // 6. Calculate FINAL adjusted values
        // ================================================================
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
        
        log.info("=== Rebuild Complete ===");
        log.info("Adjusted - PEC: {}, PMC: {}, PIEC: {}, PIMC: {}",
                adjustedPensionEc, adjustedPensionMc, adjustedPensionIec, adjustedPensionImc);
        log.info("Total Excluded Principal: {}, Total Excluded Interest: {}",
                excludedPec.add(excludedPmc), excludedPiec.add(excludedPimc));
        
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
            .excludedPec(excludedPec)
            .excludedPmc(excludedPmc)
            .excludedPiec(excludedPiec)
            .excludedPimc(excludedPimc)
            .build();
    }

    // ================================================================
    // HELPER METHODS
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

    private ArrConfiguration getArrConfiguration(String accountingYear) {
        if (accountingYear == null) {
            return null;
        }

        log.debug("Looking for ARR configuration for year: {}", accountingYear);

        Optional<ArrConfiguration> arrOpt = arrRepo.findByAccountingYear(accountingYear);
        if (arrOpt.isPresent()) {
            log.debug("✅ Using ARR configuration for year: {}", accountingYear);
            return arrOpt.get();
        }

        String yearWithoutDash = accountingYear.replace("-", "");
        List<ArrConfiguration> allArr = arrRepo.findAll();
        for (ArrConfiguration arr : allArr) {
            String arrYear = arr.getAccountingYear().replace("-", "");
            if (arrYear.equals(yearWithoutDash)) {
                log.debug("✅ Using ARR configuration for year (without dash): {}", arr.getAccountingYear());
                return arr;
            }
        }

        log.warn("⚠️ ARR configuration not found for year: {}, trying fallback options", accountingYear);
        
        try {
            int year = Integer.parseInt(accountingYear.split("-")[0]);
            log.debug("Parsed year: {}", year);
            
            for (int i = 1; i <= 5; i++) {
                String previousYearDash = (year - i) + "-" + (year - i);
                log.debug("  Trying previous year: {}", previousYearDash);
                
                Optional<ArrConfiguration> prevArrOpt = arrRepo.findByAccountingYear(previousYearDash);
                if (prevArrOpt.isPresent()) {
                    log.debug("✅ Using ARR configuration from previous year: {} ({} years back)", previousYearDash, i);
                    return prevArrOpt.get();
                }
            }
        } catch (Exception e) {
            log.warn("Could not parse accounting year: {}", accountingYear);
        }

        allArr = arrRepo.findAll();
        if (!allArr.isEmpty()) {
            allArr.sort((a, b) -> b.getAccountingYear().compareTo(a.getAccountingYear()));
            ArrConfiguration latest = allArr.get(0);
            log.debug("✅ Using latest available ARR configuration from year: {}", latest.getAccountingYear());
            return latest;
        }

        log.warn("❌ No ARR configuration found in database, using default values");
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

    private MemberBalanceSnapshot getPreviousYearSnapshot(String cid, String nppfNumber, int currentYear) {
        List<MemberBalanceSnapshot> snapshots = snapshotRepo
                .findByCidAndNppfNumberOrderByAccountingYearDesc(cid, nppfNumber);

        log.debug("========== DEBUG: getPreviousYearSnapshot ==========");
        log.debug("Looking for year < {}", currentYear);
        log.debug("Total snapshots found: {}", snapshots.size());
        
        if (snapshots.isEmpty()) {
            log.debug("❌ No snapshots found!");
            return null;
        }

        for (MemberBalanceSnapshot snapshot : snapshots) {
            log.debug("  Snapshot year: {}, PF_EC: {}", snapshot.getAccountingYear(), snapshot.getPfEc());
            if (snapshot.getAccountingYear() != null) {
                try {
                    int snapshotYear = Integer.parseInt(snapshot.getAccountingYear().trim());
                    if (snapshotYear < currentYear) {
                        log.debug("✅ Found snapshot for year: {}", snapshot.getAccountingYear());
                        return snapshot;
                    }
                } catch (NumberFormatException e) {
                    log.debug("  Could not parse year from: {}", snapshot.getAccountingYear());
                }
            }
        }

        log.debug("❌ No snapshot found for year < {}", currentYear);
        log.debug("=====================================================");
        return null;
    }

    /**
     * Get year end date based on accounting year rules
     */
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
        // Adjusted values after excluding excess
        private BigDecimal adjustedPensionEc;
        private BigDecimal adjustedPensionMc;
        private BigDecimal adjustedPensionIec;
        private BigDecimal adjustedPensionImc;
        
        // Adjusted opening balances
        private BigDecimal adjustedOpeningPec;
        private BigDecimal adjustedOpeningPmc;
        private BigDecimal adjustedOpeningPiec;
        private BigDecimal adjustedOpeningPimc;
        
        // Excluded amounts
        private BigDecimal excludedPrincipal;
        private BigDecimal excludedInterest;
        private BigDecimal excludedPec;
        private BigDecimal excludedPmc;
        private BigDecimal excludedPiec;
        private BigDecimal excludedPimc;
    }
}