package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.StageRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.StageMapper;
import com.claim.claim_processing.common.repository.common.StageRepository;
import com.claim.claim_processing.common.service.common.StageService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageServiceImpl implements StageService {

    private final StageRepository repository;
    private final StageMapper mapper;

    @Override
    public ApiResponseDTO<StageResponseDto> create(
            StageRequestDto dto
    ) {

        try {

            if (repository.existsByCode(dto.getCode())) {
                throw ClaimException.conflict(
                        "Stage code already exists: " + dto.getCode()
                );
            }

            StageMaster entity = mapper.toEntity(dto);

            StageMaster savedEntity = repository.save(entity);

            return ApiResponseDTO.created(
                    mapper.toResponseDto(savedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error creating Stage", ex);

            throw ClaimException.internalError(
                    "Failed to create Stage",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<StageResponseDto> update(
            Long id,
            StageRequestDto dto
    ) {

        try {

            StageMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Stage",
                                    String.valueOf(id)
                            )
                    );

            mapper.updateEntityFromDto(dto, entity);

            entity.setUpdatedBy(dto.getUpdatedBy());

            StageMaster updatedEntity = repository.save(entity);

            return ApiResponseDTO.success(
                    "Stage updated successfully",
                    mapper.toResponseDto(updatedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error updating Stage", ex);

            throw ClaimException.internalError(
                    "Failed to update Stage",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<StageResponseDto> getById(
            Long id
    ) {

        try {

            StageMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Stage",
                                    String.valueOf(id)
                            )
                    );

            return ApiResponseDTO.success(
                    mapper.toResponseDto(entity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error fetching Stage by id", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Stage",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<StageResponseDto> getByCode(
            String code
    ) {

        try {

            StageMaster entity = repository.findByCode(code)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Stage code",
                                    code
                            )
                    );

            return ApiResponseDTO.success(
                    mapper.toResponseDto(entity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error fetching Stage by code", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Stage",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<List<StageResponseDto>> getAll() {

        try {

            List<StageResponseDto> response =
                    repository.findAll()
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            return ApiResponseDTO.success(response);

        } catch (Exception ex) {

            log.error("Error fetching all Stages", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Stages",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<List<StageResponseDto>> getAllActive() {

        try {

            List<StageResponseDto> response =
                    repository.findByIsActive(ActivityEnum.Y)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            return ApiResponseDTO.success(response);

        } catch (Exception ex) {

            log.error("Error fetching active Stages", ex);

            throw ClaimException.internalError(
                    "Failed to fetch active Stages",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        try {

            StageMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Stage",
                                    String.valueOf(id)
                            )
                    );

            // SOFT DELETE
            entity.setIsActive(ActivityEnum.N);
            entity.setUpdatedAt(LocalDateTime.now());

            repository.save(entity);

            return ApiResponseDTO.success(
                    "Stage deleted successfully",
                    null
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error while deleting Stage", ex);

            throw ClaimException.internalError(
                    "Failed to delete Stage",
                    ex
            );
        }
    }
}