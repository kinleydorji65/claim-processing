package com.claim.claim_processing.common.service.impl.beneficary;

import com.claim.claim_processing.common.entities.beneficiary_master.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.DTO.response.beneficiary.ClaimantTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.beneficiary.ClaimantTypeUpdateRequestDto;
import com.claim.claim_processing.common.DTO.request.beneficiary.ClaimantTypeCreateRequestDto;
import com.claim.claim_processing.common.repository.beneficiary.ClaimantTypeRepository;
import com.claim.claim_processing.common.mapper.beneficiary.ClaimantTypeMapper;
import com.claim.claim_processing.common.service.beneficiary.ClaimantTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimantTypeServiceImpl implements ClaimantTypeService {

    private final ClaimantTypeRepository claimantTypeRepository;
    private final ClaimantTypeMapper claimantTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClaimantTypeResponseDto> getAllActive() {
        List<ClaimantTypeMaster> claimantTypes =
                claimantTypeRepository.findByIsActiveOrderByDisplayOrderAsc(ActivityEnum.Y);

        return claimantTypeMapper.toResponseDtoList(claimantTypes);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimantTypeResponseDto getById(Long id) {
        ClaimantTypeMaster claimantType = claimantTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claimant type not found with id: " + id));

        return claimantTypeMapper.toResponseDto(claimantType);
    }

    @Override
    public ClaimantTypeResponseDto create(ClaimantTypeCreateRequestDto requestDto) {
        if (claimantTypeRepository.existsByCode(requestDto.getCode())) {
            throw new RuntimeException("Claimant type code already exists: " + requestDto.getCode());
        }

        ClaimantTypeMaster claimantType = claimantTypeMapper.toEntity(requestDto);
        claimantType.setCreatedBy("SYSTEM");

        ClaimantTypeMaster savedClaimantType = claimantTypeRepository.save(claimantType);
        return claimantTypeMapper.toResponseDto(savedClaimantType);
    }

    @Override
    public ClaimantTypeResponseDto update(Long id, ClaimantTypeUpdateRequestDto requestDto) {
        ClaimantTypeMaster existingClaimantType = claimantTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claimant type not found with id: " + id));

        claimantTypeMapper.updateEntityFromDto(requestDto, existingClaimantType);
        existingClaimantType.setUpdatedBy("SYSTEM");

        ClaimantTypeMaster updatedClaimantType = claimantTypeRepository.save(existingClaimantType);
        return claimantTypeMapper.toResponseDto(updatedClaimantType);
    }

    @Override
    public void deactivate(Long id) {
        ClaimantTypeMaster existingClaimantType = claimantTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claimant type not found with id: " + id));

        existingClaimantType.setIsActive(ActivityEnum.N);
        existingClaimantType.setUpdatedBy("SYSTEM");

        claimantTypeRepository.save(existingClaimantType);
    }
}