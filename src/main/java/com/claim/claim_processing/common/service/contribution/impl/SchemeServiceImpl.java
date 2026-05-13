package com.claim.claim_processing.common.service.contribution.impl;

import com.claim.claim_processing.common.DTO.request.contribution.SchemeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.contribution.SchemeUpdateRequestDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.mapper.contribution.SchemeTypeMapper;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.service.contribution.SchemeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SchemeServiceImpl implements SchemeService {

    private final SchemeTypeRepository repository;
    private final SchemeTypeMapper mapper;

    @Override
    public ApiResponseDTO<SchemeTypeResponseDto> create(
            SchemeCreateRequestDto dto
    ) {

        try {

            if (repository.existsByCode(dto.getCode())) {
                throw ClaimException.conflict(
                        "Scheme code already exists: " + dto.getCode()
                );
            }

            SchemeMaster entity = mapper.toEntity(dto);

            if (entity.getIsActive() == null) {
                entity.setIsActive(ActivityEnum.Y);
            }

            SchemeMaster savedEntity = repository.save(entity);

            return ApiResponseDTO.created(
                    mapper.toResponseDto(savedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error creating Scheme", ex);

            throw ClaimException.internalError(
                    "Failed to create Scheme",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<SchemeTypeResponseDto> update(
            Long id,
            SchemeUpdateRequestDto dto
    ) {

        try {

            SchemeMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Scheme",
                                    String.valueOf(id)
                            )
                    );

            mapper.updateEntityFromDto(dto, entity);

            SchemeMaster updatedEntity = repository.save(entity);

            return ApiResponseDTO.success(
                    "Scheme updated successfully",
                    mapper.toResponseDto(updatedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error updating Scheme", ex);

            throw ClaimException.internalError(
                    "Failed to update Scheme",
                    ex
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<SchemeTypeResponseDto> getById(
            Long id
    ) {

        try {

            SchemeMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Scheme",
                                    String.valueOf(id)
                            )
                    );

            return ApiResponseDTO.success(
                    mapper.toResponseDto(entity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error fetching Scheme by id", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Scheme",
                    ex
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<SchemeTypeResponseDto>> getAll() {

        try {

            List<SchemeTypeResponseDto> response =
                    repository.findAll()
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            return ApiResponseDTO.success(response);

        } catch (Exception ex) {

            log.error("Error fetching all Schemes", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Schemes",
                    ex
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<SchemeTypeResponseDto>> getAllActive() {

        try {

            List<SchemeTypeResponseDto> response =
                    repository.findByIsActive(ActivityEnum.Y)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            return ApiResponseDTO.success(response);

        } catch (Exception ex) {

            log.error("Error fetching active Schemes", ex);

            throw ClaimException.internalError(
                    "Failed to fetch active Schemes",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<String> delete(
            Long id
    ) {

        try {

            SchemeMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Scheme",
                                    String.valueOf(id)
                            )
                    );

            repository.delete(entity);

            return ApiResponseDTO.success(
                    "Scheme deleted successfully",
                    null
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error deleting Scheme", ex);

            throw ClaimException.internalError(
                    "Failed to delete Scheme",
                    ex
            );
        }
    }
}