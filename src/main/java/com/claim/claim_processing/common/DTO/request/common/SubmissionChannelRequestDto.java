package com.claim.claim_processing.common.DTO.request.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionChannelRequestDto {

    private String code;
    private String name;
}