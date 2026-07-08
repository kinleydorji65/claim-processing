package com.claim.claim_processing.document.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMasterUpdateDto {
    private String userType;
    private Boolean isApproved;
    private Long documentId;
    private String referenceId;
    private String serviceCode;
    private String updatedBy;
}
