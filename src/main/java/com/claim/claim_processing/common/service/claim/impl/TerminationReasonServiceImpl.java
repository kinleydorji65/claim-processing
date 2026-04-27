package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.entities.claim.TerminationReasonMaster;
import com.claim.claim_processing.common.DTO.request.claim.TerminationReasonCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.TerminationReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.TerminationReasonUpdateRequestDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.TerminationReasonMapper;
import com.claim.claim_processing.common.repository.claim.TerminationReasonRepository;
import com.claim.claim_processing.common.service.claim.TerminationReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TerminationReasonServiceImpl implements TerminationReasonService {

    private final TerminationReasonRepository terminationReasonRepository;
    private final TerminationReasonMapper terminationReasonMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TerminationReasonResponseDto> getAllActive() {
        List<TerminationReasonMaster> reasons =
                terminationReasonRepository.findAllByOrderByIsActiveAsc(ActivityEnum.Y);

        return terminationReasonMapper.toResponseDtoList(reasons);
    }

    @Override
    @Transactional(readOnly = true)
    public TerminationReasonResponseDto getById(Long id) {
        TerminationReasonMaster reason = terminationReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Termination reason not found with id: " + id));

        return terminationReasonMapper.toResponseDto(reason);
    }

    @Override
    public TerminationReasonResponseDto create(TerminationReasonCreateRequestDto requestDto) {
        if (terminationReasonRepository.existsByCode(requestDto.getCode())) {
            throw new RuntimeException("Termination reason code already exists: " + requestDto.getCode());
        }

        TerminationReasonMaster reason = terminationReasonMapper.toEntity(requestDto);
        reason.setCreatedBy("SYSTEM");

        TerminationReasonMaster savedReason = terminationReasonRepository.save(reason);
        return terminationReasonMapper.toResponseDto(savedReason);
    }

    @Override
    public TerminationReasonResponseDto update(Long id, TerminationReasonUpdateRequestDto requestDto) {
        TerminationReasonMaster existingReason = terminationReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Termination reason not found with id: " + id));

        terminationReasonMapper.updateEntityFromDto(requestDto, existingReason);
        existingReason.setUpdatedBy("SYSTEM");

        TerminationReasonMaster updatedReason = terminationReasonRepository.save(existingReason);
        return terminationReasonMapper.toResponseDto(updatedReason);
    }

    @Override
    public void deactivate(Long id) {
        TerminationReasonMaster existingReason = terminationReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Termination reason not found with id: " + id));

        existingReason.setIsActive(ActivityEnum.N);
        existingReason.setUpdatedBy("SYSTEM");

        terminationReasonRepository.save(existingReason);
    }
}
