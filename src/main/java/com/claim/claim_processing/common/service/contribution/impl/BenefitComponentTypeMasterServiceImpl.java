package com.claim.claim_processing.common.service.contribution.impl;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeDetail;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.mapper.contribution.BenefitComponentTypeMasterMapper;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeDetailRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeMasterRepository;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.common.service.contribution.BenefitComponentTypeMasterService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BenefitComponentTypeMasterServiceImpl
        implements BenefitComponentTypeMasterService {

    private final BenefitComponentTypeMasterRepository repository;
    private final BenefitComponentTypeMasterMapper mapper;
    private final BenefitComponentTypeDetailRepository detailMapRepository;
    private final ComponentMasterRepository componentRepo;

    @Override
    public ApiResponseDTO<BenefitComponentTypeMasterResponseDto> create(
            BenefitComponentTypeMasterRequestDto requestDto) {
        validateDuplicateCode(requestDto.getCode());

        BenefitComponentTypeMaster entity = mapper.toEntity(requestDto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        BenefitComponentTypeMaster saved = repository.save(entity);
        List<BenefitComponentTypeDetail> mappings = mapComponentDetails(entity, requestDto.getComponentIds());
        return ApiResponseDTO.success(mapper.toResponseDto(saved, mappings));
    }

    private List<BenefitComponentTypeDetail> mapComponentDetails(BenefitComponentTypeMaster entity,
            List<Long> componentIds) {

        List<BenefitComponentTypeDetail> components = detailMapRepository
                .findByBenefitComponentType_IdAndComponent_IdIn(entity.getId(), componentIds);
        List<BenefitComponentTypeDetail> mappings;
        if (components.isEmpty()) {
            mappings = componentIds.stream().map(componentId -> {
                BenefitComponentTypeDetail componentTypeDetail = BenefitComponentTypeDetail.builder()
                        .benefitComponentType(entity)
                        .component(getComponent(componentId))
                        .build();
                detailMapRepository.save(componentTypeDetail);
                return componentTypeDetail;
            }).toList();
        } else {
            mappings = components.stream()
                    .filter(map -> componentIds.contains(map.getComponent().getId()))
                    .map(map -> {
                        map.setBenefitComponentType(entity);
                        map.setComponent(getComponent(map.getComponent().getId()));
                        return map;
                    })
                    .toList();
        }
        return mappings;
    }

    private ComponentMaster getComponent(Long componentId) {
        return componentRepo.findById(componentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Component not found with id: " + componentId));
    }

    @Override
    public ApiResponseDTO<BenefitComponentTypeMasterResponseDto> update(
            Long id,
            BenefitComponentTypeMasterRequestDto requestDto) {
        BenefitComponentTypeMaster entity = findEntityById(id);

        if (requestDto.getCode() != null &&
                !requestDto.getCode().equalsIgnoreCase(entity.getCode())) {

            validateDuplicateCode(requestDto.getCode());
        }

        mapper.updateEntityFromDto(requestDto, entity);

        BenefitComponentTypeMaster updated = repository.save(entity);
        List<BenefitComponentTypeDetail> mappings = mapComponentDetails(entity, requestDto.getComponentIds());
        return ApiResponseDTO.success(mapper.toResponseDto(updated, mappings));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<BenefitComponentTypeMasterResponseDto> getById(Long id) {
        BenefitComponentTypeMaster entity = findEntityById(id);
        List<BenefitComponentTypeDetail> mappings = detailMapRepository.findByBenefitComponentType_Id(id);
        return ApiResponseDTO.success(mapper.toResponseDto(entity, mappings));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> getAll() {
        List<BenefitComponentTypeMasterResponseDto> response = repository.findAll()
                .stream()
                .map(entity -> {
                    List<BenefitComponentTypeDetail> mappings = detailMapRepository
                            .findByBenefitComponentType_Id(entity.getId());
                    return mapper.toResponseDto(entity, mappings);
                })
                .toList();
        return ApiResponseDTO.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> getAllWithoutComponent() {
        List<BenefitComponentTypeMaster> benefitComponents = repository.findAll();
        return ApiResponseDTO.success(mapper.toResponseDto(benefitComponents));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> getByStatus(
            ActivityEnum isActive) {
        List<BenefitComponentTypeMasterResponseDto> response = repository.findByIsActive(isActive)
                .stream()
                .map(entity -> {
                    List<BenefitComponentTypeDetail> mappings = detailMapRepository
                            .findByBenefitComponentType_Id(entity.getId());
                    return mapper.toResponseDto(entity, mappings);
                })
                .toList();
        return ApiResponseDTO.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> searchByName(
            String keyword) {
        List<BenefitComponentTypeMasterResponseDto> response = repository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(entity -> {
                    List<BenefitComponentTypeDetail> mappings = detailMapRepository
                            .findByBenefitComponentType_Id(entity.getId());
                    return mapper.toResponseDto(entity, mappings);
                })
                .toList();
        return ApiResponseDTO.success(response);
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {
        BenefitComponentTypeMaster entity = findEntityById(id);
        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);
        return ApiResponseDTO.success("Benefit Component Type deleted successfully");
    }

    // =========================
    // PRIVATE METHODS
    // =========================

    private BenefitComponentTypeMaster findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Benefit Component Type not found with id: " + id));
    }

    private void validateDuplicateCode(String code) {
        if (code != null && repository.existsByCode(code)) {
            throw new IllegalArgumentException(
                    "Code already exists: " + code);
        }
    }
}