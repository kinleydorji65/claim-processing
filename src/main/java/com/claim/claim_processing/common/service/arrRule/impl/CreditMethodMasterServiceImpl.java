package com.claim.claim_processing.common.service.arrRule.impl;

import com.claim.claim_processing.common.DTO.request.arrRule.CreditMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.arrRule.CreditMethodResponseDto;
import com.claim.claim_processing.common.entities.arrMaster.CreditMethodMaster;
import com.claim.claim_processing.common.mapper.arrRule.CreditMethodMasterMapper;
import com.claim.claim_processing.common.repository.arrRule.CreditMethodRepository;
import com.claim.claim_processing.common.service.arrRule.CreditMethodMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditMethodMasterServiceImpl implements CreditMethodMasterService {

    private final CreditMethodRepository repository;
    private final CreditMethodMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public CreditMethodResponseDto create(CreditMethodRequestDto request) {

        if (repository.existsByCode(request.getCode())) {
            throw ClaimException.conflict(
                    "Code already exists: " + request.getCode()
            );
        }

        CreditMethodMaster entity = mapper.toEntity(request);
        return mapper.toResponseDto(repository.save(entity));
    }

    // ================= UPDATE =================
    @Override
    public CreditMethodResponseDto update(Long id, CreditMethodRequestDto request) {

        CreditMethodMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("CreditMethodMaster", id.toString())
                );

        repository.findByCode(request.getCode())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw ClaimException.conflict(
                                "Code already exists: " + request.getCode()
                        );
                    }
                });

        mapper.updateEntity(request, entity);

        return mapper.toResponseDto(repository.save(entity));
    }

    // ================= GET BY ID =================
    @Override
    @Transactional(readOnly = true)
    public CreditMethodResponseDto getById(Long id) {

        CreditMethodMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("CreditMethodMaster", id.toString())
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY CODE =================
    @Override
    @Transactional(readOnly = true)
    public CreditMethodResponseDto getByCode(String code) {

        if (code == null || code.trim().isEmpty()) {
            throw ClaimException.badRequest("Code cannot be null or empty");
        }

        CreditMethodMaster entity = repository.findByCode(code.trim())
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("CreditMethodMaster", code)
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL ACTIVE =================
    @Override
    @Transactional(readOnly = true)
    public List<CreditMethodResponseDto> getAllActive() {

        List<CreditMethodMaster> list = repository.findAll()
                .stream()
                .filter(e -> e.getIsActive() != null && e.getIsActive().name().equals("Y"))
                .toList();

        if (list.isEmpty()) {
            throw ClaimException.notFound("No active records found");
        }

        return mapper.toResponseDtoList(list);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public void delete(Long id) {

        CreditMethodMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("CreditMethodMaster", id.toString())
                );

        entity.setIsActive(
                com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum.N
        );

        repository.save(entity);
    }
}