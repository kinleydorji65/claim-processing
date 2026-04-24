package com.claim.claim_processing.common.service.impl.common;

import com.claim.claim_processing.common.DTO.request.common.SubmissionChannelRequestDto;
import com.claim.claim_processing.common.DTO.response.common.SubmissionChannelResponseDto;
import com.claim.claim_processing.common.DTO.update.common.SubmissionChannelUpdateDto;
import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.SubmissionChannelMapper;
import com.claim.claim_processing.common.repository.common.SubmissionChannelRepository;
import com.claim.claim_processing.common.service.common.SubmissionChannelService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionChannelServiceImpl implements SubmissionChannelService {

    private final SubmissionChannelRepository repository;
    private final SubmissionChannelMapper mapper;

    @Override
    public SubmissionChannelResponseDto create(SubmissionChannelRequestDto requestDto) {
        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Submission channel code already exists: " + requestDto.getCode()
            );
        }

        SubmissionChannelMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        SubmissionChannelMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public SubmissionChannelResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public SubmissionChannelResponseDto getByCode(String code) {
        SubmissionChannelMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Submission channel not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    public List<SubmissionChannelResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<SubmissionChannelResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActiveOrderByNameAsc(ActivityEnum.Y)
        );
    }

    @Override
    public SubmissionChannelResponseDto update(Long id, SubmissionChannelUpdateDto updateDto) {
        SubmissionChannelMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        SubmissionChannelMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void deactivate(Long id) {
        SubmissionChannelMaster entity = findById(id);

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }

    private SubmissionChannelMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Submission channel not found with id: " + id)
                );
    }
}