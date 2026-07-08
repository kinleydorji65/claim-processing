package com.claim.claim_processing.document.mapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.document.dto.DocumentMasterResponseDto;
import com.claim.claim_processing.document.dto.DocumentMasterResponseDto.DocumentFileDto;
import com.claim.claim_processing.document.entity.DocumentMaster;

@Component
public class DocumentMasterMapper {

    /**
     * Convert multiple DocumentMaster entities (one per file)
     * into a single response DTO with file list
     */
    public DocumentMasterResponseDto toDto(List<DocumentMaster> entities) {

        if (entities == null || entities.isEmpty()) {
            return DocumentMasterResponseDto.builder()
                    .files(Collections.emptyList())
                    .build();
        }

        // Choose latest created record as header
        DocumentMaster head = entities.stream()
                .max(Comparator.comparing(e ->
                        e.getCreatedAt() == null ? LocalDateTime.MIN : e.getCreatedAt()
                ))
                .orElse(entities.get(0));

        List<DocumentFileDto> files = entities.stream()
                .map(this::toFileDto)
                .collect(Collectors.toList());

        return DocumentMasterResponseDto.builder()
                // .documentId(head.getDocumentId())
                .referenceId(head.getReferenceId())
                .serviceCode(head.getServiceCode())
                .files(files)
                .createdBy(head.getCreatedBy())
                .createdAt(head.getCreatedAt())
                .updatedBy(head.getUpdatedBy())
                .updatedAt(head.getUpdatedAt())
                .build();
    }

    /**
     * Convert single entity to response
     */
    public DocumentMasterResponseDto toDto(DocumentMaster entity) {

        if (entity == null) {
            return null;
        }

        return DocumentMasterResponseDto.builder()
                .referenceId(entity.getReferenceId())
                .serviceCode(entity.getServiceCode())
                .files(List.of(toFileDto(entity)))
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert entity → file DTO
     */
    private DocumentFileDto toFileDto(DocumentMaster entity) {

        if (entity == null) {
            return null;
        }

        return DocumentFileDto.builder()
                .documentId(entity.getDocumentId())
                .documentName(entity.getDocumentName())
                .fileType(entity.getFileType())
                .filePath(entity.getFilePath())
                .build();
    }
}

