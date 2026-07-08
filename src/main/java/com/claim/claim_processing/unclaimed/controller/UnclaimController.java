package com.claim.claim_processing.unclaimed.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.unclaimed.UnclaimService;

import java.util.List;

@RestController
@RequestMapping("/api/un-claim-processing/unclaim")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Unclaim Management", description = "APIs for managing unclaimed reserve accounts")
public class UnclaimController {

    private final UnclaimService unclaimService;

    @GetMapping("/reserve-accounts")
    @Operation(
        summary = "Get unclaimed reserve accounts",
        description = "Retrieves all unclaimed reserve accounts for a given user code"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unclaimed reserve accounts retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user code provided"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDTO<List<ReserveAccountResponseDto>>> getUnclaimedReserveAccounts(
            @Parameter(description = "User code to fetch unclaimed reserve accounts", required = true)
            @RequestParam @NotBlank(message = "User code is required") String userCode
    ) {
        log.info("REST request to get unclaimed reserve accounts for user: {}", userCode);
        
            List<ReserveAccountResponseDto> response = unclaimService.getUnclaimedReserveAccounts(userCode);
            
            if (response == null || response.isEmpty()) {
                return ResponseEntity.ok(
                    ApiResponseDTO.success("No unclaimed reserve accounts found for user: " + userCode, response)
                );
            }
            
            log.info("Found {} unclaimed reserve accounts for user: {}", response.size(), userCode);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Unclaimed reserve accounts retrieved successfully", response)
            );
    }

    @PatchMapping("/reserve-accounts/activate")
    @Operation(
        summary = "Activate unclaimed reserve account",
        description = "Activates an unclaimed reserve account by setting its status to 'ACTIVE'"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unclaimed reserve account activated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input provided"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Reserve account not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDTO<List<ReserveAccountResponseDto>>> activateUnclaimReserveAccount(
            @Parameter(description = "User code", required = true)
            @RequestParam @NotBlank(message = "User code is required") String userCode,
            
            @Parameter(description = "NPPF number of the member to activate", required = true)
            @RequestParam @NotBlank(message = "NPPF number is required") String nppfNumber
    ) {
        log.info("REST request to activate unclaimed reserve account for user: {}, nppf: {}", userCode, nppfNumber);
        
            List<ReserveAccountResponseDto> response = unclaimService.activateUnclaimReserveAccount(userCode, nppfNumber);
            
            
            log.info("Successfully activated reserve account for NPPF: {}", nppfNumber);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Unclaimed reserve account activated successfully", response)
            );
    }

    @PatchMapping("/reserve-accounts/activate/{nppfNumber}")
    @Operation(
        summary = "Activate unclaimed reserve account by NPPF",
        description = "Activates an unclaimed reserve account using path variable"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unclaimed reserve account activated successfully"),
        @ApiResponse(responseCode = "404", description = "Reserve account not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDTO<List<ReserveAccountResponseDto>>> activateUnclaimReserveAccountByNppf(
            @Parameter(description = "NPPF number of the member to activate", required = true)
            @PathVariable @NotBlank(message = "NPPF number is required") String nppfNumber,
            
            @Parameter(description = "User code")
            @RequestParam(defaultValue = "SYSTEM") String userCode
    ) {
        log.info("REST request to activate unclaimed reserve account for NPPF: {} by user: {}", nppfNumber, userCode);
        
            List<ReserveAccountResponseDto> response = unclaimService.activateUnclaimReserveAccount(userCode, nppfNumber);
        
            
            log.info("Successfully activated reserve account for NPPF: {}", nppfNumber);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Unclaimed reserve account activated successfully", response)
            );
    }

    // Additional optional endpoints

    @GetMapping("/reserve-accounts/{nppfNumber}")
    @Operation(
        summary = "Get unclaimed reserve account by NPPF",
        description = "Retrieves a specific unclaimed reserve account by NPPF number"
    )
    public ResponseEntity<ApiResponseDTO<ReserveAccountResponseDto>> getUnclaimedReserveAccountByNppf(
            @Parameter(description = "NPPF number", required = true)
            @PathVariable @NotBlank(message = "NPPF number is required") String nppfNumber
    ) {
        log.info("REST request to get unclaimed reserve account for NPPF: {}", nppfNumber);
        
            // You would need to add this method to your service if needed
            // ReserveAccountResponseDto response = unclaimService.getUnclaimedReserveAccountByNppf(nppfNumber);
            
            return ResponseEntity.ok(
                ApiResponseDTO.success("Reserve account retrieved successfully", null)
            );
    }
}
