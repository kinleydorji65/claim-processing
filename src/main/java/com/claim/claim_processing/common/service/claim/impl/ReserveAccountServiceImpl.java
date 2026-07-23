package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.mapper.claim.ReserveAccountMapper;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.service.claim.ReserveAccountService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveAccountServiceImpl implements ReserveAccountService {

    private final ReserveAccountRepository repository;
    private final ReserveAccountMapper mapper;

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    @Transactional
    public ApiResponseDTO<ReserveAccountResponseDto> create(
            ReserveAccountRequestDto dto
    ) {

        try {
            log.info("Creating Reserve Account for member: {}", dto.getMemberCode());

            // Check if reserve account already exists for this member
            if (dto.getIdentityNumber() != null) {
                if (repository.existsByIdentityNumber(
                        dto.getIdentityNumber())) {
                    throw ClaimException.conflict(
                            "Reserve Account already exists for this member"
                    );
                }
            }

            ReserveAccount entity = mapper.toEntity(dto);

            // Set default values
            if (entity.getIsActive() == null) {
                entity.setIsActive("Y");
            }
            if (entity.getStatus() == null) {
                entity.setStatus("ACTIVE");
            }
            if (entity.getTotalAmount() == null) {
                entity.setTotalAmount(BigDecimal.ZERO);
            }
            if (entity.getForfeitedAmount() == null) {
                entity.setForfeitedAmount(BigDecimal.ZERO);
            }

            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            ReserveAccount savedEntity = repository.save(entity);

            log.info("Reserve Account created successfully with ID: {}", savedEntity.getId());

            return ApiResponseDTO.success(
                    "Reserve Account created successfully",
                    mapper.toResponseDto(savedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to create Reserve Account", ex);
            throw ClaimException.internalError(
                    "Failed to create Reserve Account: " + ex.getMessage(),
                    ex
            );
        }
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @Override
    @Transactional
    public ApiResponseDTO<ReserveAccountResponseDto> update(
            Long id,
            ReserveAccountRequestDto dto
    ) {

        try {
            log.info("Updating Reserve Account with ID: {}", id);

            ReserveAccount entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Reserve Account",
                                    String.valueOf(id)
                            )
                    );

            // Update only allowed fields
            if (dto.getTotalAmount() != null) {
                entity.setTotalAmount(dto.getTotalAmount());
            }
            if (dto.getForfeitedAmount() != null) {
                entity.setForfeitedAmount(dto.getForfeitedAmount());
            }
            if (dto.getComponentCode() != null) {
                entity.setComponentCode(dto.getComponentCode());
            }
                entity.setStatus("Y");

            entity.setUpdatedAt(LocalDateTime.now());

            ReserveAccount updatedEntity = repository.save(entity);

            log.info("Reserve Account updated successfully with ID: {}", updatedEntity.getId());

            return ApiResponseDTO.success(
                    "Reserve Account updated successfully",
                    mapper.toResponseDto(updatedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to update Reserve Account", ex);
            throw ClaimException.internalError(
                    "Failed to update Reserve Account: " + ex.getMessage(),
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
            log.info("Fetching Reserve Account with ID: {}", id);

            ReserveAccount entity = repository.findById(id)
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
            log.error("Failed to fetch Reserve Account", ex);
            throw ClaimException.internalError(
                    "Failed to fetch Reserve Account: " + ex.getMessage(),
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
            log.info("Fetching all Reserve Accounts");

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
            log.error("Failed to fetch Reserve Accounts", ex);
            throw ClaimException.internalError(
                    "Failed to fetch Reserve Accounts: " + ex.getMessage(),
                    ex
            );
        }
    }

    // -------------------------------
    // DELETE
    // -------------------------------
    @Override
    @Transactional
    public ApiResponseDTO<String> delete(Long id) {

        try {
            log.info("Deleting Reserve Account with ID: {}", id);

            ReserveAccount entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Reserve Account",
                                    String.valueOf(id)
                            )
                    );

            // Soft delete - set isActive to 'N'
            entity.setIsActive("N");
            entity.setUpdatedAt(LocalDateTime.now());
            repository.save(entity);

            log.info("Reserve Account deleted successfully with ID: {}", id);

            return ApiResponseDTO.success(
                    "Reserve Account deleted successfully",
                    "Deleted successfully"
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to delete Reserve Account", ex);
            throw ClaimException.internalError(
                    "Failed to delete Reserve Account: " + ex.getMessage(),
                    ex
            );
        }
    }

    // -------------------------------
    // GET BY MEMBER
    // -------------------------------
    @Override
    public ApiResponseDTO<List<ReserveAccountResponseDto>> getByNppfNumber(
            String nppfNumber
    ) {

        try {
            log.info("Fetching Reserve Accounts for NPPF: {}", nppfNumber);

            List<ReserveAccountResponseDto> response =
                    repository.findByNppfNumber(nppfNumber)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            if (response.isEmpty()) {
                throw ClaimException.notFound(
                        "No Reserve Accounts found for NPPF: " + nppfNumber
                );
            }

            return ApiResponseDTO.success(
                    "Reserve Accounts fetched successfully",
                    response
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to fetch Reserve Accounts by NPPF", ex);
            throw ClaimException.internalError(
                    "Failed to fetch Reserve Accounts: " + ex.getMessage(),
                    ex
            );
        }
    }

    // -------------------------------
    // GET BY IDENTITY NUMBER
    // -------------------------------
    @Override
    public ApiResponseDTO<List<ReserveAccountResponseDto>> getByIdentityNumber(
            String identityNumber
    ) {

        try {
            log.info("Fetching Reserve Accounts for Identity: {}", identityNumber);

            List<ReserveAccountResponseDto> response =
                    repository.findByIdentityNumber(identityNumber)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            if (response.isEmpty()) {
                throw ClaimException.notFound(
                        "No Reserve Accounts found for Identity: " + identityNumber
                );
            }

            return ApiResponseDTO.success(
                    "Reserve Accounts fetched successfully",
                    response
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to fetch Reserve Accounts by Identity", ex);
            throw ClaimException.internalError(
                    "Failed to fetch Reserve Accounts: " + ex.getMessage(),
                    ex
            );
        }
    }

    // -------------------------------
    // GET BY STATUS
    // -------------------------------
    @Override
    public ApiResponseDTO<List<ReserveAccountResponseDto>> getByStatus(
            String status
    ) {

        try {
            log.info("Fetching Reserve Accounts with status: {}", status);

            List<ReserveAccountResponseDto> response =
                    repository.findByStatus(status)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            if (response.isEmpty()) {
                throw ClaimException.notFound(
                        "No Reserve Accounts found with status: " + status
                );
            }

            return ApiResponseDTO.success(
                    "Reserve Accounts fetched successfully",
                    response
            );

        } catch (ClaimException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to fetch Reserve Accounts by status", ex);
            throw ClaimException.internalError(
                    "Failed to fetch Reserve Accounts: " + ex.getMessage(),
                    ex
            );
        }
    }
}