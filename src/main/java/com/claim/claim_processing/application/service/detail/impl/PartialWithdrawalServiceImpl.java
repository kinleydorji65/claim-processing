package com.claim.claim_processing.application.service.detail.impl;

import com.claim.claim_processing.application.DTO.request.detail.PartialWithdrawalRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;
import com.claim.claim_processing.application.mapper.detail.PartialWithdrawalMapper;
import com.claim.claim_processing.application.repository.detail.PartialWithdrawalDetailRepository;
import com.claim.claim_processing.application.service.detail.PartialWithdrawalService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.partial.*;
import com.claim.claim_processing.common.repository.common.PayeeTypeRepository;
import com.claim.claim_processing.common.repository.partial.*;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PartialWithdrawalServiceImpl implements PartialWithdrawalService {

    private final PartialWithdrawalDetailRepository partialWithdrawalRepository;
    private final PayeeTypeRepository payeeTypeRepository;
    private final PartialReasonRepository withdrawalReasonRepository;
    private final UnemploymentCauseMasterRepository unemploymentCauseRepository;
    private final BusinessTypeRepository businessTypeRepository;
    private final PartialWithdrawalMapper partialWithdrawalMapper;

    @Override
    public PartialWithdrawalDetail create(ClaimApplication claimApplication,
            PartialWithdrawalRequestDto request) {

        validateRequired(request);
        PayeeTypeMaster payeeType = getPayeeType(request.getPayeeTypeId());
        PartialWithdrawalReasonMaster reason = getWithdrawalReason(request.getWithdrawalReasonId());

        validateByWithdrawalReason(request, reason.getCode());

        PartialWithdrawalDetail entity = partialWithdrawalMapper.toEntity(request);

        entity.setClaimApplication(claimApplication);
        entity.setPayeeType(payeeType);
        entity.setWithdrawalReason(reason);
        entity.setBusinessType(getBusinessTypeIfPresent(request.getBusinessTypeId()));
        entity.setUnemploymentCauseMaster(getUnemploymentCauseIfPresent(request.getUnemploymentCauseId()));

        PartialWithdrawalDetail saved = partialWithdrawalRepository.save(entity);

        return saved;
    }

    @Override
    @Transactional
    public PartialWithdrawalDetail update(PartialWithdrawalRequestDto request) {

        PartialWithdrawalDetail existing = partialWithdrawalRepository.findById(request.getPartialWithdrawalId())
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Partial withdrawal detail",
                        request.getPartialWithdrawalId().toString()));

        partialWithdrawalMapper.updateEntityFromDto(request, existing);

        if (request.getPayeeTypeId() != null) {
            existing.setPayeeType(getPayeeType(request.getPayeeTypeId()));
        }

        if (request.getWithdrawalReasonId() != null) {
            PartialWithdrawalReasonMaster reason = getWithdrawalReason(request.getWithdrawalReasonId());

            existing.setWithdrawalReason(reason);
        }

        if (request.getBusinessTypeId() != null || request.getBusinessTypeId() > 0) {
            existing.setBusinessType(
                    getBusinessTypeIfPresent(request.getBusinessTypeId()));
        }

        if (request.getUnemploymentCauseId() != null || request.getUnemploymentCauseId() > 0) {
            existing.setUnemploymentCauseMaster(
                    getUnemploymentCauseIfPresent(request.getUnemploymentCauseId()));
        }

        if (request.getUpdatedBy() != null) {
            existing.setUpdatedBy(request.getUpdatedBy());
        }

        validateByWithdrawalReasonForUpdate(existing);

        PartialWithdrawalDetail updated = partialWithdrawalRepository.save(existing);

        return updated;
    }

    @Override
    public ApiResponseDTO<Void> delete(Long id) {

        PartialWithdrawalDetail existing = partialWithdrawalRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Partial withdrawal detail",
                        id.toString()));

        partialWithdrawalRepository.delete(existing);

        return ApiResponseDTO.success(
                "Partial withdrawal detail deleted successfully",
                null);
    }

    private UnemploymentCauseMaster getUnemploymentCauseIfPresent(Long id) {
        if (id == null) {
            return null;
        }

        return unemploymentCauseRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Unemployment cause",
                        id.toString()));
    }

    private void validateRequired(PartialWithdrawalRequestDto request) {

        if (request.getPayeeTypeId() == null) {
            throw ClaimException.singleValidationError(
                    "payeeTypeId",
                    "Payee type is required");
        }

        if (request.getWithdrawalReasonId() == null) {
            throw ClaimException.singleValidationError(
                    "withdrawalReasonId",
                    "Withdrawal reason is required");
        }

        if (request.getRequestedWithdrawalAmount() == null) {
            throw ClaimException.singleValidationError(
                    "requestedWithdrawalAmount",
                    "Requested withdrawal amount is required");
        }

        if (request.getRequestedWithdrawalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw ClaimException.singleValidationError(
                    "requestedWithdrawalAmount",
                    "Requested withdrawal amount must be greater than zero");
        }
    }

    private void validateByWithdrawalReason(PartialWithdrawalRequestDto request, String reasonCode) {

        if (reasonCode == null || reasonCode.isBlank()) {
            throw ClaimException.singleValidationError(
                    "withdrawalReasonId",
                    "Withdrawal reason code is missing");
        }

        String code = reasonCode.trim().toUpperCase();

        if ("UNEMPLOYMENT".equals(code)) {

            if (request.getUnemploymentStartDate() == null) {
                throw ClaimException.singleValidationError(
                        "unemploymentStartDate",
                        "Unemployment start date is required");
            }

            if (request.getUnemploymentCauseId() == null) {
                throw ClaimException.singleValidationError(
                        "unemploymentCauseId",
                        "Unemployment cause is required");
            }
        }

        if ("BUSINESS_INVESTMENT".equals(code)) {
            if (request.getBusinessTypeId() == null) {
                throw ClaimException.singleValidationError(
                        "businessTypeId",
                        "Business type is required");
            }

            if (request.getBusinessName() == null || request.getBusinessName().isBlank()) {
                throw ClaimException.singleValidationError(
                        "businessName",
                        "Business name is required");
            }

            if (request.getProposedInvestmentAmount() == null) {
                throw ClaimException.singleValidationError(
                        "proposedInvestmentAmount",
                        "Proposed investment amount is required");
            }
        }

        if ("HOUSING".equals(code)) {
            if (request.getHousePurchaseType() == null || request.getHousePurchaseType().isBlank()) {
                throw ClaimException.singleValidationError(
                        "housePurchaseType",
                        "House purchase type is required");
            }

            if (request.getPropertyLocation() == null || request.getPropertyLocation().isBlank()) {
                throw ClaimException.singleValidationError(
                        "propertyLocation",
                        "Property location is required");
            }

            if (request.getEstimatedCost() == null) {
                throw ClaimException.singleValidationError(
                        "estimatedCost",
                        "Estimated cost is required");
            }
        }
    }

    private void validateByWithdrawalReasonForUpdate(PartialWithdrawalDetail existing) {

        if (existing.getWithdrawalReason() == null ||
                existing.getWithdrawalReason().getCode() == null ||
                existing.getWithdrawalReason().getCode().isBlank()) {

            throw ClaimException.singleValidationError(
                    "withdrawalReasonId",
                    "Withdrawal reason is required");
        }

        String code = existing.getWithdrawalReason().getCode().trim().toUpperCase();

        if ("UNEMPLOYMENT".equals(code)) {

            if (existing.getUnemploymentStartDate() == null) {
                throw ClaimException.singleValidationError(
                        "unemploymentStartDate",
                        "Unemployment start date is required");
            }
        }

        if ("BUSINESS_INVESTMENT".equals(code)) {
            if (existing.getBusinessType() == null) {
                throw ClaimException.singleValidationError(
                        "businessTypeId",
                        "Business type is required");
            }

            if (existing.getBusinessName() == null || existing.getBusinessName().isBlank()) {
                throw ClaimException.singleValidationError(
                        "businessName",
                        "Business name is required");
            }

            if (existing.getProposedInvestmentAmount() == null) {
                throw ClaimException.singleValidationError(
                        "proposedInvestmentAmount",
                        "Proposed investment amount is required");
            }
        }

        if ("HOUSING".equals(code)) {
            if (existing.getHousePurchaseType() == null || existing.getHousePurchaseType().isBlank()) {
                throw ClaimException.singleValidationError(
                        "housePurchaseType",
                        "House purchase type is required");
            }

            if (existing.getPropertyLocation() == null || existing.getPropertyLocation().isBlank()) {
                throw ClaimException.singleValidationError(
                        "propertyLocation",
                        "Property location is required");
            }

            if (existing.getEstimatedCost() == null) {
                throw ClaimException.singleValidationError(
                        "estimatedCost",
                        "Estimated cost is required");
            }
        }
    }

    private PayeeTypeMaster getPayeeType(Long id) {
        return payeeTypeRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Payee type",
                        id.toString()));
    }

    private PartialWithdrawalReasonMaster getWithdrawalReason(Long id) {
        return withdrawalReasonRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Withdrawal reason",
                        id.toString()));
    }

    private BusinessTypeMaster getBusinessTypeIfPresent(Long id) {
        if (id == null) {
            return null;
        }

        return businessTypeRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Business type",
                        id.toString()));
    }
}