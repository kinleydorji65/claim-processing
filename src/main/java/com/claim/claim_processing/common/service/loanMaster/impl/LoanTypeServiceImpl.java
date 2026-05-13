package com.claim.claim_processing.common.service.loanMaster.impl;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanTypeResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.loanMaster.LoanTypeMaster;
import com.claim.claim_processing.common.mapper.loanMaster.LoanTypeMapper;
import com.claim.claim_processing.common.repository.loanMaster.LoanTypeRepository;
import com.claim.claim_processing.common.service.loanMaster.LoanTypeService;
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
public class LoanTypeServiceImpl implements LoanTypeService {

    private final LoanTypeRepository repository;
    private final LoanTypeMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<LoanTypeResponseDto> create(LoanTypeRequestDto dto) {

        try {

            if (repository.existsByCode(dto.getCode())) {
                throw ClaimException.conflict(
                        "Loan Type already exists with code: " + dto.getCode()
                );
            }

            LoanTypeMaster entity = mapper.toEntity(dto);

            entity.setCreatedBy(dto.getCreatedBy());
            entity.setUpdatedBy(dto.getCreatedBy());

            entity.setIsActive(
                    dto.getIsActive() != null ? dto.getIsActive() : ActivityEnum.Y
            );

            LoanTypeMaster saved = repository.save(entity);

            return ApiResponseDTO.created(
                    mapper.toResponseDto(saved)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error creating LoanType", ex);
            throw ClaimException.internalError("Failed to create Loan Type", ex);
        }
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<LoanTypeResponseDto> update(Long id, LoanTypeRequestDto dto) {

        try {

            LoanTypeMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound("LoanType", id.toString())
                    );

            if (dto.getCode() != null &&
                    repository.existsByCodeAndIdNot(dto.getCode(), id)) {
                throw ClaimException.conflict(
                        "Loan Type already exists with code: " + dto.getCode()
                );
            }

            mapper.updateEntityFromDto(dto, entity);

            entity.setUpdatedBy(dto.getUpdatedBy());

            LoanTypeMaster updated = repository.save(entity);

            return ApiResponseDTO.success(
                    mapper.toResponseDto(updated)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error updating LoanType", ex);
            throw ClaimException.internalError("Failed to update Loan Type", ex);
        }
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<LoanTypeResponseDto> getById(Long id) {

        LoanTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("LoanType", id.toString())
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
    public ApiResponseDTO<LoanTypeResponseDto> getByCode(String code) {

        LoanTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Loan Type not found with code: " + code)
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
    public ApiResponseDTO<List<LoanTypeResponseDto>> getAll() {

        return ApiResponseDTO.success(
                mapper.toResponseDtoList(repository.findAll())
        );
    }

    // -----------------------------
    // GET ACTIVE
    // -----------------------------
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<List<LoanTypeResponseDto>> getAllActive() {

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

            LoanTypeMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound("LoanType", id.toString())
                    );

            entity.setIsActive(ActivityEnum.N);

            repository.save(entity);

            return ApiResponseDTO.success(
                    "Loan Type deactivated successfully",
                    null
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("Error deleting LoanType", ex);
            throw ClaimException.internalError("Failed to delete Loan Type", ex);
        }
    }
}