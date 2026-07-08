package com.claim.claim_processing.rule.pension.service;

import java.util.List;

import com.claim.claim_processing.rule.pension.dto.PensionDetailRequestDto;
import com.claim.claim_processing.rule.pension.dto.PensionDetailResponseDTO;

public interface PensionService {
    PensionDetailResponseDTO createPensionDetail(PensionDetailRequestDto requestDto);
    PensionDetailResponseDTO updatePensionDetail(Long id, PensionDetailRequestDto requestDto);
    PensionDetailResponseDTO getPensionDetailById(Long id);
    PensionDetailResponseDTO getPensionDetailByNppfNumber(String nppfNumber);
    List<PensionDetailResponseDTO> getAllPensionDetails();
    PensionDetailResponseDTO createOrUpdatePensionDetail(PensionDetailRequestDto requestDto);

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
