package com.claim.claim_processing.common.service.loanMaster.impl;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanAdjustmentPriorityRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LoanAdjustmentPriorityServiceImpl implements LoanAdjustmentPriorityService {

    private final LoanAdjustmentPriorityRepository repository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanAdjustmentPriorityMapper mapper;

    // ---------------- CREATE ----------------
    @Override
    public ApiResponseDTO<LoanAdjustmentPriorityResponseDto> create(
            LoanAdjustmentPriorityRequestDto dto
    ) {
        try {

            LoanTypeMaster loanType = getLoanType(dto.getLoanTypeId());

            if (repository.existsByLoanType(loanType)) {
                throw ClaimException.conflict(
                        "Priority already configured for loan type: " + dto.getLoanTypeId()
                );
            }

            LoanAdjustmentPriorityMaster entity = mapper.toEntity(dto);
            entity.setLoanType(loanType);
            entity.setCreatedBy(dto.getCreatedBy());
            entity.setUpdatedBy(dto.getUpdatedBy());

            if (dto.getIsActive() == null) {
                entity.setIsActive(ActivityEnum.Y);
            }

            LoanAdjustmentPriorityMaster saved = repository.save(entity);

            return ApiResponseDTO.created(mapper.toResponseDto(saved));

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating LoanAdjustmentPriority", ex);
            throw ClaimException.internalError("Failed to create Loan Adjustment Priority", ex);
        }
    }

    // ---------------- UPDATE ----------------
    @Override
    public ApiResponseDTO<LoanAdjustmentPriorityResponseDto> update(
            Long id,
            LoanAdjustmentPriorityRequestDto dto
    ) {
        try {

            LoanAdjustmentPriorityMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "LoanAdjustmentPriority",
                                    id.toString()
                            )
                    );

            mapper.updateEntityFromDto(dto, entity);

            if (dto.getLoanTypeId() != null) {

                LoanTypeMaster loanType = getLoanType(dto.getLoanTypeId());

                if (repository.existsByLoanTypeAndIdNot(loanType, id)) {
                    throw ClaimException.conflict(
                            "Priority already configured for loan type: " + dto.getLoanTypeId()
                    );
                }

                entity.setLoanType(loanType);
            }

            LoanAdjustmentPriorityMaster updated = repository.save(entity);

            return ApiResponseDTO.success(
                    "Updated successfully",
                    mapper.toResponseDto(updated)
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating LoanAdjustmentPriority", ex);
            throw ClaimException.internalError("Failed to update Loan Adjustment Priority", ex);
        }
    }

    // ---------------- GET BY ID ----------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<LoanAdjustmentPriorityResponseDto> getById(Long id) {

        LoanAdjustmentPriorityMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanAdjustmentPriority",
                                id.toString()
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ---------------- GET ALL ----------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<LoanAdjustmentPriorityResponseDto>> getAll() {

        List<LoanAdjustmentPriorityResponseDto> list =
                mapper.toResponseDtoList(repository.findAll());

        return ApiResponseDTO.success(list);
    }

    // ---------------- GET ACTIVE ----------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<LoanAdjustmentPriorityResponseDto>> getAllActive() {

        List<LoanAdjustmentPriorityResponseDto> list =
                mapper.toResponseDtoList(repository.findByIsActive(ActivityEnum.Y));

        return ApiResponseDTO.success(list);
    }

    // ---------------- GET BY LOAN TYPE ----------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<LoanAdjustmentPriorityResponseDto>> getByLoanTypeId(Long loanTypeId) {

        // 1. Validate FK exists
        LoanTypeMaster loanType = loanTypeRepository.findById(loanTypeId)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanType",
                                String.valueOf(loanTypeId)
                        )
                );

        // 2. Fetch mappings
        List<LoanAdjustmentPriorityMaster> list =
                repository.findByLoanType(loanType);

        // 3. Business rule validation (THIS IS YOUR BLOCK)
        if (list.isEmpty()) {
            throw ClaimException.notFound(
                    "No Loan Adjustment Priority configured for Loan Type: " + loanTypeId
            );
        }

        // 4. Return response
        return ApiResponseDTO.success(
                mapper.toResponseDtoList(list)
        );
    }

    // ---------------- DELETE (SOFT) ----------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        try {

            LoanAdjustmentPriorityMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "LoanAdjustmentPriority",
                                    id.toString()
                            )
                    );

            entity.setIsActive(ActivityEnum.N);
            repository.save(entity);

            return ApiResponseDTO.success("Deactivated successfully", null);

        } catch (Exception ex) {
            log.error("Error deleting LoanAdjustmentPriority", ex);
            throw ClaimException.internalError("Failed to delete Loan Adjustment Priority", ex);
        }
    }

    // ---------------- HELPER ----------------
    private LoanTypeMaster getLoanType(Long id) {
        return loanTypeRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("LoanType", id.toString())
                );
    }
}