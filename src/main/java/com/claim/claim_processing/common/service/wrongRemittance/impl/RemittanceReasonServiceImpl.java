package com.claim.claim_processing.common.service.wrongRemittance.impl;

import com.claim.claim_processing.common.DTO.request.wrong_remittance.RemittanceReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.wrong_remittance.RemittanceReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.wrong_remittance.RemittanceReasonUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.wrong_remittance_master.WrongRemittanceReasonMaster;
import com.claim.claim_processing.common.mapper.wrong_remittance.RemittanceReasonMapper;
import com.claim.claim_processing.common.repository.wrong_remittance.RemittanceReasonRepository;
import com.claim.claim_processing.common.service.wrongRemittance.RemittanceReasonService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemittanceReasonServiceImpl implements RemittanceReasonService {



    private final RemittanceReasonRepository repository;
    private final RemittanceReasonMapper mapper;

    @Override
    public RemittanceReasonResponseDto create(RemittanceReasonRequestDto requestDto) {
        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Remittance reason code already exists: " + requestDto.getCode()
            );
        }

        WrongRemittanceReasonMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        WrongRemittanceReasonMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public RemittanceReasonResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public RemittanceReasonResponseDto getByCode(String code) {
        WrongRemittanceReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Remittance reason not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    public List<RemittanceReasonResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<RemittanceReasonResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActiveOrderByDisplayOrderAscNameAsc(ActivityEnum.Y)
        );
    }

    @Override
    public RemittanceReasonResponseDto update(Long id, RemittanceReasonUpdateDto updateDto) {
        WrongRemittanceReasonMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        WrongRemittanceReasonMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void deactivate(Long id) {
        WrongRemittanceReasonMaster entity = findById(id);

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }

    private WrongRemittanceReasonMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Remittance reason not found with id: " + id)
                );
    }
}