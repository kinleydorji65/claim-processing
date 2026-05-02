package com.claim.claim_processing.common.DTO.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private Long status;
    private String message;
    private T data;

    // Static helper methods for common responses
    public static <T> ApiResponseDTO<T> success(T data) {
        return ApiResponseDTO.<T>builder()
                .status(200L)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return ApiResponseDTO.<T>builder()
                .status(200L)
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponseDTO<Void> error(Long status, String message) {
        return ApiResponseDTO.<Void>builder()
                .status(status)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponseDTO<T> error(Long status, String message, T data) {
        return ApiResponseDTO.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponseDTO<Void> created(String message) {
        return ApiResponseDTO.<Void>builder()
                .status(201L)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponseDTO<T> created(T data) {
        return ApiResponseDTO.<T>builder()
                .status(201L)
                .message("Created successfully")
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> notFound(String message) {
        return ApiResponseDTO.<T>builder()
                .status(404L)
                .message(message)
                .data(null)
                .build();
    }

    public static ApiResponseDTO<Void> badRequest(String message) {
        return ApiResponseDTO.<Void>builder()
                .status(400L)
                .message(message)
                .data(null)
                .build();
    }
}
