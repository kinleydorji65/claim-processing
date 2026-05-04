package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleResponseDto;
import com.claim.claim_processing.common.entities.claim.*;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.mapper.claim.ClaimVestingRuleMasterMapper;
import com.claim.claim_processing.common.repository.claim.ClaimVestingRuleMasterRepository;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.claim.ClaimVestingCutoffRepository;
import com.claim.claim_processing.common.repository.claim.VestingRefundTypeRepository;
import com.claim.claim_processing.common.repository.common.RuleTypeRepository;
import com.claim.claim_processing.common.service.claim.ClaimVestingRuleMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimVestingRuleMasterServiceImpl implements ClaimVestingRuleMasterService {

    private final ClaimVestingRuleMasterRepository repository;
    private final AgencyCategoryRepository categoryRepository;
    private final ClaimVestingCutoffRepository cutoffRepository;
    private final VestingRefundTypeRepository refundRepository;
    private final RuleTypeRepository ruleTypeRepository;

    private final ClaimVestingRuleMasterMapper mapper;

    // =========================
    // CREATE
    // =========================
    @Override
    public ClaimVestingRuleResponseDto createRule(ClaimVestingRuleRequestDto dto) {

        repository.findByRuleCode(dto.getRuleCode())
                .ifPresent(r -> {
                    throw ClaimException.conflict("Rule code already exists: " + dto.getRuleCode());
                });

        ClaimVestingRuleMaster entity = mapper.toEntity(dto);

        entity.setCategory(getCategory(dto.getCategoryId()));
        entity.setCutoff(getCutoff(dto.getCutoffId()));
        entity.setRefund(getRefund(dto.getRefundId()));
        entity.setRuleType(getRuleType(dto.getRuleTypeId()));

        return mapper.toDto(repository.save(entity));
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public ClaimVestingRuleResponseDto updateRule(Long id, ClaimVestingRuleRequestDto dto) {

        ClaimVestingRuleMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Rule not found: " + id));

        mapper.updateEntityFromDto(dto, entity);

        if (dto.getCategoryId() != null)
            entity.setCategory(getCategory(dto.getCategoryId()));

        if (dto.getCutoffId() != null)
            entity.setCutoff(getCutoff(dto.getCutoffId()));

        if (dto.getRefundId() != null)
            entity.setRefund(getRefund(dto.getRefundId()));

        if (dto.getRuleTypeId() != null)
            entity.setRuleType(getRuleType(dto.getRuleTypeId()));

        return mapper.toDto(repository.save(entity));
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    @Transactional(readOnly = true)
    public ClaimVestingRuleResponseDto getById(Long id) {
        return mapper.toDto(
                repository.findById(id)
                        .orElseThrow(() -> ClaimException.notFound("Rule not found: " + id))
        );
    }

    // =========================
    // GET ALL
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<ClaimVestingRuleResponseDto> getAll() {
        return mapper.toDto(repository.findAll());
    }

    // =========================
    // FILTERS (CLEAN FK STYLE)
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<ClaimVestingRuleResponseDto> getByCategoryId(String categoryId) {
        return mapper.toDto(repository.findByCategory(getCategoryByCode(categoryId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimVestingRuleResponseDto> getByRefundId(Long refundId) {
        return mapper.toDto(repository.findByRefund(getRefund(refundId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimVestingRuleResponseDto> getByRuleTypeId(Long ruleTypeId) {
        return mapper.toDto(repository.findByRuleType(getRuleType(ruleTypeId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimVestingRuleResponseDto> getByCutoffId(Long cutoffId) {
        return mapper.toDto(repository.findByCutoff(getCutoff(cutoffId)));
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void deleteRule(Long id) {
        ClaimVestingRuleMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Rule not found: " + id));

        repository.delete(entity);
    }

    // =========================
    // FK HELPERS (CLEAN CENTRALIZED)
    // =========================
    private AgencyCategory getCategory(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Category not found: " + id));
    }

    private AgencyCategory getCategoryByCode(String code) {
        return categoryRepository.findByCategoryId(code)
                .orElseThrow(() -> ClaimException.notFound("Category not found: " + code));
    }

    private ClaimVestingCutoffMaster getCutoff(Long id) {
        return cutoffRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Cutoff not found: " + id));
    }

    private VestingRefundType getRefund(Long id) {
        return refundRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Refund not found: " + id));
    }

    private RuleTypeMaster getRuleType(Long id) {
        return ruleTypeRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("RuleType not found: " + id));
    }
}