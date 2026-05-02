package com.claim.claim_processing.common.service.loanMaster.impl;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanAdjustmentPriorityRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanAdjustmentPriorityResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.loanMaster.LoanAdjustmentPriorityMaster;
import com.claim.claim_processing.common.entities.loanMaster.LoanTypeMaster;
import com.claim.claim_processing.common.mapper.loanMaster.LoanAdjustmentPriorityMapper;
import com.claim.claim_processing.common.repository.loanMaster.LoanAdjustmentPriorityRepository;
import com.claim.claim_processing.common.repository.loanMaster.LoanTypeRepository;
import com.claim.claim_processing.common.service.loanMaster.LoanAdjustmentPriorityService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanAdjustmentPriorityServiceImpl
        implements LoanAdjustmentPriorityService {

    private final LoanAdjustmentPriorityRepository repository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanAdjustmentPriorityMapper mapper;

    // 🔹 CREATE
    @Override
    public LoanAdjustmentPriorityResponseDto create(
            LoanAdjustmentPriorityRequestDto dto
    ) {

        LoanTypeMaster loanType = loanTypeRepository.findById(dto.getLoanTypeId())
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanType",
                                dto.getLoanTypeId().toString()
                        )
                );

        // one config per loan type
        if (repository.existsByLoanType(loanType)) {
            throw ClaimException.conflict(
                    "Priority already configured for loan type id: "
                            + dto.getLoanTypeId()
            );
        }

        LoanAdjustmentPriorityMaster entity = mapper.toEntity(dto);

        entity.setLoanType(loanType);
        entity.setCreatedBy(dto.getCreatedBy());

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 UPDATE
    @Override
    public LoanAdjustmentPriorityResponseDto update(
            Long id,
            LoanAdjustmentPriorityRequestDto dto
    ) {

        LoanAdjustmentPriorityMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanAdjustmentPriority",
                                id.toString()
                        )
                );

        mapper.updateEntityFromDto(dto, entity);

        // FK update support
        if (dto.getLoanTypeId() != null) {

            LoanTypeMaster loanType = loanTypeRepository.findById(dto.getLoanTypeId())
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "LoanType",
                                    dto.getLoanTypeId().toString()
                            )
                    );

            if (repository.existsByLoanTypeAndIdNot(loanType, id)) {
                throw ClaimException.conflict(
                        "Priority already configured for loan type id: "
                                + dto.getLoanTypeId()
                );
            }

            entity.setLoanType(loanType);
        }

        entity.setUpdatedBy(dto.getUpdatedBy());

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 GET BY ID
    @Override
    @Transactional(readOnly = true)
    public LoanAdjustmentPriorityResponseDto getById(Long id) {

        LoanAdjustmentPriorityMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanAdjustmentPriority",
                                id.toString()
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<LoanAdjustmentPriorityResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // 🔹 GET ACTIVE
    @Override
    @Transactional(readOnly = true)
    public List<LoanAdjustmentPriorityResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    // 🔹 GET BY FK ID
    @Override
    @Transactional(readOnly = true)
    public List<LoanAdjustmentPriorityResponseDto> getByLoanTypeId(
            Long loanTypeId
    ) {

        LoanTypeMaster loanType = loanTypeRepository.findById(loanTypeId)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanType",
                                loanTypeId.toString()
                        )
                );

        return mapper.toResponseDtoList(
                repository.findByLoanType(loanType)
        );
    }


    // 🔹 DELETE (SOFT DELETE)
    @Override
    public void delete(Long id) {

        LoanAdjustmentPriorityMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanAdjustmentPriority",
                                id.toString()
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}