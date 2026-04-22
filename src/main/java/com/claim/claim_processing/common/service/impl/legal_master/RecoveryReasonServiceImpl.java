package com.claim.claim_processing.common.service.impl.legal_master;

import com.claim.claim_processing.common.DTO.request.legal_master.RecoveryReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.legal_master.RecoveryReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.legal_master.RecoveryReasonUpdateDto;
import com.claim.claim_processing.common.entities.legal_master.RecoveryReasonMaster;
import com.claim.claim_processing.common.mapper.legal_master.RecoveryReasonMapper;
import com.claim.claim_processing.common.repository.legal_master.RecoveryReasonRepository;
import com.claim.claim_processing.common.service.legal_master.RecoveryReasonService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecoveryReasonServiceImpl implements RecoveryReasonService {

    private static final String ACTIVE = "Y";
    private static final String INACTIVE = "N";

    private final RecoveryReasonRepository repository;
    private final RecoveryReasonMapper mapper;

    @Override
    public RecoveryReasonResponseDto create(RecoveryReasonRequestDto requestDto) {
        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Recovery reason code already exists: " + requestDto.getCode()
            );
        }

        RecoveryReasonMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        RecoveryReasonMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public RecoveryReasonResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public RecoveryReasonResponseDto getByCode(String code) {
        RecoveryReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Recovery reason not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    public List<RecoveryReasonResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<RecoveryReasonResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActiveOrderByDisplayOrderAscNameAsc(ACTIVE)
        );
    }

    @Override
    public RecoveryReasonResponseDto update(Long id, RecoveryReasonUpdateDto updateDto) {
        RecoveryReasonMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        RecoveryReasonMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {
        RecoveryReasonMaster entity = findById(id);

        entity.setIsActive(INACTIVE);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }

    private RecoveryReasonMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Recovery reason not found with id: " + id)
                );
    }
}