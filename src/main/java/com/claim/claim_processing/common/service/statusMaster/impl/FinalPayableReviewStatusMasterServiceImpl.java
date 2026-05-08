package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.FinalPayableReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.FinalPayableReviewStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.FinalPayableReviewStatusMaster;
import com.claim.claim_processing.common.mapper.statusMaster.FinalPayableReviewStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.FinalPayableReviewStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.FinalPayableReviewStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinalPayableReviewStatusMasterServiceImpl
        implements FinalPayableReviewStatusMasterService {

    private final FinalPayableReviewStatusMasterRepository repository;
    private final FinalPayableReviewStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public FinalPayableReviewStatusResponseDto create(
            FinalPayableReviewStatusRequestDto dto
    ) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Final payable review status code already exists: " + dto.getCode()
            );
        }

        FinalPayableReviewStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        FinalPayableReviewStatusMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // ================= UPDATE =================
    @Override
    public FinalPayableReviewStatusResponseDto update(
            Long id,
            FinalPayableReviewStatusRequestDto dto
    ) {

        FinalPayableReviewStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Final payable review status not found with id: " + id
                        )
                );

        // Prevent duplicate code update
        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Final payable review status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        FinalPayableReviewStatusMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // ================= GET BY ID =================
    @Override
    public FinalPayableReviewStatusResponseDto getById(Long id) {

        FinalPayableReviewStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Final payable review status not found with id: " + id
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY CODE =================
    @Override
    public FinalPayableReviewStatusResponseDto getByCode(String code) {

        FinalPayableReviewStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Final payable review status not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================
    @Override
    public List<FinalPayableReviewStatusResponseDto> getAll() {

        List<FinalPayableReviewStatusMaster> list = repository.findAll();

        list.sort(
                Comparator.comparing(FinalPayableReviewStatusMaster::getDisplayOrder)
        );

        return mapper.toResponseDtoList(list);
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public List<FinalPayableReviewStatusResponseDto> getAllActive() {

        List<FinalPayableReviewStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(
                Comparator.comparing(FinalPayableReviewStatusMaster::getDisplayOrder)
        );

        return mapper.toResponseDtoList(list);
    }

    // ================= DELETE (Soft Delete) =================
    @Override
    public void delete(Long id) {

        FinalPayableReviewStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Final payable review status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}