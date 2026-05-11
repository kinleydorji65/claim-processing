package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.VestingRefundTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.VestingRefundTypeResponseDto;
import com.claim.claim_processing.common.entities.claim.VestingRefundBenefitMap;
import com.claim.claim_processing.common.entities.claim.VestingRefundType;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.mapper.claim.VestingRefundTypeMapper;
import com.claim.claim_processing.common.repository.claim.VestingRefundBenefitMapRepository;
import com.claim.claim_processing.common.repository.claim.VestingRefundTypeRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeMasterRepository;
import com.claim.claim_processing.common.service.claim.VestingRefundTypeService;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;

import org.hibernate.mapping.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VestingRefundTypeServiceImpl implements VestingRefundTypeService {

    private final VestingRefundTypeRepository repository;
    private final VestingRefundTypeMapper mapper;
    private final VestingRefundBenefitMapRepository benefitMapRepository;
    private final BenefitComponentTypeMasterRepository benefitComponentTypeMasterRepository;

    @Override
    public VestingRefundTypeResponseDto create(VestingRefundTypeRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "VestingRefundType already exists with code: " + requestDto.getCode()
            );
        }

        VestingRefundType entity = mapper.toEntity(requestDto);
        repository.save(entity);
        List<BenefitComponentTypeMaster> benefitComponentTypeMasters = mapBenefitComponents(entity, requestDto.getBenefitComponentIds());
        return mapper.toDto(entity, benefitComponentTypeMasters);
    }

    private List<BenefitComponentTypeMaster> mapBenefitComponents(VestingRefundType entity, List<Long> componentIds) {
    return componentIds.stream().map(componentId -> {
        VestingRefundBenefitMap benefitComponent = benefitMapRepository
            .findByVestingRefundType_IdAndBenefitComponentType_Id(entity.getId(), componentId)
            .orElse(null);
            
        BenefitComponentTypeMaster componentMaster;
        
        if (benefitComponent == null) {
            componentMaster = getBenefitComponent(componentId);
            benefitComponent = VestingRefundBenefitMap.builder()
                    .vestingRefundType(entity)
                    .benefitComponentType(componentMaster)
                    .build();
            benefitMapRepository.save(benefitComponent);
        } else {
            componentMaster = getBenefitComponent(componentId);
            benefitComponent.setBenefitComponentType(componentMaster);
            benefitMapRepository.save(benefitComponent);
        }
        
        return componentMaster;  // ← This was missing!
    }).toList();
}

    private BenefitComponentTypeMaster getBenefitComponent(Long componentId){
        return benefitComponentTypeMasterRepository.findById(componentId)
                .orElseThrow(() -> ClaimException.notFound("BenefitComponentTypeMaster not found: " + componentId));
    }

    @Override
    public VestingRefundTypeResponseDto update(Long id, VestingRefundTypeRequestDto requestDto) {

        VestingRefundType existing = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("VestingRefundType not found: " + id)
                );

        if (requestDto.getCode() != null
                && !requestDto.getCode().equals(existing.getCode())
                && repository.existsByCode(requestDto.getCode())) {

            throw ClaimException.conflict(
                    "VestingRefundType already exists with code: " + requestDto.getCode()
            );
        }

        mapper.updateEntityFromDto(requestDto, existing);
        VestingRefundType entity = repository.save(existing);
        List<BenefitComponentTypeMaster> benefitComponentTypeMasters = mapBenefitComponents(entity, requestDto.getBenefitComponentIds());
        return mapper.toDto(entity, benefitComponentTypeMasters);
    }

    @Override
    public VestingRefundTypeResponseDto getById(Long id) {

        VestingRefundType entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("VestingRefundType not found: " + id)
                );
        List<BenefitComponentTypeMaster> benefitComponentTypeMasters = benefitMapRepository.findByVestingRefundType_Id(entity.getId())
                .stream()
                .map(VestingRefundBenefitMap::getBenefitComponentType)
                .toList();
        return mapper.toDto(entity, benefitComponentTypeMasters);
    }

    @Override
    public List<VestingRefundTypeResponseDto> getAll() {

        return repository.findAll()
                .stream()
                .map(entity -> {
                    List<BenefitComponentTypeMaster> benefitComponentTypeMasters = benefitMapRepository.findByVestingRefundType_Id(entity.getId())
                            .stream()
                            .map(VestingRefundBenefitMap::getBenefitComponentType)
                            .toList();
                    return mapper.toDto(entity, benefitComponentTypeMasters);
                })
                .toList();
    }

    @Override
    public void delete(Long id) {

        VestingRefundType entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("VestingRefundType not found: " + id)
                );

        repository.delete(entity);
    }
}