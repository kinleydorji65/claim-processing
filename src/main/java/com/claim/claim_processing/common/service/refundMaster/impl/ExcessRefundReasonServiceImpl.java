package com.claim.claim_processing.common.service.refundMaster.impl;

import com.claim.claim_processing.common.DTO.request.refundMaster.ExcessRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.refundMaster.ExcessRefundReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.refundMaster.ExcessRefundReasonUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.refundMaster.ExcessRefundReasonMaster;
import com.claim.claim_processing.common.mapper.refundMaster.ExcessRefundReasonMapper;
import com.claim.claim_processing.common.repository.refundMaster.ExcessRefundReasonRepository;
import com.claim.claim_processing.common.service.refundMaster.ExcessRefundReasonService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExcessRefundReasonServiceImpl implements ExcessRefundReasonService {

    private final ExcessRefundReasonRepository repository;
    private final ExcessRefundReasonMapper mapper;

    @Override
    public ApiResponseDTO<ExcessRefundReasonResponseDto> create(
            ExcessRefundReasonRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Excess refund reason already exists with code: " + requestDto.getCode()
            );
        }

        ExcessRefundReasonMaster entity = mapper.toEntity(requestDto);
        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setUpdatedBy(requestDto.getUpdatedBy());

        ExcessRefundReasonMaster saved = repository.save(entity);

        return ApiResponseDTO.created(
                mapper.toResponseDto(saved)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<ExcessRefundReasonResponseDto> getById(Long id) {

        ExcessRefundReasonMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Excess refund reason not found with id: " + id
                ));

        return ApiResponseDTO.success(
                "Excess refund reason fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<ExcessRefundReasonResponseDto> getByCode(String code) {

        ExcessRefundReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Excess refund reason not found with code: " + code
                ));

        return ApiResponseDTO.success(
                "Excess refund reason fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<ExcessRefundReasonResponseDto>> getAll() {

        List<ExcessRefundReasonResponseDto> response = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Excess refund reasons fetched successfully",
                response
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<ExcessRefundReasonResponseDto>> getAllActive() {

        List<ExcessRefundReasonResponseDto> response =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        return ApiResponseDTO.success(
                "Active excess refund reasons fetched successfully",
                response
        );
    }

    @Override
    public ApiResponseDTO<ExcessRefundReasonResponseDto> update(
            Long id,
            ExcessRefundReasonUpdateDto updateDto) {

        ExcessRefundReasonMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Excess refund reason not found with id: " + id
                ));

        if (updateDto.getCode() != null
                && !updateDto.getCode().equalsIgnoreCase(entity.getCode())
                && repository.existsByCode(updateDto.getCode())) {
            throw ClaimException.conflict(
                    "Excess refund reason already exists with code: " + updateDto.getCode()
            );
        }

        mapper.updateEntityFromDto(updateDto, entity);
        ExcessRefundReasonMaster updated = repository.save(entity);

        return ApiResponseDTO.success(
                "Excess refund reason updated successfully",
                mapper.toResponseDto(updated)
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        ExcessRefundReasonMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Excess refund reason not found with id: " + id
                ));

        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);

        return ApiResponseDTO.success(
                "Excess refund reason deleted successfully",
                "Deleted successfully"
        );
    }
}