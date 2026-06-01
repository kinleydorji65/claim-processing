package com.claim.claim_processing.application.service.detail.impl;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiarySettlementDetailRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiarySettlementResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.BeneficiaryClaimantDetail;
import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;
import com.claim.claim_processing.application.mapper.detail.BeneficiarySettlementDetailMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.detail.BeneficiaryClaimantDetailRepository;
import com.claim.claim_processing.application.repository.detail.BeneficiarySettlementDetailRepository;
import com.claim.claim_processing.application.service.detail.BeneficiarySettlementDetailService;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class BeneficiarySettlementDetailServiceImpl
        implements BeneficiarySettlementDetailService {

    private final BeneficiarySettlementDetailRepository repository;
    private final ClaimApplicationRepository claimApplicationRepository;
    private final BeneficiaryClaimantDetailRepository beneficiaryClaimantDetailRepository;
    private final CessationTypeRepository cessationTypeMasterRepository;
    private final BeneficiarySettlementDetailMapper mapper;

    @Override
    public BeneficiarySettlementResponseDto create(
            BeneficiarySettlementDetailRequestDto request
    ) {
        validateCreateRequest(request);

        if (repository.existsByClaimApplication_Id(request.getClaimApplicationId())) {
            throw ClaimException.conflict(
                    "Beneficiary settlement detail already exists for claim application id: "
                            + request.getClaimApplicationId()
            );
        }

        BeneficiarySettlementDetail entity = mapper.toEntity(request);

        applyRequiredForeignKeys(entity, request);
        applyOptionalForeignKeys(entity, request);

        BeneficiarySettlementDetail saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public BeneficiarySettlementResponseDto patch(
            Long id,
            BeneficiarySettlementDetailRequestDto request
    ) {
        if (id == null) {
            throw ClaimException.badRequest("Beneficiary settlement detail id is required");
        }

        if (request == null) {
            throw ClaimException.badRequest("Request body is required");
        }

        BeneficiarySettlementDetail entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Beneficiary settlement detail",
                        String.valueOf(id)
                ));

        validatePatchRequest(request);
        validateClaimApplicationForPatch(id, request.getClaimApplicationId());

        mapper.patchEntity(entity, request);

        applyPatchForeignKeys(entity, request);

        BeneficiarySettlementDetail updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiarySettlementResponseDto getById(Long id) {
        if (id == null) {
            throw ClaimException.badRequest("Beneficiary settlement detail id is required");
        }

        BeneficiarySettlementDetail entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Beneficiary settlement detail",
                        String.valueOf(id)
                ));

        return mapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiarySettlementResponseDto getByClaimApplicationId(
            Long claimApplicationId
    ) {
        if (claimApplicationId == null) {
            throw ClaimException.badRequest("Claim application id is required");
        }

        BeneficiarySettlementDetail entity = repository
                .findByClaimApplication_Id(claimApplicationId)
                .orElseThrow(() -> ClaimException.notFound(
                        "Beneficiary settlement detail not found for claim application id: "
                                + claimApplicationId
                ));

        return mapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiarySettlementResponseDto getByDeceasedMemberCode(
            String deceasedMemberCode
    ) {
        if (isBlank(deceasedMemberCode)) {
            throw ClaimException.badRequest("Deceased member code is required");
        }

        BeneficiarySettlementDetail entity = repository
                .findByDeceasedMemberCode(deceasedMemberCode.trim())
                .orElseThrow(() -> ClaimException.notFound(
                        "Beneficiary settlement detail not found for deceased member code: "
                                + deceasedMemberCode
                ));

        return mapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeneficiarySettlementResponseDto> getAll() {
        List<BeneficiarySettlementDetail> list = repository.findAll();

        if (list.isEmpty()) {
            throw ClaimException.notFound("No beneficiary settlement details found");
        }

        return list.stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw ClaimException.badRequest("Beneficiary settlement detail id is required");
        }

        BeneficiarySettlementDetail entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Beneficiary settlement detail",
                        String.valueOf(id)
                ));

        repository.delete(entity);
    }

    private void validateCreateRequest(BeneficiarySettlementDetailRequestDto request) {
        if (request == null) {
            throw ClaimException.badRequest("Request body is required");
        }

        if (request.getClaimApplicationId() == null) {
            throw ClaimException.singleValidationError(
                    "claimApplicationId",
                    "Claim application id is required"
            );
        }

        if (request.getCessationTypeId() == null) {
            throw ClaimException.singleValidationError(
                    "cessationTypeId",
                    "Cessation type id is required"
            );
        }

        if (isBlank(request.getDeceasedMemberCode())) {
            throw ClaimException.singleValidationError(
                    "deceasedMemberCode",
                    "Deceased member code is required"
            );
        }

        if (isBlank(request.getDeceasedNppfNumber())) {
            throw ClaimException.singleValidationError(
                    "deceasedNppfNumber",
                    "Deceased NPPF number is required"
            );
        }

        if (request.getDateOfDeath() == null) {
            throw ClaimException.singleValidationError(
                    "dateOfDeath",
                    "Date of death is required"
            );
        }

        validateDates(request);
    }

    private void validatePatchRequest(BeneficiarySettlementDetailRequestDto request) {
        validateDates(request);
    }

    private void validateDates(BeneficiarySettlementDetailRequestDto request) {
        LocalDate today = LocalDate.now();

        if (request.getDateOfDeath() != null
                && request.getDateOfDeath().isAfter(today)) {
            throw ClaimException.badRequest("Date of death cannot be in the future");
        }

        if (request.getServiceJoiningDate() != null
                && request.getDateOfDeath() != null
                && request.getServiceJoiningDate().isAfter(request.getDateOfDeath())) {
            throw ClaimException.badRequest(
                    "Service joining date cannot be after date of death"
            );
        }

        if (request.getLastContributionDate() != null
                && request.getDateOfDeath() != null
                && request.getLastContributionDate().isAfter(request.getDateOfDeath())) {
            throw ClaimException.badRequest(
                    "Last contribution date cannot be after date of death"
            );
        }

        if (request.getNonContributionMonths() != null
                && request.getNonContributionMonths() < 0) {
            throw ClaimException.badRequest(
                    "Non contribution months cannot be negative"
            );
        }
    }

    private void validateClaimApplicationForPatch(
            Long currentId,
            Long newClaimApplicationId
    ) {
        if (newClaimApplicationId == null) {
            return;
        }

        repository.findByClaimApplication_Id(newClaimApplicationId)
                .ifPresent(existing -> {
                    if (!Objects.equals(existing.getId(), currentId)) {
                        throw ClaimException.conflict(
                                "Beneficiary settlement detail already exists for claim application id: "
                                        + newClaimApplicationId
                        );
                    }
                });
    }

    private void applyRequiredForeignKeys(
            BeneficiarySettlementDetail entity,
            BeneficiarySettlementDetailRequestDto request
    ) {
        ClaimApplication claimApplication = claimApplicationRepository
                .findById(request.getClaimApplicationId())
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Claim application",
                        String.valueOf(request.getClaimApplicationId())
                ));

        CessationTypeMaster cessationType = cessationTypeMasterRepository
                .findById(request.getCessationTypeId())
                .orElseThrow(() -> ClaimException.resourceNotFound(
                        "Cessation type",
                        String.valueOf(request.getCessationTypeId())
                ));

        entity.setClaimApplication(claimApplication);
        entity.setCessationType(cessationType);
    }

    private void applyOptionalForeignKeys(
            BeneficiarySettlementDetail entity,
            BeneficiarySettlementDetailRequestDto request
    ) {
        if (request.getBeneficiaryClaimantDetailIds() == null
                || request.getBeneficiaryClaimantDetailIds().isEmpty()) {
            return;
        }

        List<BeneficiaryClaimantDetail> claimantDetails =
                beneficiaryClaimantDetailRepository.findAllById(
                        request.getBeneficiaryClaimantDetailIds()
                );

        if (claimantDetails.size()
                != request.getBeneficiaryClaimantDetailIds().size()) {
            throw ClaimException.badRequest(
                    "One or more beneficiary claimant detail ids are invalid"
            );
        }

        entity.setBeneficiaryClaimantDetails(claimantDetails);
    }

    private void applyPatchForeignKeys(
            BeneficiarySettlementDetail entity,
            BeneficiarySettlementDetailRequestDto request
    ) {
        if (request.getClaimApplicationId() != null) {
            ClaimApplication claimApplication = claimApplicationRepository
                    .findById(request.getClaimApplicationId())
                    .orElseThrow(() -> ClaimException.resourceNotFound(
                            "Claim application",
                            String.valueOf(request.getClaimApplicationId())
                    ));

            entity.setClaimApplication(claimApplication);
        }

        if (request.getCessationTypeId() != null) {
            CessationTypeMaster cessationType = cessationTypeMasterRepository
                    .findById(request.getCessationTypeId())
                    .orElseThrow(() -> ClaimException.resourceNotFound(
                            "Cessation type",
                            String.valueOf(request.getCessationTypeId())
                    ));

            entity.setCessationType(cessationType);
        }

        applyOptionalForeignKeys(entity, request);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}