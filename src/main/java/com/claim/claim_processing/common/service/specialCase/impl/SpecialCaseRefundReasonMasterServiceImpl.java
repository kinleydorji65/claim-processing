package com.claim.claim_processing.common.service.specialCase.impl;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseRefundReasonResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundReasonMaster;
import com.claim.claim_processing.common.mapper.specialCase.SpecialCaseRefundReasonMasterMapper;
import com.claim.claim_processing.common.repository.specialCase.SpecialCaseRefundReasonMasterRepository;
import com.claim.claim_processing.common.service.specialCase.SpecialCaseRefundReasonMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialCaseRefundReasonMasterServiceImpl implements SpecialCaseRefundReasonMasterService {

    private final SpecialCaseRefundReasonMasterRepository repository;
    private final SpecialCaseRefundReasonMasterMapper mapper;

    @Override
    public ApiResponseDTO<SpecialCaseRefundReasonResponseDto> create(
            SpecialCaseRefundReasonRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Special case refund reason already exists with code: " + requestDto.getCode()
            );
        }

        SpecialCaseRefundReasonMaster entity = mapper.toEntity(requestDto);
        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        SpecialCaseRefundReasonMaster saved = repository.save(entity);

        return ApiResponseDTO.created(
                mapper.toResponseDto(saved)
        );
    }

    @Override
    public ApiResponseDTO<SpecialCaseRefundReasonResponseDto> update(
            Long id,
            SpecialCaseRefundReasonRequestDto requestDto) {

        SpecialCaseRefundReasonMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Special case refund reason not found with id: " + id
                ));

        if (requestDto.getCode() != null
                && !requestDto.getCode().equalsIgnoreCase(entity.getCode())
                && repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Special case refund reason already exists with code: " + requestDto.getCode()
            );
        }

        mapper.updateEntityFromDto(requestDto, entity);
        entity.setUpdatedBy(requestDto.getUpdatedBy());

        SpecialCaseRefundReasonMaster updated = repository.save(entity);

        return ApiResponseDTO.success(
                "Special case refund reason updated successfully",
                mapper.toResponseDto(updated)
        );
    }


    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<SpecialCaseRefundReasonResponseDto> getById(Long id) {

        SpecialCaseRefundReasonMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Special case refund reason not found with id: " + id
                ));

        return ApiResponseDTO.success(
                "Special case refund reason fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<SpecialCaseRefundReasonResponseDto> getByCode(String code) {

        SpecialCaseRefundReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Special case refund reason not found with code: " + code
                ));

        return ApiResponseDTO.success(
                "Special case refund reason fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<SpecialCaseRefundReasonResponseDto>> getAll() {

        List<SpecialCaseRefundReasonResponseDto> response = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Special case refund reasons fetched successfully",
                response
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<SpecialCaseRefundReasonResponseDto>> getAllActive() {

        List<SpecialCaseRefundReasonResponseDto> response =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(
                "Active special case refund reasons fetched successfully",
                response
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        SpecialCaseRefundReasonMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Special case refund reason not found with id: " + id
                ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Special case refund reason deleted successfully",
                "Deleted successfully"
        );
    }
}