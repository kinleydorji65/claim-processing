package com.claim.claim_processing.common.DTO.response.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentTypeDTO {
    private Long employmentTypeId;
    private String employmentTypeName;
}