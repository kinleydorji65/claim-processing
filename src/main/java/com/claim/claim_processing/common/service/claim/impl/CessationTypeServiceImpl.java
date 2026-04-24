package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.DTO.request.claim.CessationTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.CessationTypeUpdateRequestDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.CessationTypeMapper;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.common.service.claim.CessationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CessationTypeServiceImpl implements CessationTypeService {

    private final CessationTypeRepository cessationTypeRepository;
    private final CessationTypeMapper cessationTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CessationTypeResponseDto> getAllActive() {
        List<CessationTypeMaster> list =
                cessationTypeRepository.findByIsActiveOrderByNameAsc(ActivityEnum.Y);

        return cessationTypeMapper.toResponseDtoList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public CessationTypeResponseDto getById(Long id) {
        CessationTypeMaster entity = cessationTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cessation type not found with id: " + id));

        return cessationTypeMapper.toResponseDto(entity);
    }

    @Override
    public CessationTypeResponseDto create(CessationTypeCreateRequestDto requestDto) {

        if (cessationTypeRepository.existsByCode(requestDto.getCode())) {
            throw new RuntimeException("Cessation type code already exists: " + requestDto.getCode());
        }

        CessationTypeMaster entity = cessationTypeMapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");

        CessationTypeMaster saved = cessationTypeRepository.save(entity);
        return cessationTypeMapper.toResponseDto(saved);
    }

    @Override
    public CessationTypeResponseDto update(Long id, CessationTypeUpdateRequestDto requestDto) {

        CessationTypeMaster entity = cessationTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cessation type not found with id: " + id));

        cessationTypeMapper.updateEntityFromDto(requestDto, entity);
        entity.setUpdatedBy("SYSTEM");

        CessationTypeMaster updated = cessationTypeRepository.save(entity);
        return cessationTypeMapper.toResponseDto(updated);
    }

    @Override
    public void deactivate(Long id) {

        CessationTypeMaster entity = cessationTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cessation type not found with id: " + id));

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        cessationTypeRepository.save(entity);
    }
}