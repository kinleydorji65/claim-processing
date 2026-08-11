package com.claim.claim_processing.document.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCategoryResponseDto {
    private Long id;
    private String serviceCode;
    private String name;
    private String isActive;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private List<DocumentSubCategoryResponseDto> subCategories;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentSubCategoryResponseDto {  // ← Added 'static'
        private Long id;
        private Long documentCategoryId;
        private String documentCategoryName;
        private String name;
        private String createdBy;
        private String updatedBy;
        private List<DocumentRequirementResponseDto> requirementCategories;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentRequirementResponseDto {  // ← Already static, keep it
        private Long id;
        private Long subCategoryId;
        private String subCategoryName;
        private String name;
        private String isActive;
        private String isRequired;
        private LocalDateTime createdAt;
        private String createdBy;
        private LocalDateTime updatedAt;
        private String updatedBy;
    }
}