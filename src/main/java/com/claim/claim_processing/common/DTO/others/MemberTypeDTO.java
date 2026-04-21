package com.claim.claim_processing.common.DTO.others;

import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberTypeDTO {
    private Long memberTypeId;
    private String memberTypeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
