package com.claim.claim_processing.application.service.detail.impl;

import com.claim.claim_processing.application.DTO.request.detail.LegalRecoveryRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.LegalRecoveryResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;
import com.claim.claim_processing.application.mapper.detail.LegalRecoveryMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.detail.LegalRecoveryDetailRepository;
import com.claim.claim_processing.application.service.detail.LegalRecoveryService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.adjustmentMaster.LoanStatusMaster;
import com.claim.claim_processing.common.entities.adjustmentMaster.LoanTypeMaster;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.entities.legalMaster.RecoveryReasonMaster;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.adjustmentMaster.LoanStatusRepository;
import com.claim.claim_processing.common.repository.adjustmentMaster.LoanTypeRepository;
import com.claim.claim_processing.common.repository.common.PayeeTypeRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.repository.legalMaster.RecoveryReasonRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LegalRecoveryServiceImpl implements LegalRecoveryService {

    private final LegalRecoveryDetailRepository legalRecoveryRepository;
    private final ClaimApplicationRepository claimApplicationRepository;
    private final RecoveryReasonRepository recoveryReasonRepository;
    private final PayeeTypeRepository payeeTypeRepository;
    private final SchemeTypeRepository schemeMasterRepository;
    private final StatusMasterRepository statusMasterRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanStatusRepository loanStatusRepository;
    private final LegalRecoveryMapper legalRecoveryMapper;

    @Override
    public ApiResponseDTO<LegalRecoveryResponseDto> create(LegalRecoveryRequestDto request) {

        validateRequired(request);

        if (legalRecoveryRepository.existsByClaimApplication_Id(request.getClaimApplicationId())) {
            throw ClaimException.conflict(
                    "Legal recovery detail already exists for claim application id: "
                            + request.getClaimApplicationId()
            );
        }

        LegalRecoveryDetail entity = legalRecoveryMapper.toEntity(request);

        entity.setClaimApplication(getClaimApplication(request.getClaimApplicationId()));
        entity.setRecoveryReason(getRecoveryReasonIfPresent(request.getRecoveryReasonId()));
        entity.setPayeeType(getPayeeType(request.getPayeeTypeId()));
        entity.setSchemeType(getSchemeTypeIfPresent(request.getSchemeTypeId()));
        entity.setCurrentStatus(getCurrentStatusIfPresent(request.getCurrentStatusId()));
        entity.setLoanType(getLoanTypeIfPresent(request.getLoanTypeId()));
        entity.setLoanStatus(getLoanStatusIfPresent(request.getLoanStatusId()));

        LegalRecoveryDetail saved = legalRecoveryRepository.save(entity);

        return ApiResponseDTO.created(
                legalRecoveryMapper.toResponseDto(saved)
        );
    }

    @Override
    public ApiResponseDTO<LegalRecoveryResponseDto> update(Long id, LegalRecoveryRequestDto request) {

        LegalRecoveryDetail existing = legalRecoveryRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Legal recovery detail",
                        id.toString()
                ));

        if (request.getClaimApplicationId() != null) {
            boolean duplicate = legalRecoveryRepository
                    .existsByClaimApplication_IdAndIdNot(request.getClaimApplicationId(), id);

            if (duplicate) {
                throw ClaimException.conflict(
                        "Legal recovery detail already exists for claim application id: "
                                + request.getClaimApplicationId()
                );
            }
        }

        legalRecoveryMapper.updateEntityFromDto(request, existing);

        if (request.getClaimApplicationId() != null) {
            existing.setClaimApplication(getClaimApplication(request.getClaimApplicationId()));
        }

        if (request.getRecoveryReasonId() != null) {
            existing.setRecoveryReason(getRecoveryReasonIfPresent(request.getRecoveryReasonId()));
        }

        if (request.getPayeeTypeId() != null) {
            existing.setPayeeType(getPayeeType(request.getPayeeTypeId()));
        }

        if (request.getSchemeTypeId() != null) {
            existing.setSchemeType(getSchemeTypeIfPresent(request.getSchemeTypeId()));
        }

        if (request.getCurrentStatusId() != null) {
            existing.setCurrentStatus(getCurrentStatusIfPresent(request.getCurrentStatusId()));
        }

        if (request.getLoanTypeId() != null) {
            existing.setLoanType(getLoanTypeIfPresent(request.getLoanTypeId()));
        }

        if (request.getLoanStatusId() != null) {
            existing.setLoanStatus(getLoanStatusIfPresent(request.getLoanStatusId()));
        }

        LegalRecoveryDetail updated = legalRecoveryRepository.save(existing);

        return ApiResponseDTO.success(
                "Legal recovery detail updated successfully",
                legalRecoveryMapper.toResponseDto(updated)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<LegalRecoveryResponseDto> getById(Long id) {

        LegalRecoveryDetail entity = legalRecoveryRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Legal recovery detail",
                        id.toString()
                ));

        return ApiResponseDTO.success(
                "Legal recovery detail fetched successfully",
                legalRecoveryMapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<LegalRecoveryResponseDto> getByClaimApplicationId(Long claimApplicationId) {

        LegalRecoveryDetail entity = legalRecoveryRepository.findByClaimApplication_Id(claimApplicationId)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Legal recovery detail for claim application",
                        claimApplicationId.toString()
                ));

        return ApiResponseDTO.success(
                "Legal recovery detail fetched successfully",
                legalRecoveryMapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<LegalRecoveryResponseDto>> getAll() {

        List<LegalRecoveryResponseDto> response = legalRecoveryRepository.findAll()
                .stream()
                .map(legalRecoveryMapper::toResponseDto)
                .toList();

        if (response.isEmpty()) {
            throw ClaimException.notFound("No legal recovery details found");
        }

        return ApiResponseDTO.success(
                "Legal recovery details fetched successfully",
                response
        );
    }

    @Override
    public ApiResponseDTO<Void> delete(Long id) {

        LegalRecoveryDetail existing = legalRecoveryRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Legal recovery detail",
                        id.toString()
                ));

        legalRecoveryRepository.delete(existing);

        return ApiResponseDTO.success(
                "Legal recovery detail deleted successfully",
                null
        );
    }

    private void validateRequired(LegalRecoveryRequestDto request) {

        if (request.getClaimApplicationId() == null) {
            throw ClaimException.singleValidationError(
                    "claimApplicationId",
                    "Claim application id is required"
            );
        }

        if (request.getPayeeTypeId() == null) {
            throw ClaimException.singleValidationError(
                    "payeeTypeId",
                    "Payee type is required"
            );
        }

        if (request.getRecoveryRequestedAmount() == null) {
            throw ClaimException.singleValidationError(
                    "recoveryRequestedAmount",
                    "Recovery requested amount is required"
            );
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

    private RecoveryReasonMaster getRecoveryReasonIfPresent(Long id) {
        if (id == null) {
            return null;
        }

        return recoveryReasonRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Recovery reason",
                        id.toString()
                ));
    }

    private SchemeMaster getSchemeTypeIfPresent(Long id) {
        if (id == null) {
            return null;
        }

        return schemeMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Scheme type",
                        id.toString()
                ));
    }

    private StatusMaster getCurrentStatusIfPresent(Long id) {
        if (id == null) {
            return null;
        }

        return statusMasterRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Current status",
                        id.toString()
                ));
    }

    private LoanTypeMaster getLoanTypeIfPresent(Long id) {
        if (id == null) {
            return null;
        }

        return loanTypeRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Loan type",
                        id.toString()
                ));
    }

    private LoanStatusMaster getLoanStatusIfPresent(Long id) {
        if (id == null) {
            return null;
        }

        return loanStatusRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Loan status",
                        id.toString()
                ));
    }
}