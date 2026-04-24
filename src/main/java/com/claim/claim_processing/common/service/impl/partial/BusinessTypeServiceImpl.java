package com.claim.claim_processing.common.service.impl.partial;

import com.claim.claim_processing.common.DTO.request.partial.BusinessTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.BusinessTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.BusinessTypeUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.BusinessTypeMaster;
import com.claim.claim_processing.common.mapper.partial.BusinessTypeMapper;
import com.claim.claim_processing.common.repository.partial.BusinessTypeRepository;
import com.claim.claim_processing.common.service.partial.BusinessTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessTypeServiceImpl implements BusinessTypeService {

    private final BusinessTypeRepository repository;
    private final BusinessTypeMapper mapper;

    @Override
    public BusinessTypeResponseDto create(BusinessTypeRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Business type code already exists: " + requestDto.getCode()
            );
        }

        BusinessTypeMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        BusinessTypeMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public BusinessTypeResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public BusinessTypeResponseDto getByCode(String code) {
        BusinessTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Business type not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    public List<BusinessTypeResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<BusinessTypeResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActiveOrderByDisplayOrderAscNameAsc(ActivityEnum.Y)
                repository.findByIsActiveOrderByDisplayOrderAscNameAsc(ActivityEnum.Y)
        );
    }

    @Override
    public BusinessTypeResponseDto update(Long id, BusinessTypeUpdateDto updateDto) {

        BusinessTypeMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        BusinessTypeMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {

        BusinessTypeMaster entity = findById(id);

        // Soft delete
        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }

    // 🔹 Common reusable method
    private BusinessTypeMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Business type not found with id: " + id)
                );
    }
}