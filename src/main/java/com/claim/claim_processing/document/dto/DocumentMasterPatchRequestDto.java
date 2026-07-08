package com.claim.claim_processing.document.dto;


import lombok.Data;

import java.util.List;

@Data
public class DocumentMasterPatchRequestDto {
    private List<DocumentMasterUpdateDto> requests;
}

