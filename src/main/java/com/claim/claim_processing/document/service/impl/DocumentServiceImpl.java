package com.claim.claim_processing.document.service.impl;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.document.dto.DocumentTypeResponseDto;
import com.claim.claim_processing.document.entity.DocumentTypeMaster;
import com.claim.claim_processing.document.mapper.DocumentMapper;
import com.claim.claim_processing.document.repository.DocumentMasterMapRepository;
import com.claim.claim_processing.document.service.DocumentService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    // private final DocumentTypeMasterRepository documentTypeMasterRepository;
    private final DocumentMasterMapRepository documentMasterMapRepository;
    private final DocumentMapper documentMapper;

    @Override
    public ApiResponseDTO<List<DocumentTypeResponseDto>> generateClaimDocument(Long claimTypeId) {

        
        List<DocumentTypeMaster> entity = documentMasterMapRepository.findByClaimType_Id(claimTypeId)
                .stream()
                .map(map -> map.getDocument())
                .toList()
                .stream()
                .filter(doc -> doc.getIsActive().equals(ActivityEnum.Y))
                .toList();

        List<DocumentTypeResponseDto> response = entity.stream()
                .map(documentMapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(response);
    }
}
