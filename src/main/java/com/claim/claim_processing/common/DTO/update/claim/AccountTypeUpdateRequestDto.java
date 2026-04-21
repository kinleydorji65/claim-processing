package com.claim.claim_processing.common.DTO.update.claim;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTypeUpdateRequestDto {

    private String name;
    private String isActive;
}