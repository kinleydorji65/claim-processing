package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalRuleResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalAccumulationMaster;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalRuleMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalAccumulationRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.mapper.partial.PartialWithdrawalRuleMapper;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalRuleRepository;
import com.claim.claim_processing.common.repository.partial.PartialReasonRepository;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialWithdrawalRuleServiceImpl implements PartialWithdrawalRuleService {

    private final PartialWithdrawalRuleRepository ruleRepository;
    private final AgencyCategoryRepository categoryRepository;
    private final PartialReasonRepository reasonRepository;
    private final PartialWithdrawalAccumulationRepository accumulationRepository;
    private final PartialWithdrawalRuleMapper mapper;

    // -----------------------
    // CREATE
    // -----------------------
    @Override
    public PartialWithdrawalRuleResponseDto create(PartialWithdrawalRuleRequestDto dto) {

        validateDuplicate(dto.getCategoryId(), dto.getReasonId());

        PartialWithdrawalRuleMaster entity = mapper.toEntity(dto);

        entity.setCategory(getCategory(dto.getCategoryId()));
        entity.setReason(getReason(dto.getReasonId()));

        return mapper.toResponseDto(ruleRepository.save(entity));
    }

    // -----------------------
    // UPDATE (PATCH)
    // -----------------------
    @Override
    public PartialWithdrawalRuleResponseDto update(Long id, PartialWithdrawalRuleRequestDto dto) {

        PartialWithdrawalRuleMaster entity = ruleRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("PartialWithdrawalRule", id.toString())
                );

        mapper.updateEntityFromDto(dto, entity);

        if (dto.getCategoryId() != null) {
            entity.setCategory(getCategory(dto.getCategoryId()));
        }

        if (dto.getReasonId() != null) {
            entity.setReason(getReason(dto.getReasonId()));
        }

        return mapper.toResponseDto(ruleRepository.save(entity));
    }

    // -----------------------
    // GET BY ID
    // -----------------------
    @Override
    public PartialWithdrawalRuleResponseDto getById(Long id) {
        return ruleRepository.findById(id)
                .map(mapper::toResponseDto)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("PartialWithdrawalRule", id.toString())
                );
    }

    // -----------------------
    // GET ALL
    // -----------------------
    @Override
    public List<PartialWithdrawalRuleResponseDto> getAll() {
        List<PartialWithdrawalRuleMaster> list = ruleRepository.findAll();

        if (list.isEmpty()) {
            throw ClaimException.notFound("No Partial Withdrawal Rules found");
        }

        return list.stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    // -----------------------
    // FILTERS
    // -----------------------
    @Override
    public List<PartialWithdrawalRuleResponseDto> getByCategory(String categoryId) {
        List<PartialWithdrawalRuleMaster> list =
                ruleRepository.findByCategory_CategoryId(categoryId);

        if (list.isEmpty()) {
            throw ClaimException.resourceNotFound("Rules for categoryId", categoryId);
        }

        return list.stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    public List<PartialWithdrawalRuleResponseDto> getByReason(Long reasonId) {
        List<PartialWithdrawalRuleMaster> list =
                ruleRepository.findByReason_Id(reasonId);

        if (list.isEmpty()) {
            throw ClaimException.resourceNotFound("Rules for reasonId", reasonId.toString());
        }

        return list.stream()
                .map(mapper::toResponseDto)
                .toList();
    }


    @Override
    public List<PartialWithdrawalRuleResponseDto> getByAccumulation(Long accumulationId) {
        List<PartialWithdrawalRuleMaster> list =
                ruleRepository.findByAccumulation_Id(accumulationId);

        if (list.isEmpty()) {
            throw ClaimException.resourceNotFound("Rules for accumulationId", accumulationId.toString());
        }

        return list.stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    // -----------------------
    // DELETE
    // -----------------------
    @Override
    public void delete(Long id) {
        if (!ruleRepository.existsById(id)) {
            throw ClaimException.resourceNotFound("PartialWithdrawalRule", id.toString());
        }
        ruleRepository.deleteById(id);
    }

    // -----------------------
    // HELPERS
    // -----------------------
    private AgencyCategory getCategory(String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("AgencyCategory", categoryId)
                );
    }

    private PartialWithdrawalReasonMaster getReason(Long reasonId) {
        return reasonRepository.findById(reasonId)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("PartialWithdrawalReason", reasonId.toString())
                );
    }

    private PartialWithdrawalAccumulationMaster getAccumulation(Long accumulationId) {
        return accumulationRepository.findById(accumulationId)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("PartialWithdrawalAccumulation", accumulationId.toString())
                );
    }

    private void validateDuplicate(String categoryId, Long reasonId) {

        boolean exists = ruleRepository.findByCategory_CategoryId(categoryId)
                .stream()
                .anyMatch(r -> r.getReason().getId().equals(reasonId));

        if (exists) {
            throw ClaimException.conflict(
                    "Rule already exists for categoryId: " + categoryId +
                            " and reasonId: " + reasonId
            );
        }
    }
}