package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.PaymentLineStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.PaymentLineStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.PaymentLineStatusMaster;
import com.claim.claim_processing.common.mapper.statusMaster.PaymentLineStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.PaymentLineStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.PaymentLineStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentLineStatusMasterServiceImpl implements PaymentLineStatusMasterService {

    private final PaymentLineStatusMasterRepository repository;
    private final PaymentLineStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public ApiResponseDTO<PaymentLineStatusResponseDto> create(PaymentLineStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Payment line status code already exists: " + dto.getCode()
            );
        }

        PaymentLineStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        PaymentLineStatusMaster saved = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(saved));
    }

    // ================= UPDATE =================
    @Override
    public ApiResponseDTO<PaymentLineStatusResponseDto> update(Long id, PaymentLineStatusRequestDto dto) {

        PaymentLineStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment line status not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Payment line status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        PaymentLineStatusMaster updated = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(updated));
    }

    // ================= GET BY ID =================
    @Override
    public ApiResponseDTO<PaymentLineStatusResponseDto> getById(Long id) {

        PaymentLineStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment line status not found with id: " + id
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET BY CODE =================
    @Override
    public ApiResponseDTO<PaymentLineStatusResponseDto> getByCode(String code) {

        PaymentLineStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment line status not found with code: " + code
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET ALL =================
    @Override
    public ApiResponseDTO<List<PaymentLineStatusResponseDto>> getAll() {

        List<PaymentLineStatusMaster> list = repository.findAll();

        list.sort(Comparator.comparing(PaymentLineStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public ApiResponseDTO<List<PaymentLineStatusResponseDto>> getAllActive() {

        List<PaymentLineStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(PaymentLineStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        PaymentLineStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment line status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
        return ApiResponseDTO.success("Payment line status deleted successfully");
    }
}