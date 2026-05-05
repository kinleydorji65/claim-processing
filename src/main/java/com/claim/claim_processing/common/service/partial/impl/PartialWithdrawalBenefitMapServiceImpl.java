package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalBenefitMapRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalBenefitMapResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalBenefitMap;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalAccumulationMaster;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.mapper.partial.PartialWithdrawalBenefitMapMapper;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalBenefitMapRepository;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalAccumulationRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeMasterRepository;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalBenefitMapService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialWithdrawalBenefitMapServiceImpl implements PartialWithdrawalBenefitMapService {

    private final PartialWithdrawalBenefitMapRepository repository;
    private final PartialWithdrawalAccumulationRepository accumulationRepository;
    private final BenefitComponentTypeMasterRepository componentRepository;
    private final PartialWithdrawalBenefitMapMapper mapper;

    // ---------------- CREATE ----------------
    @Override
    public PartialWithdrawalBenefitMapResponseDto create(PartialWithdrawalBenefitMapRequestDto dto) {

        PartialWithdrawalBenefitMap entity = mapper.toEntity(dto);

        entity.setAccumulation(getAccumulation(dto.getAccumulationId()));
        entity.setBenefitComponent(getComponent(dto.getBenefitComponentId()));

        return mapper.toDto(repository.save(entity));
    }

    // ---------------- UPDATE ----------------
    @Override
    public PartialWithdrawalBenefitMapResponseDto update(PartialWithdrawalBenefitMapRequestDto dto) {

        if (dto.getId() == null) {
            throw ClaimException.badRequest("Id is required for update");
        }

        PartialWithdrawalBenefitMap entity =
                repository.findById(dto.getId())
                        .orElseThrow(() ->
                                ClaimException.resourceNotFound(
                                        "PartialWithdrawalBenefitMap",
                                        dto.getId().toString()
                                ));

        mapper.updateEntityFromDto(dto, entity);

        entity.setAccumulation(getAccumulation(dto.getAccumulationId()));
        entity.setBenefitComponent(getComponent(dto.getBenefitComponentId()));

        return mapper.toDto(repository.save(entity));
    }

    // ---------------- GET BY ID ----------------
    @Override
    public PartialWithdrawalBenefitMapResponseDto getById(Long id) {

        return mapper.toDto(
                repository.findById(id)
                        .orElseThrow(() ->
                                ClaimException.resourceNotFound(
                                        "PartialWithdrawalBenefitMap",
                                        id.toString()
                                ))
        );
    }

    // ---------------- GET BY ACCUMULATION ----------------
    @Override
    public List<PartialWithdrawalBenefitMapResponseDto> getByAccumulationId(Long accumulationId) {

        List<PartialWithdrawalBenefitMap> list =
                repository.findByAccumulation_Id(accumulationId);

        if (list.isEmpty()) {
            throw ClaimException.resourceNotFound(
                    "PartialWithdrawalBenefitMap",
                    "accumulationId=" + accumulationId
            );
        }

        return mapper.toDtoList(list);
    }

    // ---------------- GET BY COMPONENT ----------------
    @Override
    public List<PartialWithdrawalBenefitMapResponseDto> getByBenefitComponentId(Long benefitComponentId) {

        List<PartialWithdrawalBenefitMap> list =
                repository.findByBenefitComponent_Id(benefitComponentId);

        if (list.isEmpty()) {
            throw ClaimException.resourceNotFound(
                    "PartialWithdrawalBenefitMap",
                    "benefitComponentId=" + benefitComponentId
            );
        }

        return mapper.toDtoList(list);
    }

    // ---------------- DELETE ----------------
    @Override
    public void delete(Long id) {

        PartialWithdrawalBenefitMap entity =
                repository.findById(id)
                        .orElseThrow(() ->
                                ClaimException.resourceNotFound(
                                        "PartialWithdrawalBenefitMap",
                                        id.toString()
                                ));

        repository.delete(entity);
    }

    // ---------------- HELPERS ----------------
    private PartialWithdrawalAccumulationMaster getAccumulation(Long id) {
        return accumulationRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Accumulation", id.toString()));
    }

    private BenefitComponentTypeMaster getComponent(Long id) {
        return componentRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("BenefitComponent", id.toString()));
    }
}