<<<<<<<< HEAD:src/main/java/com/claim/claim_processing/common/DTO/response/specialCase/SpecialCaseAuthorityResponseDto.java
package com.claim.claim_processing.common.DTO.response.specialCase;
========
package com.claim.claim_processing.common.DTO.response.partial;

>>>>>>>> b4d6508a6c394933134d296412d24e7321c33ea5:src/main/java/com/claim/claim_processing/common/DTO/response/partial/PartialWithdrawalReasonResponseDto.java
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalReasonResponseDto {

    private Long id;
    private String code;
    private String name;
    private ActivityEnum isActive;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}