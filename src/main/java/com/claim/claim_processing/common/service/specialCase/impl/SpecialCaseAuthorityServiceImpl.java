package com.claim.claim_processing.common.service.specialCase.impl;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseAuthorityRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseAuthorityResponseDto;
import com.claim.claim_processing.common.DTO.update.specialCase.SpecialCaseAuthorityUpdateRequestDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;
import com.claim.claim_processing.common.mapper.specialCase.SpecialCaseAuthorityMapper;
import com.claim.claim_processing.common.repository.specialCase.SpecialCaseAuthorityRepository;
import com.claim.claim_processing.common.service.specialCase.SpecialCaseAuthorityService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialCaseAuthorityServiceImpl implements SpecialCaseAuthorityService {

    private final SpecialCaseAuthorityRepository repository;
    private final SpecialCaseAuthorityMapper mapper;

    @Override
    public ApiResponseDTO<SpecialCaseAuthorityResponseDto> create(
            SpecialCaseAuthorityRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Special case authority already exists with code: " + requestDto.getCode()
            );
        }

        SpecialCaseRefundAuthorityMaster entity = mapper.toEntity(requestDto);

        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setUpdatedBy(requestDto.getUpdatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        SpecialCaseRefundAuthorityMaster saved = repository.save(entity);

        return ApiResponseDTO.created(
                mapper.toResponseDto(saved)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<SpecialCaseAuthorityResponseDto> getById(Long id) {

        SpecialCaseRefundAuthorityMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Special case authority not found with id: " + id
                ));

        return ApiResponseDTO.success(
                "Special case authority fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<SpecialCaseAuthorityResponseDto> getByCode(String code) {

        SpecialCaseRefundAuthorityMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Special case authority not found with code: " + code
                ));

        return ApiResponseDTO.success(
                "Special case authority fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<SpecialCaseAuthorityResponseDto>> getAll() {

        List<SpecialCaseAuthorityResponseDto> response = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Special case authorities fetched successfully",
                response
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<SpecialCaseAuthorityResponseDto>> getAllActive() {

        List<SpecialCaseAuthorityResponseDto> response =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(
                "Active special case authorities fetched successfully",
                response
        );
    }

    @Override
    public ApiResponseDTO<SpecialCaseAuthorityResponseDto> update(
            Long id,
            SpecialCaseAuthorityUpdateRequestDto updateRequestDto) {

        SpecialCaseRefundAuthorityMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Special case authority not found with id: " + id
                ));

        if (updateRequestDto.getCode() != null
                && !updateRequestDto.getCode().equalsIgnoreCase(entity.getCode())
                && repository.existsByCode(updateRequestDto.getCode())) {

            throw ClaimException.conflict(
                    "Special case authority already exists with code: "
                            + updateRequestDto.getCode()
            );
        }

        mapper.updateEntityFromDto(updateRequestDto, entity);

        entity.setUpdatedBy(updateRequestDto.getUpdatedBy());
        entity.setUpdatedAt(LocalDateTime.now());

        SpecialCaseRefundAuthorityMaster updated = repository.save(entity);

        return ApiResponseDTO.success(
                "Special case authority updated successfully",
                mapper.toResponseDto(updated)
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        SpecialCaseRefundAuthorityMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Special case authority not found with id: " + id
                ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Special case authority deleted successfully",
                "Deleted successfully"
        );
    }
}