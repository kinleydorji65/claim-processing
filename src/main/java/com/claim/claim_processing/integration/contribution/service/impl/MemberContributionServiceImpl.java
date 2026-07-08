package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.entity.MemberBalanceSnapshot;
import com.claim.claim_processing.integration.contribution.entity.MemberInterestRecord;
import com.claim.claim_processing.integration.contribution.repository.MemberBalanceSnapshotRepository;
import com.claim.claim_processing.integration.contribution.repository.MemberInterestRecordRepository;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberContributionServiceImpl implements MemberContributionService {

    private final MemberBalanceSnapshotRepository snapshotRepo;
    private final MemberInterestRecordRepository mirRepo;

    @Override
    public MemberContributionSummary getContributionSummary(String nppfNumber, String cid) {
        System.out.println("=== START getContributionSummary ===");
        System.out.println("nppfNumber: " + nppfNumber + ", cid: " + cid);
        
        try {
            // 1. Get all snapshots for this member (all years)
            List<MemberBalanceSnapshot> snapshots = new ArrayList<>();
            
            if (cid == null || cid.isEmpty()) {
                System.out.println("Querying by nppfNumber only: " + nppfNumber);
                snapshots = snapshotRepo.findByNppfNumberOrderByAccountingYearDesc(nppfNumber);
            } else {
                System.out.println("Querying by cid and nppfNumber: " + cid + ", " + nppfNumber);
                snapshots = snapshotRepo.findByCidAndNppfNumberOrderByAccountingYearDesc(cid, nppfNumber);
            }

            System.out.println("Found " + snapshots.size() + " snapshots in database");
            
            // Debug: Print each snapshot
            for (int i = 0; i < snapshots.size(); i++) {
                MemberBalanceSnapshot s = snapshots.get(i);
                System.out.println("Snapshot[" + i + "]: year=" + s.getAccountingYear() + ", pfEc=" + s.getPfEc() + ", pfMc=" + s.getPfMc() + ", pensionEc=" + s.getPensionEc() + ", gc=" + s.getGc() + ", vc=" + s.getVc() + ", totalBalance=" + s.getTotalBalance() + ", totalContributions=" + s.getTotalContributions());
            }

            if (snapshots.isEmpty()) {
                log.warn("No snapshots found for nppfNumber={}, cid={}", nppfNumber, cid);
                throw ClaimException.notFound("No contribution snapshots found for the given member.");
            }

            // 2. Aggregate totals across all years
            BigDecimal totalPfEc = BigDecimal.ZERO;
            BigDecimal totalPfMc = BigDecimal.ZERO;
            BigDecimal totalPensionEc = BigDecimal.ZERO;
            BigDecimal totalGc = BigDecimal.ZERO;
            BigDecimal totalVc = BigDecimal.ZERO;
            BigDecimal totalInterestEc = BigDecimal.ZERO;
            BigDecimal totalInterestMc = BigDecimal.ZERO;
            BigDecimal totalInterestPension = BigDecimal.ZERO;
            BigDecimal totalInterestGc = BigDecimal.ZERO;
            BigDecimal totalInterestVc = BigDecimal.ZERO;
            
            LocalDate earliestDate = null;
            LocalDate latestDate = null;

            for (MemberBalanceSnapshot snapshot : snapshots) {
                log.debug("Processing snapshot: year={}", snapshot.getAccountingYear());
                
                // Sum contributions
                totalPfEc = totalPfEc.add(n(snapshot.getPfEc()));
                totalPfMc = totalPfMc.add(n(snapshot.getPfMc()));
                totalPensionEc = totalPensionEc.add(n(snapshot.getPensionEc()));
                totalGc = totalGc.add(n(snapshot.getGc()));
                totalVc = totalVc.add(n(snapshot.getVc()));
                
                // Sum interest
                totalInterestEc = totalInterestEc.add(n(snapshot.getInterestEc()));
                totalInterestMc = totalInterestMc.add(n(snapshot.getInterestMc()));
                totalInterestPension = totalInterestPension.add(n(snapshot.getInterestPension()));
                totalInterestGc = totalInterestGc.add(n(snapshot.getInterestGc()));
                totalInterestVc = totalInterestVc.add(n(snapshot.getInterestVc()));
                
                // Track date range
                LocalDate snapDate = snapshot.getLastUpdatedAt() != null 
                    ? snapshot.getLastUpdatedAt().toLocalDate() 
                    : LocalDate.now();
                if (earliestDate == null || snapDate.isBefore(earliestDate)) {
                    earliestDate = snapDate;
                }
                if (latestDate == null || snapDate.isAfter(latestDate)) {
                    latestDate = snapDate;
                }
            }
            System.out.println("Aggregated totals:");
            System.out.println("  Total PF EC: " + totalPfEc);
            System.out.println("  Total PF MC: " + totalPfMc);
            System.out.println("  Total Pension EC: " + totalPensionEc);
            System.out.println("  Total GC: " + totalGc);
            System.out.println("  Total VC: " + totalVc);
            System.out.println("  Total Interest EC: " + totalInterestEc);
            System.out.println("  Total Interest MC: " + totalInterestMc);
            System.out.println("  Total Interest Pension: " + totalInterestPension);
            System.out.println("  Total Interest GC: " + totalInterestGc);
            System.out.println("  Total Interest VC: " + totalInterestVc);

            // 3. Calculate totals
            BigDecimal totalPrincipal = totalPfEc.add(totalPfMc)
                    .add(totalPensionEc).add(totalGc).add(totalVc);
            
            BigDecimal totalInterest = totalInterestEc.add(totalInterestMc)
                    .add(totalInterestPension).add(totalInterestGc).add(totalInterestVc);
            
            BigDecimal totalBalance = totalPrincipal.add(totalInterest);

            System.out.println("Final calculated totals:");
            System.out.println("  Total Principal: " + totalPrincipal);
            System.out.println("  Total Interest: " + totalInterest);
            System.out.println("  Total Balance: " + totalBalance);

            // 4. Get all interest records for detailed component breakdown
            List<MemberInterestRecord> interestRecords = new ArrayList<>();
            if (cid == null || cid.isEmpty()) {
                interestRecords = mirRepo.findByNppfNumberOrderByAccountingYearDesc(nppfNumber);
            } else {
                interestRecords = mirRepo.findByCidAndNppfNumberOrderByAccountingYearDesc(cid, nppfNumber);
            }
            
            System.out.println("Found " + interestRecords.size() + " interest records");

            // 5. Build component groups with proper breakdown
            List<MemberContributionSummary.ComponentGroup> componentGroups = 
                    buildComponentGroups(snapshots, interestRecords);

            System.out.println("Built " + componentGroups.size() + " component groups:");
            for (MemberContributionSummary.ComponentGroup group : componentGroups) {
                System.out.println("  " + group.getComponentCode() + ": principal=" + group.getPrincipalAmount() + ", interest=" + group.getInterestAmount() + ", total=" + group.getTotalAmount());
            }

            // 6. Calculate contribution months
            int contributionMonths = calculateContributionMonths(snapshots);
            System.out.println("Contribution months: " + contributionMonths);

            // 7. Build and return the summary
            MemberContributionSummary summary = MemberContributionSummary.builder()
                    .nppfNumber(nppfNumber)
                    .schemeTypeId(getSchemeTypeId(snapshots))
                    .pfJoiningDate(earliestDate)
                    .pensionJoiningDate(earliestDate)
                    .totalContributionMonths(contributionMonths)
                    .totalContributionYears(contributionMonths / 12)
                    .totalNonContributionMonths(0)
                    .contributionStartDate(earliestDate)
                    .contributionEndDate(latestDate)
                    .totalPrincipalAmount(totalPrincipal)
                    .totalInterestAmount(totalInterest)
                    .totalBalance(totalBalance)
                    .componentGroups(componentGroups)
                    .build();

            System.out.println("=== END getContributionSummary - Returning summary with " + 
                summary.getComponentGroups().size() + " component groups ===");
            
            return summary;

        } catch (Exception e) {
            log.error("Error fetching contribution summary for nppfNumber={}, cid={}", 
                    nppfNumber, cid, e);
            throw ClaimException.internalError("Failed to fetch contribution summary: " + e.getMessage());
        }
    }

    // Helper methods for building component groups
    private List<MemberContributionSummary.ComponentGroup> buildComponentGroups(
            List<MemberBalanceSnapshot> snapshots,
            List<MemberInterestRecord> interestRecords) {
        
        log.debug("Building component groups from {} snapshots and {} interest records", 
            snapshots.size(), interestRecords.size());
        
        // Group interest by component code
        Map<String, BigDecimal> interestByComponent = new HashMap<>();
        for (MemberInterestRecord mir : interestRecords) {
            if ("CREDITED".equals(mir.getStatus()) || "ADJUSTED".equals(mir.getStatus())) {
                String code = mir.getComponentCode();
                BigDecimal current = interestByComponent.getOrDefault(code, BigDecimal.ZERO);
                interestByComponent.put(code, current.add(n(mir.getTotalInterest())));
                log.debug("Interest for {}: {}", code, interestByComponent.get(code));
            }
        }

        // Aggregate contributions from snapshots
        Map<String, BigDecimal> contributionByComponent = new HashMap<>();
        for (MemberBalanceSnapshot snap : snapshots) {
            addToMap(contributionByComponent, "IEC", snap.getPfEc());
            addToMap(contributionByComponent, "IMC", snap.getPfMc());
            addToMap(contributionByComponent, "IPC", snap.getPensionEc());
            addToMap(contributionByComponent, "IGC", snap.getGc());
            addToMap(contributionByComponent, "IVC", snap.getVc());
        }

        log.debug("Contributions by component: {}", contributionByComponent);
        log.debug("Interest by component: {}", interestByComponent);

        List<MemberContributionSummary.ComponentGroup> groups = new ArrayList<>();
        
        // Create ComponentGroup for each component type
        // ALWAYS include ALL components, even with zero values
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
        
        MemberContributionSummary.ComponentGroup group = MemberContributionSummary.ComponentGroup.builder()
                .componentCode(code)
                .componentName(name)
                .principalAmount(principal != null ? principal : BigDecimal.ZERO)
                .interestAmount(interest != null ? interest : BigDecimal.ZERO)
                .totalAmount((principal != null ? principal : BigDecimal.ZERO)
                        .add(interest != null ? interest : BigDecimal.ZERO))
                .build();
        
        log.debug("Created component group: {} - principal={}, interest={}, total={}", 
            code, group.getPrincipalAmount(), group.getInterestAmount(), group.getTotalAmount());
        
        return group;
    }

    private void addToMap(Map<String, BigDecimal> map, String key, BigDecimal value) {
        if (value != null) {
            BigDecimal current = map.getOrDefault(key, BigDecimal.ZERO);
            map.put(key, current.add(value));
            log.debug("Added to map: {}={}", key, map.get(key));
        }
    }

    private MemberContributionSummary buildSummaryFromInterestRecords(
            String nppfNumber, List<MemberInterestRecord> interestRecords) {
        
        System.out.println("Building summary from interest records for nppfNumber=" + nppfNumber);
        
        Map<String, BigDecimal> interestByComponent = new HashMap<>();
        for (MemberInterestRecord mir : interestRecords) {
            if ("CREDITED".equals(mir.getStatus()) || "ADJUSTED".equals(mir.getStatus())) {
                String code = mir.getComponentCode();
                BigDecimal current = interestByComponent.getOrDefault(code, BigDecimal.ZERO);
                interestByComponent.put(code, current.add(n(mir.getTotalInterest())));
            }
        }

        List<MemberContributionSummary.ComponentGroup> groups = new ArrayList<>();
        String[][] components = {
            {"IEC", "Employee PF Contribution"},
            {"IMC", "Employer PF Contribution"},
            {"IPC", "Pension Contribution"},
            {"IGC", "Government Contribution"},
            {"IVC", "Voluntary Contribution"}
        };
        
        BigDecimal totalInterest = BigDecimal.ZERO;
        for (String[] comp : components) {
            BigDecimal interest = interestByComponent.getOrDefault(comp[0], BigDecimal.ZERO);
            totalInterest = totalInterest.add(interest);
            groups.add(createComponentGroup(comp[0], comp[1], BigDecimal.ZERO, interest));
        }

        return MemberContributionSummary.builder()
                .nppfNumber(nppfNumber)
                .schemeTypeId(1L)
                .totalContributionMonths(0)
                .totalContributionYears(0)
                .totalNonContributionMonths(0)
                .totalPrincipalAmount(BigDecimal.ZERO)
                .totalInterestAmount(totalInterest)
                .totalBalance(totalInterest)
                .componentGroups(groups)
                .build();
    }

    private int calculateContributionMonths(List<MemberBalanceSnapshot> snapshots) {
        if (snapshots.isEmpty()) return 0;
        
        Set<String> uniqueYears = new HashSet<>();
        for (MemberBalanceSnapshot snap : snapshots) {
            if (snap.getAccountingYear() != null && !snap.getAccountingYear().isEmpty()) {
                uniqueYears.add(snap.getAccountingYear());
                log.debug("Added year: {}", snap.getAccountingYear());
            }
        }
        
        int months = uniqueYears.size() * 12;
        System.out.println("Calculated " + months + " contribution months from " + uniqueYears.size() + " unique years");
        return months;
    }

    private MemberContributionSummary buildEmptySummary(String nppfNumber, String cid) {
        System.out.println("Building empty summary for nppfNumber=" + nppfNumber + ", cid=" + cid);
        
        List<MemberContributionSummary.ComponentGroup> emptyGroups = new ArrayList<>();
        String[][] components = {
            {"IEC", "Employee PF Contribution"},
            {"IMC", "Employer PF Contribution"},
            {"IPC", "Pension Contribution"},
            {"IGC", "Government Contribution"},
            {"IVC", "Voluntary Contribution"}
        };
        
        for (String[] comp : components) {
            emptyGroups.add(createComponentGroup(comp[0], comp[1], BigDecimal.ZERO, BigDecimal.ZERO));
        }

        return MemberContributionSummary.builder()
                .nppfNumber(nppfNumber)
                .schemeTypeId(1L)
                .totalContributionMonths(0)
                .totalContributionYears(0)
                .totalNonContributionMonths(0)
                .totalPrincipalAmount(BigDecimal.ZERO)
                .totalInterestAmount(BigDecimal.ZERO)
                .totalBalance(BigDecimal.ZERO)
                .componentGroups(emptyGroups)
                .build();
    }

    private BigDecimal n(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private Long getSchemeTypeId(List<MemberBalanceSnapshot> snapshots) {
        if (snapshots.isEmpty()) return 1L;
        return 1L; // Default
    }
}