package com.claim.claim_processing.common.service.contribution.impl;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.DTO.request.contribution.SchemeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.contribution.SchemeUpdateRequestDto;
import com.claim.claim_processing.common.mapper.contribution.SchemeMapper;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.service.contribution.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SchemeServiceImpl implements SchemeService {

    private final SchemeTypeRepository schemeRepository;
    private final SchemeMapper schemeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SchemeTypeResponseDto> getAllActive() {
        List<SchemeMaster> schemes = schemeRepository.findByIsActiveOrderByNameAsc(ActivityEnum.Y);
        return schemeMapper.toResponseDtoList(schemes);
    }

    @Override
    @Transactional(readOnly = true)
    public SchemeTypeResponseDto getById(Long id) {
        SchemeMaster scheme = schemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scheme not found with id: " + id));

        return schemeMapper.toResponseDto(scheme);
    }

    @Override
    public SchemeTypeResponseDto create(SchemeCreateRequestDto requestDto) {
        if (schemeRepository.existsByCode(requestDto.getCode())) {
            throw new RuntimeException("Scheme code already exists: " + requestDto.getCode());
        }

        SchemeMaster scheme = schemeMapper.toEntity(requestDto);
        scheme.setCreatedBy("SYSTEM");

        SchemeMaster savedScheme = schemeRepository.save(scheme);
        return schemeMapper.toResponseDto(savedScheme);
    }

    @Override
    public SchemeTypeResponseDto update(Long id, SchemeUpdateRequestDto requestDto) {
        SchemeMaster existingScheme = schemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scheme not found with id: " + id));

        schemeMapper.updateEntityFromDto(requestDto, existingScheme);
        existingScheme.setUpdatedBy("SYSTEM");

        SchemeMaster updatedScheme = schemeRepository.save(existingScheme);
        return schemeMapper.toResponseDto(updatedScheme);
    }

    @Override
    public void deactivate(Long id) {
        SchemeMaster existingScheme = schemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scheme not found with id: " + id));

        existingScheme.setIsActive(ActivityEnum.N);
        existingScheme.setUpdatedBy("SYSTEM");

        schemeRepository.save(existingScheme);
    }
}
