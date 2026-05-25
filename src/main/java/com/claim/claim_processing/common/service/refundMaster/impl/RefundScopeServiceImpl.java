package com.claim.claim_processing.common.service.refundMaster.impl;

import com.claim.claim_processing.common.DTO.request.refundMaster.RefundScopeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.refundMaster.RefundScopeResponseDto;
import com.claim.claim_processing.common.DTO.update.refundMaster.RefundScopeUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.refundMaster.RefundScopeMaster;
import com.claim.claim_processing.common.mapper.refundMaster.RefundScopeMapper;
import com.claim.claim_processing.common.repository.refundMaster.RefundScopeRepository;
import com.claim.claim_processing.common.service.refundMaster.RefundScopeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundScopeServiceImpl implements RefundScopeService {

    private final RefundScopeRepository repository;
    private final RefundScopeMapper mapper;

    @Override
    public ApiResponseDTO<RefundScopeResponseDto> create(
            RefundScopeRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Refund scope already exists with code: " + requestDto.getCode()
            );
        }

        RefundScopeMaster entity = mapper.toEntity(requestDto);
        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setUpdatedBy(requestDto.getUpdatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        RefundScopeMaster saved = repository.save(entity);

        return ApiResponseDTO.created(
                mapper.toResponseDto(saved)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<RefundScopeResponseDto> getById(Long id) {

        RefundScopeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Refund scope not found with id: " + id
                ));

        return ApiResponseDTO.success(
                "Refund scope fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<RefundScopeResponseDto> getByCode(String code) {

        RefundScopeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Refund scope not found with code: " + code
                ));

        return ApiResponseDTO.success(
                "Refund scope fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<RefundScopeResponseDto>> getAll() {

        List<RefundScopeResponseDto> response = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Refund scopes fetched successfully",
                response
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<RefundScopeResponseDto>> getAllActive() {

        List<RefundScopeResponseDto> response = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Active refund scopes fetched successfully",
                response
        );
    }

    @Override
    public ApiResponseDTO<RefundScopeResponseDto> update(
            Long id,
            RefundScopeUpdateDto updateDto) {

        RefundScopeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Refund scope not found with id: " + id
                ));

        if (updateDto.getCode() != null
                && !updateDto.getCode().equalsIgnoreCase(entity.getCode())
                && repository.existsByCode(updateDto.getCode())) {
            throw ClaimException.conflict(
                    "Refund scope already exists with code: " + updateDto.getCode()
            );
        }

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy(updateDto.getUpdatedBy());

        RefundScopeMaster updated = repository.save(entity);

        return ApiResponseDTO.success(
                "Refund scope updated successfully",
                mapper.toResponseDto(updated)
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        RefundScopeMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Refund scope not found with id: " + id
                ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Refund scope deleted successfully",
                "Deleted successfully"
        );
    }
}