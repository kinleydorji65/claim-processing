package com.claim.claim_processing.common.entities.others;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistredAgencyMappingId implements Serializable {

    private String userCode;
    private String agencyCode;
}
