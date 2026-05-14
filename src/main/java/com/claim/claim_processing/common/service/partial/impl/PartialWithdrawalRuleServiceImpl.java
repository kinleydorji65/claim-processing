package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalRuleResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalAccumulationMaster;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalRuleMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.mapper.partial.PartialWithdrawalRuleMapper;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.partial.PartialReasonRepository;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalAccumulationRepository;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalRuleRepository;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalRuleService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialWithdrawalRuleServiceImpl implements PartialWithdrawalRuleService {

    private final PartialWithdrawalRuleRepository repository;
    private final PartialWithdrawalRuleMapper mapper;

    private final AgencyCategoryRepository categoryRepository;
    private final PartialReasonRepository reasonRepository;
    private final PartialWithdrawalAccumulationRepository accumulationRepository;

    // =========================
    // CREATE
    // =========================
    @Override
    @Transactional
    public ApiResponseDTO<PartialWithdrawalRuleResponseDto> create(
            PartialWithdrawalRuleRequestDto dto) {

        PartialWithdrawalRuleMaster entity = mapper.toEntity(dto);

        entity.setCategory(getCategory(dto.getCategoryId()));
        entity.setReason(getReason(dto.getReasonId()));
        entity.setAccumulation(getAccumulation(dto.getAccumulationId()));

        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getUpdatedBy());

        repository.save(entity);

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public ApiResponseDTO<PartialWithdrawalRuleResponseDto> update(
            Long id,
            PartialWithdrawalRuleRequestDto dto) {

        PartialWithdrawalRuleMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Rule not found with id: " + id
                        ));

        mapper.updateEntityFromDto(dto, entity);

        if (dto.getCategoryId() != null) {
            entity.setCategory(getCategory(dto.getCategoryId()));
        }

        if (dto.getReasonId() != null) {
            entity.setReason(getReason(dto.getReasonId()));
        }

        if (dto.getAccumulationId() != null) {
            entity.setAccumulation(getAccumulation(dto.getAccumulationId()));
        }

        entity.setUpdatedBy(dto.getUpdatedBy());

        return ApiResponseDTO.success(
                mapper.toResponseDto(repository.save(entity))
        );
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    public ApiResponseDTO<PartialWithdrawalRuleResponseDto> getById(Long id) {

        PartialWithdrawalRuleMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Rule not found with id: " + id
                        ));

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // =========================
    // GET ALL
    // =========================
    @Override
    public ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> getAll() {

        List<PartialWithdrawalRuleResponseDto> responseDtos =
                repository.findAll()
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        if (responseDtos.isEmpty()) {
            throw ClaimException.notFound(
                    "No Partial Withdrawal Rules found"
            );
        }

        return ApiResponseDTO.success(responseDtos);
    }

    // =========================
    // GET BY CATEGORY
    // =========================
    @Override
    public ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> getByCategory(
            String categoryId) {

        getCategory(categoryId);

        List<PartialWithdrawalRuleResponseDto> responseDtos =
                repository.findByCategory_CategoryId(categoryId)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        if (responseDtos.isEmpty()) {
            throw ClaimException.notFound(
                    "No Partial Withdrawal Rules found for category id: " + categoryId
            );
        }

        return ApiResponseDTO.success(responseDtos);
    }

    // =========================
    // GET BY REASON
    // =========================
    @Override
    public ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> getByReason(
            Long reasonId) {

        getReason(reasonId);

        List<PartialWithdrawalRuleResponseDto> responseDtos =
                repository.findByReason_Id(reasonId)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        if (responseDtos.isEmpty()) {
            throw ClaimException.notFound(
                    "No Partial Withdrawal Rules found for reason id: " + reasonId
            );
        }

        return ApiResponseDTO.success(responseDtos);
    }

    // =========================
    // GET BY ACCUMULATION
    // =========================
    @Override
    public ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> getByAccumulation(
            Long accumulationId) {

        getAccumulation(accumulationId);

        List<PartialWithdrawalRuleResponseDto> responseDtos =
                repository.findByAccumulation_Id(accumulationId)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        if (responseDtos.isEmpty()) {
            throw ClaimException.notFound(
                    "No Partial Withdrawal Rules found for accumulation id: " + accumulationId
            );
        }

        return ApiResponseDTO.success(responseDtos);
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        PartialWithdrawalRuleMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Rule not found with id: " + id
                        ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Partial Withdrawal Rule deleted successfully"
        );
    }

    // =========================
    // HELPERS
    // =========================

    private AgencyCategory getCategory(String categoryId) {

        if (categoryId == null) {
            throw ClaimException.badRequest("Category id is required");
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Agency Category",
                                categoryId
                        ));
    }

    private PartialWithdrawalReasonMaster getReason(Long reasonId) {

        if (reasonId == null) {
            throw ClaimException.badRequest("Reason id is required");
        }

        return reasonRepository.findById(reasonId)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Partial Withdrawal Reason",
                                String.valueOf(reasonId)
                        ));
    }

    private PartialWithdrawalAccumulationMaster getAccumulation(Long accumulationId) {

        if (accumulationId == null) {
            throw ClaimException.badRequest("Accumulation id is required");
        }

        return accumulationRepository.findById(accumulationId)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Partial Withdrawal Accumulation",
                                String.valueOf(accumulationId)
                        ));
    }
}