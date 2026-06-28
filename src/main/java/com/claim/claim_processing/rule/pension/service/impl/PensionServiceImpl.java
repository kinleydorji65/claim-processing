package com.claim.claim_processing.rule.pension.service.impl;

import com.claim.claim_processing.common.entities.pension.PensionDetail;
import com.claim.claim_processing.common.repository.pension.PensionDetailRepository;
import com.claim.claim_processing.rule.pension.dto.PensionDetailRequestDto;
import com.claim.claim_processing.rule.pension.dto.PensionDetailResponseDTO;
import com.claim.claim_processing.rule.pension.service.PensionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensionServiceImpl implements PensionService {

    private final PensionDetailRepository pensionDetailRepository;

    @Override
    @Transactional
    public PensionDetailResponseDTO createPensionDetail(PensionDetailRequestDto requestDto) {
        log.info("Creating pension detail for NPPF: {}", requestDto.getNppfNumber());

        try {
            // Check if pension detail already exists
            PensionDetail existing = pensionDetailRepository
                    .findByNppfNumber(requestDto.getNppfNumber())
                    .orElse(null);

            if (existing != null) {
                log.warn("Pension detail already exists for NPPF: {}. Updating instead.", requestDto.getNppfNumber());
                return updatePensionDetail(existing.getId(), requestDto);
            }

            PensionDetail pensionDetail = mapToEntity(requestDto);
            pensionDetail.setCreatedAt(LocalDateTime.now());
            pensionDetail.setCreatedBy(requestDto.getCreatedBy());

            PensionDetail saved = pensionDetailRepository.save(pensionDetail);
            log.info("Pension detail created successfully with ID: {}", saved.getId());

            return mapToResponseDto(saved);

        } catch (Exception e) {
            log.error("Error creating pension detail: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create pension detail: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PensionDetailResponseDTO updatePensionDetail(Long id, PensionDetailRequestDto requestDto) {
        log.info("Updating pension detail with ID: {}", id);

        try {
            PensionDetail existing = pensionDetailRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pension detail not found with ID: " + id));

            updateEntity(existing, requestDto);
            existing.setUpdatedAt(LocalDateTime.now());
            existing.setUpdatedBy(requestDto.getCreatedBy());

            PensionDetail updated = pensionDetailRepository.save(existing);
            log.info("Pension detail updated successfully with ID: {}", updated.getId());

            return mapToResponseDto(updated);

        } catch (Exception e) {
            log.error("Error updating pension detail: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update pension detail: " + e.getMessage());
        }
    }

    @Override
    public PensionDetailResponseDTO getPensionDetailById(Long id) {
        log.info("Fetching pension detail with ID: {}", id);

        try {
            PensionDetail pensionDetail = pensionDetailRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pension detail not found with ID: " + id));

            return mapToResponseDto(pensionDetail);

        } catch (Exception e) {
            log.error("Error fetching pension detail: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch pension detail: " + e.getMessage());
        }
    }

    @Override
    public PensionDetailResponseDTO getPensionDetailByNppfNumber(String nppfNumber) {
        log.info("Fetching pension detail for NPPF: {}", nppfNumber);

        try {
            PensionDetail pensionDetail = pensionDetailRepository.findByNppfNumber(nppfNumber)
                    .orElseThrow(() -> new RuntimeException("Pension detail not found for NPPF: " + nppfNumber));

            return mapToResponseDto(pensionDetail);

        } catch (Exception e) {
            log.error("Error fetching pension detail by NPPF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch pension detail: " + e.getMessage());
        }
    }

    @Override
    public List<PensionDetailResponseDTO> getAllPensionDetails() {
        log.info("Fetching all pension details");

        try {
            List<PensionDetail> pensionDetails = pensionDetailRepository.findAll();

            return pensionDetails.stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching all pension details: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch pension details: " + e.getMessage());
        }
    }

    

    @Override
    @Transactional
    public PensionDetailResponseDTO createOrUpdatePensionDetail(
            String nppfNumber,
            String memberIdentityNumber,
            String agencyCode,
            String pensionType,
            BigDecimal monthlyPensionAmount,
            BigDecimal totalPensionFund,
            Integer totalContributionMonths,
            Integer totalContributionYears,
            LocalDateTime pensionStartDate,
            String createdBy) {

        log.info("Creating/Updating pension detail for NPPF: {}", nppfNumber);

        try {
            // Check if pension detail already exists
            PensionDetail existing = pensionDetailRepository.findByNppfNumber(nppfNumber).orElse(null);

            PensionDetail pensionDetail;

            if (existing != null) {
                // Update existing
                pensionDetail = existing;
                pensionDetail.setPensionType(pensionType);
                pensionDetail.setMonthlyPensionAmount(monthlyPensionAmount);
                pensionDetail.setTotalPensionFund(totalPensionFund);
                pensionDetail.setTotalContributionMonths(totalContributionMonths);
                pensionDetail.setTotalContributionYears(totalContributionYears);
                if (pensionStartDate != null) {
                    pensionDetail.setPensionStartDate(pensionStartDate.toLocalDate());
                }
                pensionDetail.setUpdatedBy(createdBy);
                pensionDetail.setUpdatedAt(LocalDateTime.now());
                log.info("Updating existing pension detail for NPPF: {}", nppfNumber);
            } else {
                // Create new
                pensionDetail = PensionDetail.builder()
                        .nppfNumber(nppfNumber)
                        .memberIdentityNumber(memberIdentityNumber)
                        .agencyCode(agencyCode)
                        .currencyCode("BTN")
                        .pensionType(pensionType)
                        .pensionCategory("MONTHLY")
                        .monthlyPensionAmount(monthlyPensionAmount)
                        .totalPensionFund(totalPensionFund)
                        .totalContributionMonths(totalContributionMonths)
                        .totalContributionYears(totalContributionYears)
                        .pensionStatus("ACTIVE")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build();

                if (pensionStartDate != null) {
                    pensionDetail.setPensionStartDate(pensionStartDate.toLocalDate());
                }
                log.info("Creating new pension detail for NPPF: {}", nppfNumber);
            }

            PensionDetail saved = pensionDetailRepository.save(pensionDetail);
            log.info("Pension detail saved with ID: {}", saved.getId());

            return mapToResponseDto(saved);

        } catch (Exception e) {
            log.error("Error creating/updating pension detail: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create/update pension detail: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PensionDetailResponseDTO updatePensionStatus(Long id, String status, String updatedBy) {
        log.info("Updating pension status for ID: {} to: {}", id, status);

        try {
            PensionDetail pensionDetail = pensionDetailRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pension detail not found with ID: " + id));

            pensionDetail.setPensionStatus(status);
            pensionDetail.setUpdatedBy(updatedBy);
            pensionDetail.setUpdatedAt(LocalDateTime.now());

            PensionDetail updated = pensionDetailRepository.save(pensionDetail);
            log.info("Pension status updated successfully for ID: {}", updated.getId());

            return mapToResponseDto(updated);

        } catch (Exception e) {
            log.error("Error updating pension status: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update pension status: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PensionDetailResponseDTO updatePensionBankDetails(
            Long id,
            Long bankTypeId,
            String bankName,
            String bankAccountNumber,
            String accountHolderName,
            String ifscCode,
            String updatedBy) {

        log.info("Updating pension bank details for ID: {}", id);

        try {
            PensionDetail pensionDetail = pensionDetailRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pension detail not found with ID: " + id));

            pensionDetail.setBankTypeId(bankTypeId);
            pensionDetail.setBankName(bankName);
            pensionDetail.setBankAccountNumber(bankAccountNumber);
            pensionDetail.setAccountHolderName(accountHolderName);
            pensionDetail.setIfscCode(ifscCode);
            pensionDetail.setUpdatedBy(updatedBy);
            pensionDetail.setUpdatedAt(LocalDateTime.now());

            PensionDetail updated = pensionDetailRepository.save(pensionDetail);
            log.info("Pension bank details updated successfully for ID: {}", updated.getId());

            return mapToResponseDto(updated);

        } catch (Exception e) {
            log.error("Error updating pension bank details: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update pension bank details: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deletePensionDetail(Long id, String deletedBy) {
        log.info("Deleting pension detail with ID: {}", id);

        try {
            PensionDetail pensionDetail = pensionDetailRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pension detail not found with ID: " + id));

            // Soft delete - update status
            pensionDetail.setPensionStatus("DELETED");
            pensionDetail.setUpdatedBy(deletedBy);
            pensionDetail.setUpdatedAt(LocalDateTime.now());

            pensionDetailRepository.save(pensionDetail);
            log.info("Pension detail marked as deleted with ID: {}", id);

        } catch (Exception e) {
            log.error("Error deleting pension detail: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete pension detail: " + e.getMessage());
        }
    }

    // ==================== MAPPING METHODS ====================

    /**
     * Map Request DTO to Entity
     */
    private PensionDetail mapToEntity(PensionDetailRequestDto dto) {
        PensionDetail pensionDetail = PensionDetail.builder()
                .nppfNumber(dto.getNppfNumber())
                .memberIdentityNumber(dto.getMemberIdentityNumber())
                .agencyCode(dto.getAgencyCode())
                .currencyCode(dto.getCurrencyCode() != null ? dto.getCurrencyCode() : "BTN")
                .pensionType(dto.getPensionType())
                .pensionCategory(dto.getPensionCategory())
                .pensionSubCategory(dto.getPensionSubCategory())
                .monthlyPensionAmount(dto.getMonthlyPensionAmount())
                .totalPensionFund(dto.getTotalPensionFund())
                .totalContributionMonths(dto.getTotalContributionMonths())
                .totalContributionYears(dto.getTotalContributionYears())
                .pensionStatus(dto.getPensionStatus() != null ? dto.getPensionStatus() : "ACTIVE")
                .bankTypeId(dto.getBankTypeId())
                .bankName(dto.getBankName())
                .bankAccountNumber(dto.getBankAccountNumber())
                .accountHolderName(dto.getAccountHolderName())
                .ifscCode(dto.getIfscCode())
                .build();

        if (dto.getPensionStartDate() != null) {
            pensionDetail.setPensionStartDate(dto.getPensionStartDate());
        }
        if (dto.getPensionEndDate() != null) {
            pensionDetail.setPensionEndDate(dto.getPensionEndDate());
        }
        if (dto.getRetirementDate() != null) {
            pensionDetail.setRetirementDate(dto.getRetirementDate());
        }

        return pensionDetail;
    }

    /**
     * Update Entity from Request DTO
     */
    private void updateEntity(PensionDetail entity, PensionDetailRequestDto dto) {
        if (dto.getNppfNumber() != null) entity.setNppfNumber(dto.getNppfNumber());
        if (dto.getMemberIdentityNumber() != null) entity.setMemberIdentityNumber(dto.getMemberIdentityNumber());
        if (dto.getAgencyCode() != null) entity.setAgencyCode(dto.getAgencyCode());
        if (dto.getCurrencyCode() != null) entity.setCurrencyCode(dto.getCurrencyCode());
        if (dto.getPensionType() != null) entity.setPensionType(dto.getPensionType());
        if (dto.getPensionCategory() != null) entity.setPensionCategory(dto.getPensionCategory());
        if (dto.getPensionSubCategory() != null) entity.setPensionSubCategory(dto.getPensionSubCategory());
        if (dto.getMonthlyPensionAmount() != null) entity.setMonthlyPensionAmount(dto.getMonthlyPensionAmount());
        if (dto.getTotalPensionFund() != null) entity.setTotalPensionFund(dto.getTotalPensionFund());
        if (dto.getTotalContributionMonths() != null) entity.setTotalContributionMonths(dto.getTotalContributionMonths());
        if (dto.getTotalContributionYears() != null) entity.setTotalContributionYears(dto.getTotalContributionYears());
        if (dto.getPensionStatus() != null) entity.setPensionStatus(dto.getPensionStatus());
        if (dto.getBankTypeId() != null) entity.setBankTypeId(dto.getBankTypeId());
        if (dto.getBankName() != null) entity.setBankName(dto.getBankName());
        if (dto.getBankAccountNumber() != null) entity.setBankAccountNumber(dto.getBankAccountNumber());
        if (dto.getAccountHolderName() != null) entity.setAccountHolderName(dto.getAccountHolderName());
        if (dto.getIfscCode() != null) entity.setIfscCode(dto.getIfscCode());
        if (dto.getPensionStartDate() != null) entity.setPensionStartDate(dto.getPensionStartDate());
        if (dto.getPensionEndDate() != null) entity.setPensionEndDate(dto.getPensionEndDate());
        if (dto.getRetirementDate() != null) entity.setRetirementDate(dto.getRetirementDate());
    }

    /**
     * Map Entity to Response DTO
     */
    private PensionDetailResponseDTO mapToResponseDto(PensionDetail entity) {
        return PensionDetailResponseDTO.builder()
                .pensionDetailId(entity.getId())
                .nppfNumber(entity.getNppfNumber())
                .memberIdentityNumber(entity.getMemberIdentityNumber())
                .agencyCode(entity.getAgencyCode())
                .currencyCode(entity.getCurrencyCode())
                .pensionType(entity.getPensionType())
                .pensionCategory(entity.getPensionCategory())
                .pensionSubCategory(entity.getPensionSubCategory())
                .monthlyPensionAmount(entity.getMonthlyPensionAmount())
                .totalPensionFund(entity.getTotalPensionFund())
                .totalContributionMonths(entity.getTotalContributionMonths())
                .totalContributionYears(entity.getTotalContributionYears())
                .pensionStartDate(entity.getPensionStartDate())
                .pensionEndDate(entity.getPensionEndDate())
                .retirementDate(entity.getRetirementDate())
                .pensionStatus(entity.getPensionStatus())
                .bankTypeId(entity.getBankTypeId())
                .bankName(entity.getBankName())
                .bankAccountNumber(entity.getBankAccountNumber())
                .accountHolderName(entity.getAccountHolderName())
                .ifscCode(entity.getIfscCode())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}