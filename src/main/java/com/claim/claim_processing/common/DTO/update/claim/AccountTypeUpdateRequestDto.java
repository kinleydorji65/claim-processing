package com.claim.claim_processing.common.DTO.update.claim;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTypeUpdateRequestDto {
    private String code;
    private String name;
    private ActivityEnum isActive;
    private String updatedBy;
}