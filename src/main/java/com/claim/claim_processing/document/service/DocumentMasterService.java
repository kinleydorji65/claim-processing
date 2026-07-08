package com.claim.claim_processing.document.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.claim.claim_processing.document.dto.DocumentMasterRequestDto;
import com.claim.claim_processing.document.dto.DocumentMasterResponseDto;
import com.claim.claim_processing.document.dto.DocumentMasterUpdateDto;

public interface DocumentMasterService {
    DocumentMasterResponseDto transferDocumentsForApproval(String oldReferenceId,String newReferenceId, String userType, String updatedBy);
    DocumentMasterResponseDto createDocument(DocumentMasterRequestDto request, List<MultipartFile> files);
    DocumentMasterResponseDto getByReferenceId(String referenceId);
    DocumentMasterResponseDto patchDocument(List<DocumentMasterUpdateDto> requests, List<MultipartFile> files);
    Resource getFileById(Long id);
    
}
