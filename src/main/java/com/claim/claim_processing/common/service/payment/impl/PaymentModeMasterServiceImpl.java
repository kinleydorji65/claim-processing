package com.claim.claim_processing.common.service.payment.impl;

import com.claim.claim_processing.common.DTO.request.payment.PaymentModeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.payment.PaymentModeResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.paymentMaster.PaymentModeMaster;
import com.claim.claim_processing.common.mapper.payment.PaymentModeMasterMapper;
import com.claim.claim_processing.common.repository.payment.PaymentModeMasterRepository;
import com.claim.claim_processing.common.service.payment.PaymentModeMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentModeMasterServiceImpl implements PaymentModeMasterService {

    private final PaymentModeMasterRepository repository;
    private final PaymentModeMasterMapper mapper;

    @Override
    public ApiResponseDTO<PaymentModeResponseDto> create(PaymentModeRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Payment Mode code already exists: " + requestDto.getCode()
            );
        }

        PaymentModeMaster entity = mapper.toEntity(requestDto);

        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setUpdatedBy(requestDto.getUpdatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<PaymentModeResponseDto> update(
            Long id,
            PaymentModeRequestDto requestDto) {

        PaymentModeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment Mode not found with id: " + id
                        ));

        mapper.updateEntityFromDto(requestDto, entity);

        entity.setUpdatedBy(requestDto.getUpdatedBy());

        return ApiResponseDTO.success(
                mapper.toResponseDto(repository.save(entity))
        );
    }

    @Override
    public ApiResponseDTO<PaymentModeResponseDto> patch(
            Long id,
            PaymentModeRequestDto requestDto) {

        PaymentModeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment Mode not found with id: " + id
                        ));

        mapper.patchEntityFromDto(requestDto, entity);

        entity.setUpdatedBy(requestDto.getUpdatedBy());

        return ApiResponseDTO.success(
                mapper.toResponseDto(repository.save(entity))
        );
    }

    @Override
    public ApiResponseDTO<PaymentModeResponseDto> getById(Long id) {

        PaymentModeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment Mode not found with id: " + id
                        ));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<PaymentModeResponseDto> getByCode(String code) {

        PaymentModeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment Mode not found with code: " + code
                        ));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<List<PaymentModeResponseDto>> getAll() {

        List<PaymentModeResponseDto> responseDtos = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    @Override
    public ApiResponseDTO<List<PaymentModeResponseDto>> getAllActive() {

        List<PaymentModeResponseDto> responseDtos = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        PaymentModeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment Mode not found with id: " + id
                        ));

        repository.delete(entity);

        return ApiResponseDTO.success("Payment Mode deleted successfully");
    }
}