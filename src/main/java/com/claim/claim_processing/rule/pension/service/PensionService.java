package com.claim.claim_processing.rule.pension.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.rule.pension.dto.PensionDetailRequestDto;
import com.claim.claim_processing.rule.pension.dto.PensionDetailResponseDTO;

public interface PensionService {
    PensionDetailResponseDTO createPensionDetail(PensionDetailRequestDto requestDto);
    PensionDetailResponseDTO updatePensionDetail(Long id, PensionDetailRequestDto requestDto);
    PensionDetailResponseDTO getPensionDetailById(Long id);
    PensionDetailResponseDTO getPensionDetailByNppfNumber(String nppfNumber);
    List<PensionDetailResponseDTO> getAllPensionDetails();
    PensionDetailResponseDTO createOrUpdatePensionDetail(
            String nppfNumber,
            String memberIdentityNumber,
            String agencyCode,
            String pensionType,
            BigDecimal monthlyPensionAmount,
            BigDecimal totalPensionFund,
            Integer totalContributionMonths,
            Integer totalContributionYears,
            LocalDateTime pensionStartDate,
            String createdBy);

    PensionDetailResponseDTO updatePensionStatus(Long id, String status, String updatedBy);
    PensionDetailResponseDTO updatePensionBankDetails(
            Long id,
            Long bankTypeId,
            String bankName,
            String bankAccountNumber,
            String accountHolderName,
            String ifscCode,
            String updatedBy);
    void deletePensionDetail(Long id, String deletedBy);
}
