package com.claim.claim_processing.document.mapper;

import com.claim.claim_processing.document.dto.DocumentTypeResponseDto;
import com.claim.claim_processing.document.entity.DocumentTypeMaster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    DocumentTypeResponseDto toResponseDto(DocumentTypeMaster entity);
}