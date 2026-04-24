package com.claim.claim_processing.common.DTO.update.agency;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyCategoryUpdateDto {

    private String agencyCategoryCode;

    private String categoryName;

    private String status;
}