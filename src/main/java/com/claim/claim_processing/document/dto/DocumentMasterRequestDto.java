package com.claim.claim_processing.document.dto;

import lombok.*;

@Data
@Getter
@Setter
public class DocumentMasterRequestDto {

    private String userType;
    private Boolean isApproved;

    private String referenceId;
    private String serviceCode;

    private String createdBy;
}
