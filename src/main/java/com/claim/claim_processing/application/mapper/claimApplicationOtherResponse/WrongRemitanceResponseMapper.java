package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import com.claim.claim_processing.application.DTO.response.detail.WrongRemitanceResponseDTO;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemitanceResponseDTO.WrongRemittanceCalculationComponentResponseDTO;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemitanceResponseDTO.WrongRemittanceForfeitedResponseDTO;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemitanceResponseDTO.WrongRemittanceRecalculatedMonthResponseDTO;
import com.claim.claim_processing.application.entity.application.WrongRemittanceForfeited;
import com.claim.claim_processing.application.entity.calculation.WrongRemittanceCalculationComponent;
import com.claim.claim_processing.application.entity.calculation.WrongRemittanceRecalculatedMonth;
import com.claim.claim_processing.application.entity.detail.WrongRemitance;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrongRemitanceResponseMapper {

    public WrongRemitanceResponseDTO toResponse(WrongRemitance entity) {
        if (entity == null) {
            return null;
        }

        // Convert selected months string to list
        List<String> selectedMonthsList = null;
        if (entity.getSelectedMonths() != null && !entity.getSelectedMonths().isBlank()) {
            String[] months = entity.getSelectedMonths().split(", ");
            selectedMonthsList = List.of(months);
        }

        return WrongRemitanceResponseDTO.builder()
                .id(entity.getId())
                .applicationNumber(
                        entity.getClaimApplication() != null ? entity.getClaimApplication().getApplicationNumber()
                                : null)
                .nppfNumber(entity.getNppfNumber())
                .memberName(entity.getMemberName())
                .targetYear(entity.getTargetYear())
                // Opening balances
                .openingPfMc(entity.getOpeningPfMc())
                .openingPfEc(entity.getOpeningPfEc())
                .openingPfImc(entity.getOpeningPfImc())
                .openingPfIec(entity.getOpeningPfIec())
                .openingPMc(entity.getOpeningPMc())
                .openingPEc(entity.getOpeningPEc())
                .openingPImc(entity.getOpeningPImc())
                .openingPIec(entity.getOpeningPIec())
                .openingGc(entity.getOpeningGc())
                .openingGic(entity.getOpeningGic())
                .openingVc(entity.getOpeningVc())
                .openingVic(entity.getOpeningVic())
                .openingIvc(entity.getOpeningIvc())
                .openingIgc(entity.getOpeningIgc())
                // Closing balances
                .closingPfMc(entity.getClosingPfMc())
                .closingPfEc(entity.getClosingPfEc())
                .closingPfImc(entity.getClosingPfImc())
                .closingPfIec(entity.getClosingPfIec())
                .closingPMc(entity.getClosingPMc())
                .closingPEc(entity.getClosingPEc())
                .closingPImc(entity.getClosingPImc())
                .closingPIec(entity.getClosingPIec())
                .closingGc(entity.getClosingGc())
                .closingGic(entity.getClosingGic())
                .closingVc(entity.getClosingVc())
                .closingVic(entity.getClosingVic())
                .closingIvc(entity.getClosingIvc())
                .closingIgc(entity.getClosingIgc())
                .totalRecalculatedContributions(entity.getTotalRecalculatedContributions())
                .totalRecalculatedInterest(entity.getTotalRecalculatedInterest())
                .totalRecalculatedAmount(entity.getTotalRecalculatedAmount())
                // Configuration
                .withInterest("Y".equals(entity.getWithInterest()))
                .appliedInterestRate(entity.getAppliedInterestRate())
                .yearBasis(entity.getYearBasis())
                .calculationDate(entity.getCalculationDate())
                // Selected months
                .selectedMonths(selectedMonthsList)
                .selectedMonthCount(entity.getSelectedMonthCount())
                // Status
                .status(entity.getStatus())
                .message(entity.getMessage())
                .yearsProcessed(entity.getYearsProcessed())
                .fromYear(entity.getFromYear())
                .toYear(entity.getToYear())
                // Audit
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toLocalDateTime() : null)
                .wrongRemitanceForfeiteds(mapForfeitedMap(entity.getForfeitedComponents()))
                .components(mapComponents(entity.getCalculationComponents()))
                .build();
    }

    public List<WrongRemitanceResponseDTO> toResponseList(List<WrongRemitance> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    // private List<WrongRemittanceCalculationComponent> calculationComponents = new
    // ArrayList<>();

    // private List<WrongRemittanceForfeited> forfeitedComponents = new
    // ArrayList<>();
    private List<WrongRemittanceForfeitedResponseDTO> mapForfeitedMap(List<WrongRemittanceForfeited> mappers) {
        List<WrongRemittanceForfeitedResponseDTO> responses = mappers
                .stream()
                .map(m -> {
                    return WrongRemittanceForfeitedResponseDTO
                            .builder()
                            .id(m.getId())
                            .wrongRemittanceId(m.getWrongRemitance().getId())
                            .componentCode(m.getComponentCode())
                            .componentName(m.getComponentName())
                            .amount(m.getAmount())
                            .createdBy(m.getCreatedBy())
                            .updatedBy(m.getUpdatedBy())
                            .createdAt(m.getCreatedAt())
                            .updatedAt(m.getUpdatedAt())
                            .build();
                })
                .toList();

        return responses;
    }

    private List<WrongRemittanceCalculationComponentResponseDTO> mapComponents(
            List<WrongRemittanceCalculationComponent> requests) {

        if (requests != null) {
            return null;
        }
        List<WrongRemittanceCalculationComponentResponseDTO> responses = requests
                .stream()
                .map(m -> {
                    return WrongRemittanceCalculationComponentResponseDTO
                            .builder()
                            .id(m.getId())
                            .componentCode(m.getComponentMaster().getCode())
                            .componentName(m.getComponentMaster().getName())
                            .amount(m.getAmount())
                            .createdAt(m.getCreatedAt())
                            .createdBy(m.getCreatedBy())
                            .updatedAt(m.getUpdatedAt())
                            .updatedBy(m.getUpdatedBy())
                            .build();
                })
                .toList();

        return responses;
    }

    public List<WrongRemitanceResponseDTO.WrongRemittanceRecalculatedMonthResponseDTO> mapRecalculateMonths(
        List<WrongRemittanceRecalculatedMonth> requests) {

    if (requests == null || requests.isEmpty()) {
        return new ArrayList<>();
    }
    
    return requests.stream()
        .map(entity -> WrongRemitanceResponseDTO.WrongRemittanceRecalculatedMonthResponseDTO
            .builder()
            .id(entity.getId())
            .wrongRemitanceId(entity.getWrongRemitance() != null ? 
                              entity.getWrongRemitance().getId() : null)
            .month(entity.getMonth())
            .monthName(entity.getMonthName())
            .invoiceDate(entity.getInvoiceDate() != null ? 
                        entity.getInvoiceDate().atStartOfDay() : null)
            .daysForInterest(entity.getDaysForInterest())
            .interestRate(entity.getInterestRate())
            // PF Components
            .pfMc(entity.getPfMc())
            .pfEc(entity.getPfEc())
            .pfImc(entity.getPfImc())
            .pfIec(entity.getPfIec())
            // Pension Components
            .pMc(entity.getPMc())
            .pEc(entity.getPEc())
            .pImc(entity.getPImc())
            .pIec(entity.getPIec())
            // Gratuity Components
            .gc(entity.getGc())
            .gic(entity.getGic())
            // Voluntary Components
            .vc(entity.getVc())
            .vic(entity.getVic())
            // Interest on Voluntary & Gratuity
            .ivc(entity.getIvc())
            .igc(entity.getIgc())
            // Totals
            .totalContribution(entity.getTotalContribution())
            .totalInterest(entity.getTotalInterest())
            .totalAmount(entity.getTotalAmount())
            .status(entity.getStatus())
            // Audit
            .createdAt(entity.getCreatedAt() != null ? 
                      entity.getCreatedAt().toLocalDateTime() : null)
            .createdBy(entity.getCreatedBy())
            .updatedAt(entity.getUpdatedAt() != null ? 
                      entity.getUpdatedAt().toLocalDateTime() : null)
            .updatedBy(entity.getUpdatedBy())
            .build()
        )
        .sorted((a, b) -> {
            if (a.getMonth() == null || b.getMonth() == null) return 0;
            return a.getMonth().compareTo(b.getMonth());
        })
        .collect(Collectors.toList());
}
}
