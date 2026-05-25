package com.claim.claim_processing.common.service.payment.impl;

import com.claim.claim_processing.common.DTO.request.payment.PaymentStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.payment.PaymentStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.paymentMaster.PaymentStatusMaster;
import com.claim.claim_processing.common.mapper.payment.PaymentStatusMasterMapper;
import com.claim.claim_processing.common.repository.payment.PaymentStatusMasterRepository;
import com.claim.claim_processing.common.service.payment.PaymentStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentStatusMasterServiceImpl implements PaymentStatusMasterService {

    private final PaymentStatusMasterRepository repository;
    private final PaymentStatusMasterMapper mapper;

    @Override
    public ApiResponseDTO<PaymentStatusResponseDto> create(PaymentStatusRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Payment status already exists with code: " + requestDto.getCode()
            );
        }

        PaymentStatusMaster entity = mapper.toEntity(requestDto);
        entity.setIsActive(ActivityEnum.Y);
        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setUpdatedBy(requestDto.getUpdatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        PaymentStatusMaster saved = repository.save(entity);

        return ApiResponseDTO.created(
                mapper.toResponseDto(saved)
        );
    }

    @Override
    public ApiResponseDTO<PaymentStatusResponseDto> update(
            Long id,
            PaymentStatusRequestDto requestDto
    ) {

        PaymentStatusMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Payment status not found with id: " + id
                ));

        if (requestDto.getCode() != null
                && !requestDto.getCode().equalsIgnoreCase(entity.getCode())
                && repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Payment status already exists with code: " + requestDto.getCode()
            );
        }

        mapper.updateEntityFromDto(requestDto, entity);
        entity.setUpdatedBy(requestDto.getUpdatedBy());

        PaymentStatusMaster updated = repository.save(entity);

        return ApiResponseDTO.success(
                "Payment status updated successfully",
                mapper.toResponseDto(updated)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<PaymentStatusResponseDto> getById(Long id) {

        PaymentStatusMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Payment status not found with id: " + id
                ));

        return ApiResponseDTO.success(
                "Payment status fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<PaymentStatusResponseDto> getByCode(String code) {

        PaymentStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Payment status not found with code: " + code
                ));

        return ApiResponseDTO.success(
                "Payment status fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<PaymentStatusResponseDto>> getAll() {

        List<PaymentStatusResponseDto> response = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Payment statuses fetched successfully",
                response
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<PaymentStatusResponseDto>> getAllActive() {

        List<PaymentStatusResponseDto> response = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(
                "Active payment statuses fetched successfully",
                response
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        PaymentStatusMaster entity = repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Payment status not found with id: " + id
                ));

        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);

        return ApiResponseDTO.success(
                "Payment status deleted successfully",
                "Deleted successfully"
        );
    }
}