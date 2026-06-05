package com.claim.claim_processing.application.service.detail.impl;

import com.claim.claim_processing.application.DTO.request.detail.PartialWithdrawalRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.PartialWithdrawalResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;
import com.claim.claim_processing.application.mapper.detail.PartialWithdrawalMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PartialWithdrawalServiceImpl implements PartialWithdrawalService {

    private final PartialWithdrawalDetailRepository partialWithdrawalRepository;
    private final ClaimApplicationRepository claimApplicationRepository;
    private final PayeeTypeRepository payeeTypeRepository;
    private final PartialWithdrawalRuleRepository partialWithdrawalRuleRepository;
    private final PartialReasonRepository withdrawalReasonRepository;
    private final PartialWithdrawalCauseRepository withdrawalCauseRepository;
    private final DisasterTypeRepository disasterTypeRepository;
    private final BusinessTypeRepository businessTypeRepository;
    private final PartialWithdrawalMapper partialWithdrawalMapper;

    @Override
    public ApiResponseDTO<PartialWithdrawalResponseDto> create(PartialWithdrawalRequestDto request) {

        validateRequired(request);

        // if (partialWithdrawalRepository.existsByClaimApplication_Id(request.getClaimApplicationId())) {
        //     throw ClaimException.conflict(
        //             "Partial withdrawal detail already exists for claim application id: "
        //                     + request.getClaimApplicationId()
        //     );
        // }

        ClaimApplication claimApplication = null;
        PayeeTypeMaster payeeType = getPayeeType(request.getPayeeTypeId());
        PartialWithdrawalReasonMaster reason = getWithdrawalReason(request.getWithdrawalReasonId());

        validateByWithdrawalReason(request, reason.getCode());

        PartialWithdrawalDetail entity = partialWithdrawalMapper.toEntity(request);

        entity.setClaimApplication(claimApplication);
        entity.setPayeeType(payeeType);
        entity.setWithdrawalReason(reason);
        entity.setDisasterType(getDisasterTypeIfPresent(request.getDisasterTypeId()));
        entity.setBusinessType(getBusinessTypeIfPresent(request.getBusinessTypeId()));

        PartialWithdrawalDetail saved = partialWithdrawalRepository.save(entity);

        return ApiResponseDTO.created(partialWithdrawalMapper.toResponseDto(saved));
    }

    @Override
    public ApiResponseDTO<PartialWithdrawalResponseDto> update(Long id, PartialWithdrawalRequestDto request) {

        PartialWithdrawalDetail existing = partialWithdrawalRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Partial withdrawal detail",
                        id.toString()
                ));

        // if (request.getClaimApplicationId() != null) {
        //     boolean duplicate = partialWithdrawalRepository
        //             .existsByClaimApplication_IdAndIdNot(request.getClaimApplicationId(), id);

        //     if (duplicate) {
        //         throw ClaimException.conflict(
        //                 "Partial withdrawal detail already exists for claim application id: "
        //                         + request.getClaimApplicationId()
        //         );
        //     }
        // }

        // partialWithdrawalMapper.updateEntityFromDto(request, existing);

        // if (request.getClaimApplicationId() != null) {
        //     existing.setClaimApplication(getClaimApplication(request.getClaimApplicationId()));
        // }

        if (request.getPayeeTypeId() != null) {
            existing.setPayeeType(getPayeeType(request.getPayeeTypeId()));
        }

        if (request.getWithdrawalReasonId() != null) {
            PartialWithdrawalReasonMaster reason = getWithdrawalReason(request.getWithdrawalReasonId());
            existing.setWithdrawalReason(reason);
        }

        if (request.getDisasterTypeId() != null) {
            existing.setDisasterType(getDisasterTypeIfPresent(request.getDisasterTypeId()));
        }

        if (request.getBusinessTypeId() != null) {
            existing.setBusinessType(getBusinessTypeIfPresent(request.getBusinessTypeId()));
        }

        validateByWithdrawalReasonForUpdate(existing);

        PartialWithdrawalDetail updated = partialWithdrawalRepository.save(existing);

        return ApiResponseDTO.success(
                "Partial withdrawal detail updated successfully",
                partialWithdrawalMapper.toResponseDto(updated)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<PartialWithdrawalResponseDto> getById(Long id) {

        PartialWithdrawalDetail entity = partialWithdrawalRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Partial withdrawal detail",
                        id.toString()
                ));

        return ApiResponseDTO.success(
                "Partial withdrawal detail fetched successfully",
                partialWithdrawalMapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<PartialWithdrawalResponseDto> getByClaimApplicationId(Long claimApplicationId) {

        PartialWithdrawalDetail entity = partialWithdrawalRepository.findByClaimApplication_Id(claimApplicationId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Partial withdrawal detail for claim application",
                        claimApplicationId.toString()
                ));

        return ApiResponseDTO.success(
                "Partial withdrawal detail fetched successfully",
                partialWithdrawalMapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<PartialWithdrawalResponseDto>> getAll() {

        List<PartialWithdrawalResponseDto> response = partialWithdrawalRepository.findAll()
                .stream()
                .map(partialWithdrawalMapper::toResponseDto)
                .toList();

        if (response.isEmpty()) {
            throw ClaimException.notFound("No partial withdrawal details found");
        }

        return ApiResponseDTO.success(
                "Partial withdrawal details fetched successfully",
                response
        );
    }

    @Override
    public ApiResponseDTO<Void> delete(Long id) {

        PartialWithdrawalDetail existing = partialWithdrawalRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Partial withdrawal detail",
                        id.toString()
                ));

        partialWithdrawalRepository.delete(existing);

        return ApiResponseDTO.success(
                "Partial withdrawal detail deleted successfully",
                null
        );
    }

    private void validateRequired(PartialWithdrawalRequestDto request) {

        // if (request.getClaimApplicationId() == null) {
        //     throw ClaimException.singleValidationError(
        //             "claimApplicationId",
        //             "Claim application id is required"
        //     );
        // }

        if (request.getPayeeTypeId() == null) {
            throw ClaimException.singleValidationError(
                    "payeeTypeId",
                    "Payee type is required"
            );
        }

        if (request.getWithdrawalReasonId() == null) {
            throw ClaimException.singleValidationError(
                    "withdrawalReasonId",
                    "Withdrawal reason is required"
            );
        }

        if (request.getRequestedWithdrawalAmount() == null) {
            throw ClaimException.singleValidationError(
                    "requestedWithdrawalAmount",
                    "Requested withdrawal amount is required"
            );
        }

        if (request.getRequestedWithdrawalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw ClaimException.singleValidationError(
                    "requestedWithdrawalAmount",
                    "Requested withdrawal amount must be greater than zero"
            );
        }
    }

    private void validateByWithdrawalReason(PartialWithdrawalRequestDto request, String reasonCode) {

        if (reasonCode == null || reasonCode.isBlank()) {
            throw ClaimException.singleValidationError(
                    "withdrawalReasonId",
                    "Withdrawal reason code is missing"
            );
        }

        String code = reasonCode.trim().toUpperCase();

        if ("UNEMPLOYMENT".equals(code)) {

            if (request.getUnemploymentStartDate() == null) {
                throw ClaimException.singleValidationError(
                        "unemploymentStartDate",
                        "Unemployment start date is required"
                );
            }

            if (request.getUnemploymentDurationMonths() == null) {
                throw ClaimException.singleValidationError(
                        "unemploymentDurationMonths",
                        "Unemployment duration months is required"
                );
            }
        }

        if ("BUSINESS_INVESTMENT".equals(code)) {
            if (request.getBusinessTypeId() == null) {
                throw ClaimException.singleValidationError(
                        "businessTypeId",
                        "Business type is required"
                );
            }

            if (request.getBusinessName() == null || request.getBusinessName().isBlank()) {
                throw ClaimException.singleValidationError(
                        "businessName",
                        "Business name is required"
                );
            }

            if (request.getProposedInvestmentAmount() == null) {
                throw ClaimException.singleValidationError(
                        "proposedInvestmentAmount",
                        "Proposed investment amount is required"
                );
            }
        }

        if ("HOUSING".equals(code)) {
            if (request.getHousePurchaseType() == null || request.getHousePurchaseType().isBlank()) {
                throw ClaimException.singleValidationError(
                        "housePurchaseType",
                        "House purchase type is required"
                );
            }

            if (request.getPropertyLocation() == null || request.getPropertyLocation().isBlank()) {
                throw ClaimException.singleValidationError(
                        "propertyLocation",
                        "Property location is required"
                );
            }

            if (request.getEstimatedCost() == null) {
                throw ClaimException.singleValidationError(
                        "estimatedCost",
                        "Estimated cost is required"
                );
            }
        }
    }

    private void validateByWithdrawalReasonForUpdate(PartialWithdrawalDetail existing) {

        if (existing.getWithdrawalReason() == null ||
                existing.getWithdrawalReason().getCode() == null ||
                existing.getWithdrawalReason().getCode().isBlank()) {

            throw ClaimException.singleValidationError(
                    "withdrawalReasonId",
                    "Withdrawal reason is required"
            );
        }

        String code = existing.getWithdrawalReason().getCode().trim().toUpperCase();

        if ("UNEMPLOYMENT".equals(code)) {

            if (existing.getUnemploymentStartDate() == null) {
                throw ClaimException.singleValidationError(
                        "unemploymentStartDate",
                        "Unemployment start date is required"
                );
            }

            if (existing.getUnemploymentDurationMonths() == null) {
                throw ClaimException.singleValidationError(
                        "unemploymentDurationMonths",
                        "Unemployment duration months is required"
                );
            }
        }

        if ("BUSINESS_INVESTMENT".equals(code)) {
            if (existing.getBusinessType() == null) {
                throw ClaimException.singleValidationError(
                        "businessTypeId",
                        "Business type is required"
                );
            }

            if (existing.getBusinessName() == null || existing.getBusinessName().isBlank()) {
                throw ClaimException.singleValidationError(
                        "businessName",
                        "Business name is required"
                );
            }

            if (existing.getProposedInvestmentAmount() == null) {
                throw ClaimException.singleValidationError(
                        "proposedInvestmentAmount",
                        "Proposed investment amount is required"
                );
            }
        }

        if ("HOUSING".equals(code)) {
            if (existing.getHousePurchaseType() == null || existing.getHousePurchaseType().isBlank()) {
                throw ClaimException.singleValidationError(
                        "housePurchaseType",
                        "House purchase type is required"
                );
            }

            if (existing.getPropertyLocation() == null || existing.getPropertyLocation().isBlank()) {
                throw ClaimException.singleValidationError(
                        "propertyLocation",
                        "Property location is required"
                );
            }

            if (existing.getEstimatedCost() == null) {
                throw ClaimException.singleValidationError(
                        "estimatedCost",
                        "Estimated cost is required"
                );
            }
        }
    }

    private ClaimApplication getClaimApplication(Long id) {
        return claimApplicationRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Claim application",
                        id.toString()
                ));
    }

    private PayeeTypeMaster getPayeeType(Long id) {
        return payeeTypeRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Payee type",
                        id.toString()
                ));
    }

    private PartialWithdrawalReasonMaster getWithdrawalReason(Long id) {
        return withdrawalReasonRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Withdrawal reason",
                        id.toString()
                ));
    }

    private DisasterTypeMaster getDisasterTypeIfPresent(Long id) {
        if (id == null) {
            return null;
        }

        return disasterTypeRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Disaster type",
                        id.toString()
                ));
    }

    private BusinessTypeMaster getBusinessTypeIfPresent(Long id) {
        if (id == null) {
            return null;
        }

        return businessTypeRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Business type",
                        id.toString()
                ));
    }
}