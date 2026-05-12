package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.entities.claim.AccountTypeMaster;
import com.claim.claim_processing.common.entities.claim.ReserveAccountMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.ReserveAccountMapper;
import com.claim.claim_processing.common.repository.claim.AccountTypeRepository;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.service.claim.ReserveAccountService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReserveAccountServiceImpl implements ReserveAccountService {

    private final ReserveAccountRepository repository;
    private final ReserveAccountMapper mapper;

    private final AccountTypeRepository accountTypeRepository;
    private final SchemeTypeRepository schemeTypeRepository;

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    public ApiResponseDTO<ReserveAccountResponseDto> create(
            ReserveAccountRequestDto dto
    ) {

        try {

            if (repository.existsByReserveAccountCode(dto.getReserveAccountCode())) {
                throw ClaimException.conflict(
                        "Reserve Account code already exists: "
                                + dto.getReserveAccountCode()
                );
            }

            ReserveAccountMaster entity = mapper.toEntity(dto);

            entity.setAccountType(
                    getAccountType(dto.getAccountTypeId())
            );

            entity.setSchemeType(
                    getSchemeType(dto.getSchemeTypeId())
            );

            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            if (entity.getIsActive() == null) {
                entity.setIsActive(ActivityEnum.Y);
            }

            ReserveAccountMaster savedEntity = repository.save(entity);

            return ApiResponseDTO.success(
                    "Reserve Account created successfully",
                    mapper.toResponseDto(savedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ClaimException.internalError(
                    "Failed to create Reserve Account",
                    ex
            );
        }
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @Override
    public ApiResponseDTO<ReserveAccountResponseDto> update(
            Long id,
            ReserveAccountRequestDto dto
    ) {

        try {

            ReserveAccountMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Reserve Account",
                                    String.valueOf(id)
                            )
                    );

            mapper.updateEntityFromDto(dto, entity);

            entity.setAccountType(
                    getAccountType(dto.getAccountTypeId())
            );

            entity.setSchemeType(
                    getSchemeType(dto.getSchemeTypeId())
            );

            entity.setUpdatedAt(LocalDateTime.now());

            ReserveAccountMaster updatedEntity = repository.save(entity);

            return ApiResponseDTO.success(
                    "Reserve Account updated successfully",
                    mapper.toResponseDto(updatedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ClaimException.internalError(
                    "Failed to update Reserve Account",
                    ex
            );
        }
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @Override
    public ApiResponseDTO<ReserveAccountResponseDto> getById(
            Long id
    ) {

        try {

            ReserveAccountMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Reserve Account",
                                    String.valueOf(id)
                            )
                    );

            return ApiResponseDTO.success(
                    "Reserve Account fetched successfully",
                    mapper.toResponseDto(entity)
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ClaimException.internalError(
                    "Failed to fetch Reserve Account",
                    ex
            );
        }
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @Override
    public ApiResponseDTO<List<ReserveAccountResponseDto>> getAll() {

        try {

            List<ReserveAccountResponseDto> response =
                    repository.findAll()
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            if (response.isEmpty()) {
                throw ClaimException.notFound(
                        "No Reserve Accounts found"
                );
            }

            return ApiResponseDTO.success(
                    "Reserve Accounts fetched successfully",
                    response
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ClaimException.internalError(
                    "Failed to fetch Reserve Accounts",
                    ex
            );
        }
    }

    // -------------------------------
    // DELETE
    // -------------------------------
    @Override
    public ApiResponseDTO<String> delete(
            Long id
    ) {

        try {

            ReserveAccountMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Reserve Account",
                                    String.valueOf(id)
                            )
                    );

            entity.setIsActive(ActivityEnum.N);
            entity.setUpdatedAt(LocalDateTime.now());

            repository.save(entity);

            return ApiResponseDTO.success(
                    "Reserve Account deactivated successfully"
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ClaimException.internalError(
                    "Failed to deactivate Reserve Account",
                    ex
            );
        }
    }

    // -------------------------------
    // FILTER BY ACCOUNT TYPE
    // -------------------------------
    @Override
    public ApiResponseDTO<List<ReserveAccountResponseDto>> getByAccountTypeId(
            Long accountTypeId
    ) {

        try {

            List<ReserveAccountResponseDto> response =
                    repository.findByAccountType_Id(accountTypeId)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            if (response.isEmpty()) {
                throw ClaimException.notFound(
                        "No Reserve Accounts found for Account Type ID: "
                                + accountTypeId
                );
            }

            return ApiResponseDTO.success(
                    "Reserve Accounts fetched successfully",
                    response
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ClaimException.internalError(
                    "Failed to fetch Reserve Accounts by Account Type",
                    ex
            );
        }
    }

    // -------------------------------
    // FILTER BY SCHEME TYPE
    // -------------------------------
    @Override
    public ApiResponseDTO<List<ReserveAccountResponseDto>> getBySchemeTypeId(
            Long schemeTypeId
    ) {

        try {

            List<ReserveAccountResponseDto> response =
                    repository.findBySchemeType_Id(schemeTypeId)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            if (response.isEmpty()) {
                throw ClaimException.notFound(
                        "No Reserve Accounts found for Scheme Type ID: "
                                + schemeTypeId
                );
            }

            return ApiResponseDTO.success(
                    "Reserve Accounts fetched successfully",
                    response
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ClaimException.internalError(
                    "Failed to fetch Reserve Accounts by Scheme Type",
                    ex
            );
        }
    }

    // -------------------------------
    // HELPER METHODS
    // -------------------------------

    private AccountTypeMaster getAccountType(Long id) {

        return accountTypeRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Account Type",
                                String.valueOf(id)
                        )
                );
    }

    private SchemeMaster getSchemeType(Long id) {

        return schemeTypeRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Scheme Type",
                                String.valueOf(id)
                        )
                );
    }
}