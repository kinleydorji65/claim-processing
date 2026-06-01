package com.claim.claim_processing.application.service.detail.impl;

import com.claim.claim_processing.application.DTO.request.detail.WrongRemittanceDetailRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemittanceResponseDto;
import com.claim.claim_processing.application.entity.detail.WrongRemittanceDetail;
import com.claim.claim_processing.application.mapper.detail.WrongRemittanceDetailMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.detail.WrongRemittanceDetailRepository;
import com.claim.claim_processing.application.service.detail.WrongRemittanceDetailService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.repository.claim.AccountTypeRepository;
import com.claim.claim_processing.common.repository.common.PayeeTypeRepository;
import com.claim.claim_processing.common.repository.contribution.ContributionTypeRepository;
import com.claim.claim_processing.common.repository.wrongRemittance.RemittanceErrorTypeMasterRepository;
import com.claim.claim_processing.common.repository.wrongRemittance.RemittanceReasonRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WrongRemittanceDetailServiceImpl implements WrongRemittanceDetailService {

    private final WrongRemittanceDetailRepository wrongRemittanceDetailRepository;
    private final WrongRemittanceDetailMapper wrongRemittanceDetailMapper;

    private final ClaimApplicationRepository claimApplicationRepository;
    private final RemittanceReasonRepository wrongRemittanceReasonMasterRepository;
    private final ContributionTypeRepository contributionTypeMasterRepository;
    private final AccountTypeRepository accountTypeMasterRepository;
    private final RemittanceErrorTypeMasterRepository wrongRemittanceErrorTypeMasterRepository;
    private final PayeeTypeRepository payeeTypeMasterRepository;

    @Override
    public ApiResponseDTO<WrongRemittanceResponseDto> create(WrongRemittanceDetailRequestDto request) {

        validateCreateRequest(request);

        if (wrongRemittanceDetailRepository.existsByClaimApplication_Id(request.getClaimApplicationId())) {
            throw ClaimException.conflict(
                    "Wrong remittance detail already exists for claim application id: "
                            + request.getClaimApplicationId()
            );
        }

        WrongRemittanceDetail entity = wrongRemittanceDetailMapper.toEntity(request);
        setRelations(entity, request);

        WrongRemittanceDetail savedEntity = wrongRemittanceDetailRepository.save(entity);

        return ApiResponseDTO.created(
                wrongRemittanceDetailMapper.toResponseDto(savedEntity)
        );
    }

    @Override
    public ApiResponseDTO<WrongRemittanceResponseDto> update(
            Long id,
            WrongRemittanceDetailRequestDto request
    ) {

        if (request == null) {
            throw ClaimException.badRequest("Request body is required");
        }

        WrongRemittanceDetail entity = getEntityById(id);

        wrongRemittanceDetailMapper.updateEntityFromDto(request, entity);
        setRelations(entity, request);

        WrongRemittanceDetail updatedEntity = wrongRemittanceDetailRepository.save(entity);

        return ApiResponseDTO.success(
                "Wrong remittance detail updated successfully",
                wrongRemittanceDetailMapper.toResponseDto(updatedEntity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<WrongRemittanceResponseDto> getById(Long id) {

        WrongRemittanceDetail entity = getEntityById(id);

        return ApiResponseDTO.success(
                "Wrong remittance detail fetched successfully",
                wrongRemittanceDetailMapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<WrongRemittanceResponseDto> getByClaimApplicationId(Long claimApplicationId) {

        if (claimApplicationId == null) {
            throw ClaimException.badRequest("Claim application id is required");
        }

        WrongRemittanceDetail entity = wrongRemittanceDetailRepository
                .findByClaimApplication_Id(claimApplicationId)
                .orElseThrow(() -> ClaimException.notFound(
                        "Wrong remittance detail not found for claim application id: "
                                + claimApplicationId
                ));

        return ApiResponseDTO.success(
                "Wrong remittance detail fetched successfully",
                wrongRemittanceDetailMapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<WrongRemittanceResponseDto>> getAll() {

        List<WrongRemittanceResponseDto> response = wrongRemittanceDetailRepository.findAll()
                .stream()
                .map(wrongRemittanceDetailMapper::toResponseDto)
                .toList();

        if (response.isEmpty()) {
            throw ClaimException.notFound("No wrong remittance details found");
        }

        return ApiResponseDTO.success(
                "Wrong remittance details fetched successfully",
                response
        );
    }

    @Override
    public ApiResponseDTO<Void> delete(Long id) {

        WrongRemittanceDetail entity = getEntityById(id);

        wrongRemittanceDetailRepository.delete(entity);

        return ApiResponseDTO.success(
                "Wrong remittance detail deleted successfully",
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<WrongRemittanceResponseDto>> getByAgencyCode(String agencyCode) {

        if (agencyCode == null || agencyCode.trim().isEmpty()) {
            throw ClaimException.badRequest("Agency code is required");
        }

        List<WrongRemittanceResponseDto> response = wrongRemittanceDetailRepository
                .findByAgencyCode(agencyCode)
                .stream()
                .map(wrongRemittanceDetailMapper::toResponseDto)
                .toList();

        if (response.isEmpty()) {
            throw ClaimException.notFound(
                    "No wrong remittance details found for agency code: " + agencyCode
            );
        }

        return ApiResponseDTO.success(
                "Wrong remittance details fetched successfully",
                response
        );
    }

    private WrongRemittanceDetail getEntityById(Long id) {

        if (id == null) {
            throw ClaimException.badRequest("Wrong remittance detail id is required");
        }

        return wrongRemittanceDetailRepository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Wrong remittance detail",
                        String.valueOf(id)
                ));
    }

    private void validateCreateRequest(WrongRemittanceDetailRequestDto request) {

        if (request == null) {
            throw ClaimException.badRequest("Request body is required");
        }

        if (request.getClaimApplicationId() == null) {
            throw ClaimException.badRequest("Claim application id is required");
        }

        if (request.getPayeeTypeId() == null) {
            throw ClaimException.badRequest("Payee type id is required");
        }
    }

    private void setRelations(
            WrongRemittanceDetail entity,
            WrongRemittanceDetailRequestDto request
    ) {

        if (request.getClaimApplicationId() != null) {
            entity.setClaimApplication(
                    claimApplicationRepository.findById(request.getClaimApplicationId())
                            .orElseThrow(() -> ClaimException.resourceNotFound(
                                    "Claim application",
                                    String.valueOf(request.getClaimApplicationId())
                            ))
            );
        }

        if (request.getWrongRemittanceReasonId() != null) {
            entity.setWrongRemittanceReason(
                    wrongRemittanceReasonMasterRepository.findById(request.getWrongRemittanceReasonId())
                            .orElseThrow(() -> ClaimException.resourceNotFound(
                                    "Wrong remittance reason",
                                    String.valueOf(request.getWrongRemittanceReasonId())
                            ))
            );
        }

        if (request.getContributionTypeId() != null) {
            entity.setContributionType(
                    contributionTypeMasterRepository.findById(request.getContributionTypeId())
                            .orElseThrow(() -> ClaimException.resourceNotFound(
                                    "Contribution type",
                                    String.valueOf(request.getContributionTypeId())
                            ))
            );
        }

        if (request.getAffectedAccountTypeId() != null) {
            entity.setAffectedAccountType(
                    accountTypeMasterRepository.findById(request.getAffectedAccountTypeId())
                            .orElseThrow(() -> ClaimException.resourceNotFound(
                                    "Affected account type",
                                    String.valueOf(request.getAffectedAccountTypeId())
                            ))
            );
        }

        if (request.getErrorTypeId() != null) {
            entity.setErrorType(
                    wrongRemittanceErrorTypeMasterRepository.findById(request.getErrorTypeId())
                            .orElseThrow(() -> ClaimException.resourceNotFound(
                                    "Wrong remittance error type",
                                    String.valueOf(request.getErrorTypeId())
                            ))
            );
        }

        if (request.getPayeeTypeId() != null) {
            entity.setPayeeType(
                    payeeTypeMasterRepository.findById(request.getPayeeTypeId())
                            .orElseThrow(() -> ClaimException.resourceNotFound(
                                    "Payee type",
                                    String.valueOf(request.getPayeeTypeId())
                            ))
            );
        }
    }
}