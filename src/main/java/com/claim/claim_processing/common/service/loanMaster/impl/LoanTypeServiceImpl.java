package com.claim.claim_processing.common.service.loanMaster.impl;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanTypeResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.loanMaster.LoanTypeMaster;
import com.claim.claim_processing.common.mapper.loanMaster.LoanTypeMapper;
import com.claim.claim_processing.common.repository.loanMaster.LoanTypeRepository;
import com.claim.claim_processing.common.service.loanMaster.LoanTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanTypeServiceImpl implements LoanTypeService {

    private final LoanTypeRepository repository;
    private final LoanTypeMapper mapper;

    // 🔹 CREATE
    @Override
    public LoanTypeResponseDto create(LoanTypeRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Loan Type already exists with code: " + dto.getCode()
            );
        }

        LoanTypeMaster entity = mapper.toEntity(dto);

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
    public LoanTypeResponseDto update(Long id, LoanTypeRequestDto dto) {

        LoanTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("LoanType", id.toString())
                );

        mapper.updateEntityFromDto(dto, entity);

        entity.setUpdatedBy(dto.getUpdatedBy());

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 GET BY ID
    @Override
    @Transactional(readOnly = true)
    public LoanTypeResponseDto getById(Long id) {

        LoanTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("LoanType", id.toString())
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET BY CODE
    @Override
    @Transactional(readOnly = true)
    public LoanTypeResponseDto getByCode(String code) {

        LoanTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Loan Type not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<LoanTypeResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // 🔹 GET ACTIVE
    @Override
    @Transactional(readOnly = true)
    public List<LoanTypeResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    // 🔹 DELETE (SOFT DELETE)
    @Override
    public void delete(Long id) {

        LoanTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("LoanType", id.toString())
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}