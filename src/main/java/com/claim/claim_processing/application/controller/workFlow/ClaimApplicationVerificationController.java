// package com.claim.claim_processing.application.controller.workFlow;

// import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
// import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
// import com.claim.claim_processing.application.service.workFlow.ClaimApplicationVerificationService;
// import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/claim-application-verifications")
// @RequiredArgsConstructor
// public class ClaimApplicationVerificationController {

//     private final ClaimApplicationVerificationService verificationService;

//     @PostMapping
//     public ResponseEntity<ApiResponseDTO<ClaimApplicationVerificationResponseDto>> create(
//             @RequestBody ClaimApplicationVerificationRequestDto request) {

//         return ResponseEntity.status(HttpStatus.CREATED)
//                 .body(ApiResponseDTO.success(
//                         verificationService.create(request)
//                 ));
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<ApiResponseDTO<ClaimApplicationVerificationResponseDto>> update(
//             @PathVariable Long id,
//             @RequestBody ClaimApplicationVerificationRequestDto request) {

//         return ResponseEntity.ok(
//                 ApiResponseDTO.success(
//                         verificationService.update(id, request)
//                 )
//         );
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<ApiResponseDTO<ClaimApplicationVerificationResponseDto>> getById(
//             @PathVariable Long id) {

//         return ResponseEntity.ok(
//                 ApiResponseDTO.success(
//                         verificationService.getById(id)
//                 )
//         );
//     }

//     @GetMapping("/claim-application/{claimApplicationId}")
//     public ResponseEntity<ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>>> getByClaimApplicationId(
//             @PathVariable Long claimApplicationId) {

//         return ResponseEntity.ok(
//                 ApiResponseDTO.success(
//                         verificationService.getByClaimApplicationId(claimApplicationId)
//                 )
//         );
//     }

//     @GetMapping
//     public ResponseEntity<ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>>> getAll() {

//         return ResponseEntity.ok(
//                 ApiResponseDTO.success(
//                         verificationService.getAll()
//                 )
//         );
//     }
// }