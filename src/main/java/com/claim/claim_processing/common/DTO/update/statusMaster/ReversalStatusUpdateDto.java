package com.claim.claim_processing.common.DTO.update.statusMaster;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReversalStatusUpdateDto {

    private String name;
    private String description;
    private Integer displayOrder;
    private String isActive; // "Y" / "N"
}