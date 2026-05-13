package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.PayeeTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.PayeeTypeResponseDto;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.mapper.common.PayeeTypeMapper;
import com.claim.claim_processing.common.repository.common.PayeeTypeRepository;
import com.claim.claim_processing.common.service.common.PayeeTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayeeTypeServiceImpl implements PayeeTypeService {

    private final PayeeTypeRepository repository;
    private final PayeeTypeMapper mapper;

    @Override
    public ApiResponseDTO<PayeeTypeResponseDto> create(PayeeTypeRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Payee Type code already exists: " + dto.getCode());
        }

        PayeeTypeMaster entity = mapper.toEntity(dto);
        PayeeTypeMaster saved = repository.save(entity);

        return ApiResponseDTO.created(mapper.toResponseDto(saved));
    }

    @Override
    public ApiResponseDTO<PayeeTypeResponseDto> patch(Long id, PayeeTypeRequestDto dto) {

        PayeeTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Payee Type", String.valueOf(id))
                );

        mapper.updateEntityFromDto(dto, entity);

        return ApiResponseDTO.success(
                mapper.toResponseDto(repository.save(entity))
        );
    }

    @Override
    public ApiResponseDTO<PayeeTypeResponseDto> getById(Long id) {

        PayeeTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Payee Type", String.valueOf(id))
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<PayeeTypeResponseDto> getByCode(String code) {

        PayeeTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Payee Type", code)
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<List<PayeeTypeResponseDto>> getAll() {

        return ApiResponseDTO.success(
                repository.findAll()
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList()
        );
    }

    @Override
    public ApiResponseDTO<List<PayeeTypeResponseDto>> getAllActive() {

        return ApiResponseDTO.success(
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList()
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        PayeeTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Payee Type", String.valueOf(id))
                );

        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);

        return ApiResponseDTO.success("Payee Type deactivated successfully");
    }
}