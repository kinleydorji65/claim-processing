package com.claim.claim_processing.common.DTO.response.others.agency.agencyRelated;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgencyCategoryDTO {
    private String categoryId;
    private String categoryName;
}
