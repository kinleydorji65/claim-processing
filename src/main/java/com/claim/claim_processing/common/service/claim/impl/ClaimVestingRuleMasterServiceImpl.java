package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimVestingRuleMaster;
import com.claim.claim_processing.common.entities.claim.VestingRefundType;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.mapper.claim.ClaimVestingRuleMasterMapper;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.claim.ClaimVestingRuleMasterRepository;
import com.claim.claim_processing.common.repository.claim.VestingRefundTypeRepository;
import com.claim.claim_processing.common.repository.common.RuleTypeRepository;
import com.claim.claim_processing.common.service.claim.ClaimVestingRuleMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimVestingRuleMasterServiceImpl
        implements ClaimVestingRuleMasterService {

    private final ClaimVestingRuleMasterRepository repository;
    private final ClaimVestingRuleMasterMapper mapper;
    private final AgencyCategoryRepository categoryRepository;
    private final VestingRefundTypeRepository refundRepository;
    private final RuleTypeRepository ruleTypeRepository;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<ClaimVestingRuleResponseDto> createRule(
            ClaimVestingRuleRequestDto dto) {

        try {

            if (repository.existsByRuleCode(dto.getRuleCode())) {
                throw ClaimException.conflict(
                        "Rule code already exists: " + dto.getRuleCode()
                );
            }

            ClaimVestingRuleMaster entity = mapper.toEntity(dto);

            entity.setCategory(getCategory(dto.getCategoryId()));
            entity.setRefundType(getRefund(dto.getRefundId()));
            entity.setRuleType(getRuleType(dto.getRuleTypeId()));

            ClaimVestingRuleMaster savedEntity = repository.save(entity);

            return ApiResponseDTO.success(
                    "Claim Vesting Rule created successfully",
                    mapper.toResponseDto(savedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error creating Claim Vesting Rule", ex);

            throw ClaimException.internalError(
                    "Failed to create Claim Vesting Rule",
                    ex
            );
        }
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<ClaimVestingRuleResponseDto> updateRule(
            Long id,
            ClaimVestingRuleRequestDto dto) {

        try {

            ClaimVestingRuleMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Claim Vesting Rule",
                                    String.valueOf(id)
                            ));

            mapper.updateEntityFromDto(dto, entity);

            entity.setCategory(getCategory(dto.getCategoryId()));
            entity.setRefundType(getRefund(dto.getRefundId()));
            entity.setRuleType(getRuleType(dto.getRuleTypeId()));

            ClaimVestingRuleMaster updatedEntity =
                    repository.save(entity);

            return ApiResponseDTO.success(
                    "Claim Vesting Rule updated successfully",
                    mapper.toResponseDto(updatedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error updating Claim Vesting Rule", ex);

            throw ClaimException.internalError(
                    "Failed to update Claim Vesting Rule",
                    ex
            );
        }
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<ClaimVestingRuleResponseDto> getById(Long id) {

        try {

            ClaimVestingRuleMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Claim Vesting Rule",
                                    String.valueOf(id)
                            ));

            return ApiResponseDTO.success(
                    "Claim Vesting Rule fetched successfully",
                    mapper.toResponseDto(entity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error fetching Claim Vesting Rule", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Claim Vesting Rule",
                    ex
            );
        }
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public ApiResponseDTO<List<ClaimVestingRuleResponseDto>> getAll() {

        try {

            List<ClaimVestingRuleResponseDto> responseDtos =
                    repository.findAll()
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            if (responseDtos.isEmpty()) {
                throw ClaimException.notFound(
                        "No Claim Vesting Rules found"
                );
            }

            return ApiResponseDTO.success(
                    "Claim Vesting Rules fetched successfully",
                    responseDtos
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error fetching Claim Vesting Rules", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Claim Vesting Rules",
                    ex
            );
        }
    }

    // -----------------------------
    // GET BY CATEGORY
    // -----------------------------
    @Override
    public ApiResponseDTO<List<ClaimVestingRuleResponseDto>> getByCategoryId(
            String categoryId) {

        try {

            List<ClaimVestingRuleResponseDto> responseDtos =
                    repository.findByCategory_CategoryId(categoryId)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            if (responseDtos.isEmpty()) {
                throw ClaimException.notFound(
                        "No Claim Vesting Rules found for category id: "
                                + categoryId
                );
            }

            return ApiResponseDTO.success(
                    "Claim Vesting Rules fetched successfully",
                    responseDtos
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error fetching Claim Vesting Rules by category", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Claim Vesting Rules",
                    ex
            );
        }
    }

    // -----------------------------
    // GET BY REFUND TYPE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<ClaimVestingRuleResponseDto>> getByRefundId(
            Long refundId) {

        try {

            List<ClaimVestingRuleResponseDto> responseDtos =
                    repository.findByRefundType_Id(refundId)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            if (responseDtos.isEmpty()) {
                throw ClaimException.notFound(
                        "No Claim Vesting Rules found for refund id: "
                                + refundId
                );
            }

            return ApiResponseDTO.success(
                    "Claim Vesting Rules fetched successfully",
                    responseDtos
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error fetching Claim Vesting Rules by refund", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Claim Vesting Rules",
                    ex
            );
        }
    }

    // -----------------------------
    // GET BY RULE TYPE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<ClaimVestingRuleResponseDto>> getByRuleTypeId(
            Long ruleTypeId) {

        try {

            List<ClaimVestingRuleResponseDto> responseDtos =
                    repository.findByRuleType_Id(ruleTypeId)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            if (responseDtos.isEmpty()) {
                throw ClaimException.notFound(
                        "No Claim Vesting Rules found for rule type id: "
                                + ruleTypeId
                );
            }

            return ApiResponseDTO.success(
                    "Claim Vesting Rules fetched successfully",
                    responseDtos
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error fetching Claim Vesting Rules by rule type", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Claim Vesting Rules",
                    ex
            );
        }
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> deleteRule(Long id) {

        try {

            ClaimVestingRuleMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Claim Vesting Rule",
                                    String.valueOf(id)
                            ));

            repository.delete(entity);

            return ApiResponseDTO.success(
                    "Claim Vesting Rule deleted successfully"
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error deleting Claim Vesting Rule", ex);

            throw ClaimException.internalError(
                    "Failed to delete Claim Vesting Rule",
                    ex
            );
        }
    }

    // -----------------------------
    // HELPERS
    // -----------------------------
    private AgencyCategory getCategory(String id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Agency Category",
                                id
                        ));
    }

    private VestingRefundType getRefund(Long id) {

        return refundRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Refund Type",
                                String.valueOf(id)
                        ));
    }

    private RuleTypeMaster getRuleType(Long id) {

        return ruleTypeRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Rule Type",
                                String.valueOf(id)
                        ));
    }
}