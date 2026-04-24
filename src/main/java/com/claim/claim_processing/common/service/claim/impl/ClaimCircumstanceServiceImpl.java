package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import com.claim.claim_processing.common.DTO.request.claim.ClaimCircumstanceCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimCircumstanceUpdateRequestDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.ClaimCircumstanceMapper;
import com.claim.claim_processing.common.repository.claim.ClaimCircumstanceRepository;
import com.claim.claim_processing.common.service.claim.ClaimCircumstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimCircumstanceServiceImpl implements ClaimCircumstanceService {

    private final ClaimCircumstanceRepository repository;
    private final ClaimCircumstanceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClaimCircumstanceResponseDto> getAllActive() {
        List<ClaimCircumstanceMaster> list =
                repository.findByIsActiveOrderByNameAsc(ActivityEnum.Y);

        return mapper.toResponseDtoList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimCircumstanceResponseDto getById(Long id) {
        ClaimCircumstanceMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim circumstance not found with id: " + id));

        return mapper.toResponseDto(entity);
    }

    @Override
    public ClaimCircumstanceResponseDto create(ClaimCircumstanceCreateRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw new RuntimeException("Claim circumstance code already exists: " + requestDto.getCode());
        }

        ClaimCircumstanceMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");

        ClaimCircumstanceMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public ClaimCircumstanceResponseDto update(Long id, ClaimCircumstanceUpdateRequestDto requestDto) {

        ClaimCircumstanceMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim circumstance not found with id: " + id));

        mapper.updateEntityFromDto(requestDto, entity);
        entity.setUpdatedBy("SYSTEM");

        ClaimCircumstanceMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void deactivate(Long id) {

        ClaimCircumstanceMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim circumstance not found with id: " + id));

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }
}