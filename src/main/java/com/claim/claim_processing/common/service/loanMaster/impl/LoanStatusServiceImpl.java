package com.claim.claim_processing.common.service.loanMaster.impl;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.loanMaster.LoanStatusMaster;
import com.claim.claim_processing.common.mapper.loanMaster.LoanStatusMapper;
import com.claim.claim_processing.common.repository.loanMaster.LoanStatusRepository;
import com.claim.claim_processing.common.service.loanMaster.LoanStatusService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanStatusServiceImpl implements LoanStatusService {

    private final LoanStatusRepository repository;
    private final LoanStatusMapper mapper;

    // 🔹 CREATE
    @Override
    public LoanStatusResponseDto create(LoanStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Loan Status already exists with code: " + dto.getCode()
            );
        }

        LoanStatusMaster entity = mapper.toEntity(dto);

        entity.setCreatedBy(dto.getCreatedBy());

        if (dto.getDisplayOrder() != null) {
            entity.setDisplayOrder(dto.getDisplayOrder());
        }

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 UPDATE
    @Override
    public LoanStatusResponseDto update(Long id, LoanStatusRequestDto dto) {

        LoanStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanStatus",
                                id.toString()
                        )
                );

        // duplicate check
        if (dto.getCode() != null &&
                repository.existsByCodeAndIdNot(dto.getCode(), id)) {
            throw ClaimException.conflict(
                    "Loan Status already exists with code: " + dto.getCode()
            );
        }

        mapper.updateEntityFromDto(dto, entity);

        entity.setUpdatedBy(dto.getUpdatedBy());

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 GET BY ID
    @Override
    @Transactional(readOnly = true)
    public LoanStatusResponseDto getById(Long id) {

        LoanStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanStatus",
                                id.toString()
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET BY CODE
    @Override
    @Transactional(readOnly = true)
    public LoanStatusResponseDto getByCode(String code) {

        LoanStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Loan Status not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<LoanStatusResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // 🔹 GET ACTIVE
    @Override
    @Transactional(readOnly = true)
    public List<LoanStatusResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    // 🔹 DELETE (SOFT DELETE)
    @Override
    public void delete(Long id) {

        LoanStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanStatus",
                                id.toString()
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}