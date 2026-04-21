package com.claim.claim_processing.common.DTO.others;

import lombok.Data;

@Data
public class BankTypeDTO {
    private Long bankTypeId;
    private String bankTypeName;
    private String bankShortDesc;
    private String updatedBy;
    private String status;
}
