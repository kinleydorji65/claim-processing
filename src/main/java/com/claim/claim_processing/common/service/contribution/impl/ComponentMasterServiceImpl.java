package com.claim.claim_processing.common.service.contribution.impl;

import com.claim.claim_processing.common.DTO.request.contribution.ComponentRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.contribution.ComponentResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.mapper.contribution.ComponentMasterMapper;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.common.service.contribution.ComponentMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponentMasterServiceImpl implements ComponentMasterService {

    private final ComponentMasterRepository repository;
    private final ComponentMasterMapper mapper;

    @Override
    public ApiResponseDTO<ComponentResponseDto> create(
            ComponentRequestDto requestDto
    ) {

        validateCode(requestDto.getCode(), null);

        ComponentMaster entity = mapper.toEntity(requestDto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        ComponentMaster savedEntity = repository.save(entity);

        return ApiResponseDTO.created(
                mapper.toResponseDto(savedEntity)
        );
    }

    @Override
    public ApiResponseDTO<ComponentResponseDto> update(
            Long id,
            ComponentRequestDto requestDto
    ) {

        ComponentMaster entity = getEntityById(id);

        validateCode(requestDto.getCode(), entity.getCode());

        mapper.updateEntityFromDto(requestDto, entity);

        ComponentMaster updatedEntity = repository.save(entity);

        return ApiResponseDTO.success(
                "Component master updated successfully.",
                mapper.toResponseDto(updatedEntity)
        );
    }

    @Override
    public ApiResponseDTO<ComponentResponseDto> getById(Long id) {

        ComponentMaster entity = getEntityById(id);

        return ApiResponseDTO.success(
                "Component master fetched successfully.",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    public ApiResponseDTO<List<ComponentResponseDto>> getAll() {

        return ApiResponseDTO.success(
                "Component master list fetched successfully.",
                mapper.toResponseDto(repository.findAll())
        );
    }

    @Override
    public ApiResponseDTO<List<ComponentResponseDto>> getAllActive() {

        return ApiResponseDTO.success(
                "Active component master list fetched successfully.",
                mapper.toResponseDto(
                        repository.findByIsActive(ActivityEnum.Y)
                )
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        ComponentMaster entity = getEntityById(id);

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Component master deleted successfully.",
                "Deleted Successfully"
        );
    }

    /**
     * Common method to fetch entity by id
     */
    private ComponentMaster getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Component master",
                                String.valueOf(id)
                        )
                );
    }

    /**
     * Common method to validate duplicate code
     */
    private void validateCode(String newCode, String existingCode) {

        if (newCode == null) {
            return;
        }

        boolean isDuplicate = repository.existsByCode(newCode)
                && (existingCode == null || !newCode.equals(existingCode));

        if (isDuplicate) {
            throw ClaimException.conflict(
                    "Component code already exists: " + newCode
            );
        }
    }
}