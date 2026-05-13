package com.claim.claim_processing.common.service.loanMaster.impl;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.loanMaster.LoanStatusMaster;
import com.claim.claim_processing.common.mapper.loanMaster.LoanStatusMapper;
import com.claim.claim_processing.common.repository.loanMaster.LoanStatusRepository;
import com.claim.claim_processing.common.service.loanMaster.LoanStatusService;
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
public class LoanStatusServiceImpl implements LoanStatusService {

    private final LoanStatusRepository repository;
    private final LoanStatusMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<LoanStatusResponseDto> create(LoanStatusRequestDto dto) {

        try {

            if (repository.existsByCode(dto.getCode())) {
                throw ClaimException.conflict(
                        "Loan Status already exists with code: " + dto.getCode()
                );
            }

            LoanStatusMaster entity = mapper.toEntity(dto);

            entity.setCreatedBy(dto.getCreatedBy());
            entity.setUpdatedBy(dto.getCreatedBy());

            entity.setIsActive(
                    dto.getIsActive() != null ? dto.getIsActive() : ActivityEnum.Y
            );

            if (dto.getDisplayOrder() != null) {
                entity.setDisplayOrder(dto.getDisplayOrder());
            }

            LoanStatusMaster saved = repository.save(entity);

            return ApiResponseDTO.created(
                    mapper.toResponseDto(saved)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error creating LoanStatus", ex);
            throw ClaimException.internalError("Failed to create Loan Status", ex);
        }
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<LoanStatusResponseDto> update(Long id, LoanStatusRequestDto dto) {

        try {

            LoanStatusMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "LoanStatus",
                                    id.toString()
                            )
                    );

            if (dto.getCode() != null &&
                    repository.existsByCodeAndIdNot(dto.getCode(), id)) {
                throw ClaimException.conflict(
                        "Loan Status already exists with code: " + dto.getCode()
                );
            }

            mapper.updateEntityFromDto(dto, entity);

            entity.setUpdatedBy(dto.getUpdatedBy());

            LoanStatusMaster updated = repository.save(entity);

            return ApiResponseDTO.success(
                    mapper.toResponseDto(updated)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error updating LoanStatus", ex);
            throw ClaimException.internalError("Failed to update Loan Status", ex);
        }
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<LoanStatusResponseDto> getById(Long id) {

        LoanStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "LoanStatus",
                                id.toString()
                        )
                );

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<LoanStatusResponseDto> getByCode(String code) {

        LoanStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Loan Status not found with code: " + code
                        )
                );

        return ApiResponseDTO.success(
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<LoanStatusResponseDto>> getAll() {

        return ApiResponseDTO.success(
                mapper.toResponseDtoList(repository.findAll())
        );
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<LoanStatusResponseDto>> getAllActive() {

        return ApiResponseDTO.success(
                mapper.toResponseDtoList(
                        repository.findByIsActive(ActivityEnum.Y)
                )
        );
    }

    // -----------------------------
    // DELETE (SOFT DELETE)
    // -----------------------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        try {

            LoanStatusMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "LoanStatus",
                                    id.toString()
                            )
                    );

            entity.setIsActive(ActivityEnum.N);

            repository.save(entity);

            return ApiResponseDTO.success(
                    "Loan Status deactivated successfully",
                    null
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error deleting LoanStatus", ex);
            throw ClaimException.internalError("Failed to delete Loan Status", ex);
        }
    }
}