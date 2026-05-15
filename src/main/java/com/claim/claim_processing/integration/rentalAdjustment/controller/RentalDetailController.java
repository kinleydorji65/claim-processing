package com.claim.claim_processing.integration.rentalAdjustment.controller;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.rentalAdjustment.dto.RentalDetailResponseDto;
import com.claim.claim_processing.integration.rentalAdjustment.service.RentalDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/rental-details")
@RequiredArgsConstructor
public class RentalDetailController {

    private final RentalDetailService rentalDetailService;

    @GetMapping("/{identityId}")
    public ResponseEntity<ApiResponseDTO<List<RentalDetailResponseDto>>> getRentalDetails(
            @PathVariable String identityId) {

        return ResponseEntity.ok(
                rentalDetailService.getRentalDetails(identityId)
        );
    }
}
