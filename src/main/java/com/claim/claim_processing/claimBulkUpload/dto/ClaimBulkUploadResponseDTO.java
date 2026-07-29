package com.claim.claim_processing.claimBulkUpload.dto;



import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimBulkUploadResponseDTO {
    private int totalRows;
    private int successCount;
    private int failCount;
    @Builder.Default
    private List<ClaimBulkUploadResultDTO> rows = new ArrayList<>();
}
