package com.claim.claim_processing.common.service.beneficiary.impl;

import com.claim.claim_processing.common.DTO.request.beneficiary.ClaimantTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.beneficiary.ClaimantTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.beneficiary.ClaimantTypeUpdateRequestDto;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.beneficiary.ClaimantTypeMapper;
import com.claim.claim_processing.common.repository.beneficiary.ClaimantTypeRepository;
import com.claim.claim_processing.common.service.beneficiary.ClaimantTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimantTypeServiceImpl implements ClaimantTypeService {

    private final ClaimantTypeRepository repository;
    private final ClaimantTypeMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<ClaimantTypeResponseDto>> getAllActive() {

        List<ClaimantTypeResponseDto> response = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Active claimant types fetched successfully",
                response
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<ClaimantTypeResponseDto> getById(Long id) {

        ClaimantTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claimant type not found with id: " + id
                ));

        return ApiResponseDTO.success(
                "Claimant type fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<ClaimantTypeResponseDto> getByCode(String code) {

        ClaimantTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claimant type not found with code: " + code
                ));

        return ApiResponseDTO.success(
                "Claimant type fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    public ApiResponseDTO<ClaimantTypeResponseDto> create(
            ClaimantTypeCreateRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Claimant type already exists with code: " + requestDto.getCode()
            );
        }

        ClaimantTypeMaster entity = mapper.toEntity(requestDto);
        entity.setIsActive(ActivityEnum.Y);
        entity.setUpdatedBy(requestDto.getUpdatedBy());
        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        ClaimantTypeMaster saved = repository.save(entity);

        return ApiResponseDTO.created(
                mapper.toResponseDto(saved)
        );
    }

    @Override
    public ApiResponseDTO<ClaimantTypeResponseDto> update(
            Long id,
            ClaimantTypeUpdateRequestDto requestDto) {

        ClaimantTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claimant type not found with id: " + id
                ));

        if (requestDto.getCode() != null
                && !requestDto.getCode().equalsIgnoreCase(entity.getCode())
                && repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Claimant type already exists with code: " + requestDto.getCode()
            );
        }

        mapper.updateEntityFromDto(requestDto, entity);
        entity.setUpdatedBy(requestDto.getUpdatedBy());
        entity.setUpdatedAt(LocalDateTime.now());

        ClaimantTypeMaster updated = repository.save(entity);

        return ApiResponseDTO.success(
                "Claimant type updated successfully",
                mapper.toResponseDto(updated)
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        ClaimantTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claimant type not found with id: " + id
                ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Claimant type deleted successfully",
                "Deleted successfully"
        );
    }
}