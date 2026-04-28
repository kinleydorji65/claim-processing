package com.claim.claim_processing.common.service.claim.impl;
import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.entities.claim.AccountTypeMaster;
import com.claim.claim_processing.common.entities.claim.ReserveAccountMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.mapper.claim.ReserveAccountMapper;
import com.claim.claim_processing.common.repository.claim.AccountTypeRepository;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.service.claim.ReserveAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReserveAccountServiceImpl implements ReserveAccountService {

    private final ReserveAccountRepository repository;
    private final AccountTypeRepository accountTypeRepository;
    private final SchemeTypeRepository schemeRepository;
    private final ReserveAccountMapper mapper;

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    public ReserveAccountResponseDto create(ReserveAccountRequestDto dto) {

        AccountTypeMaster accountType = accountTypeRepository.findById(dto.getAccountTypeId())
                .orElseThrow(() -> new RuntimeException("Account Type not found"));

        SchemeMaster scheme = schemeRepository.findById(dto.getSchemeTypeId())
                .orElseThrow(() -> new RuntimeException("Scheme Type not found"));

        ReserveAccountMaster entity = mapper.toEntity(dto);

        entity.setAccountType(accountType);
        entity.setSchemeType(scheme);

        return mapper.toResponseDto(repository.save(entity));
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @Override
    public ReserveAccountResponseDto update(Long id, ReserveAccountRequestDto dto) {

        ReserveAccountMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserve Account not found"));

        if (dto.getAccountTypeId() != null) {
            AccountTypeMaster accountType = accountTypeRepository.findById(dto.getAccountTypeId())
                    .orElseThrow(() -> new RuntimeException("Account Type not found"));
            entity.setAccountType(accountType);
        }

        if (dto.getSchemeTypeId() != null) {
            SchemeMaster scheme = schemeRepository.findById(dto.getSchemeTypeId())
                    .orElseThrow(() -> new RuntimeException("Scheme Type not found"));
            entity.setSchemeType(scheme);
        }

        mapper.updateEntityFromDto(dto, entity);

        return mapper.toResponseDto(repository.save(entity));
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @Override
    public ReserveAccountResponseDto getById(Long id) {
        ReserveAccountMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserve Account not found"));

        return mapper.toResponseDto(entity);
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @Override
    public List<ReserveAccountResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // -------------------------------
    // DELETE
    // -------------------------------
    @Override
    public void delete(Long id) {
        ReserveAccountMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserve Account not found"));

        repository.delete(entity);
    }

    // -------------------------------
    // FILTER BY ACCOUNT TYPE
    // -------------------------------
    @Override
    public List<ReserveAccountResponseDto> getByAccountTypeId(Long accountTypeId) {
        return mapper.toResponseDtoList(
                repository.findByAccountType_Id(accountTypeId)
        );
    }

    // -------------------------------
    // FILTER BY SCHEME TYPE
    // -------------------------------
    @Override
    public List<ReserveAccountResponseDto> getBySchemeTypeId(Long schemeTypeId) {
        return mapper.toResponseDtoList(
                repository.findBySchemeType_Id(schemeTypeId)
        );
    }
}