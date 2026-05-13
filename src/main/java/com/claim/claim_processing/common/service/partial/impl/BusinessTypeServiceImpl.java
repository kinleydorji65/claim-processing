package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.BusinessTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.BusinessTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.BusinessTypeUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.BusinessTypeMaster;
import com.claim.claim_processing.common.mapper.partial.BusinessTypeMapper;
import com.claim.claim_processing.common.repository.partial.BusinessTypeRepository;
import com.claim.claim_processing.common.service.partial.BusinessTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BusinessTypeServiceImpl implements BusinessTypeService {

    private final BusinessTypeRepository repository;
    private final BusinessTypeMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<BusinessTypeResponseDto> create(
            BusinessTypeRequestDto requestDto
    ) {

        try {

            if (repository.existsByCode(requestDto.getCode())) {
                throw ClaimException.conflict(
                        "Business Type already exists with code: "
                                + requestDto.getCode()
                );
            }

            BusinessTypeMaster entity = mapper.toEntity(requestDto);

            entity.setCreatedBy(requestDto.getCreatedBy());
            entity.setUpdatedBy(requestDto.getCreatedBy());

            entity.setIsActive(
                    requestDto.getIsActive() != null
                            ? requestDto.getIsActive()
                            : ActivityEnum.Y
            );

            BusinessTypeMaster savedEntity = repository.save(entity);

            return ApiResponseDTO.created(
                    mapper.toResponseDto(savedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error creating Business Type", ex);

            throw ClaimException.internalError(
                    "Failed to create Business Type",
                    ex
            );
        }
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<BusinessTypeResponseDto> update(
            Long id,
            BusinessTypeUpdateDto updateDto
    ) {

        try {

            BusinessTypeMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "BusinessType",
                                    String.valueOf(id)
                            )
                    );

            if (updateDto.getCode() != null
                    && repository.existsByCodeAndIdNot(
                    updateDto.getCode(),
                    id
            )) {

                throw ClaimException.conflict(
                        "Business Type already exists with code: "
                                + updateDto.getCode()
                );
            }

            mapper.updateEntityFromDto(updateDto, entity);

            entity.setUpdatedBy(updateDto.getUpdatedBy());

            BusinessTypeMaster updatedEntity = repository.save(entity);

            return ApiResponseDTO.success(
                    mapper.toResponseDto(updatedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error updating Business Type", ex);

            throw ClaimException.internalError(
                    "Failed to update Business Type",
                    ex
            );
        }
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<BusinessTypeResponseDto> getById(
            Long id
    ) {

        BusinessTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "BusinessType",
                                String.valueOf(id)
                        )
                );

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<BusinessTypeResponseDto> getByCode(
            String code
    ) {

        BusinessTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Business Type not found with code: "
                                        + code
                        )
                );

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<BusinessTypeResponseDto>> getAll() {

        return ApiResponseDTO.success(
                mapper.toResponseDtoList(repository.findAll())
        );
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<BusinessTypeResponseDto>> getAllActive() {

        return ApiResponseDTO.success(
                mapper.toResponseDtoList(
                        repository.findByIsActive(ActivityEnum.Y)
                )
        );
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(
            Long id
    ) {

        try {

            BusinessTypeMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "BusinessType",
                                    String.valueOf(id)
                            )
                    );

            entity.setIsActive(ActivityEnum.N);

            repository.save(entity);

            return ApiResponseDTO.success(
                    "Business Type deactivated successfully",
                    null
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error deleting Business Type", ex);

            throw ClaimException.internalError(
                    "Failed to delete Business Type",
                    ex
            );
        }
    }
}