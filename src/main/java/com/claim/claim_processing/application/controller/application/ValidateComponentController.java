package com.claim.claim_processing.application.controller.application;

import com.claim.claim_processing.application.service.application.ValidateComponent;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/validate")
@RequiredArgsConstructor
public class ValidateComponentController {

    private final ValidateComponent validateComponentService;

    /**
     * Validate component by NPPF number and component name
     * 
     * @param nppfNumber The NPPF number of the member
     * @param componentName The component name to validate
     * @return ApiResponseDTO with validation result
     */
    @GetMapping("/component")
    public ResponseEntity<ApiResponseDTO<String>> validateComponent(
            @RequestParam("nppfNumber") String nppfNumber,
            @RequestParam("componentCode") String componentCode) {
        
        log.info("Validating component for NPPF: {}, Component: {}", nppfNumber, componentCode);
        
            ApiResponseDTO<String> response = validateComponentService.validateComponent(nppfNumber, componentCode);
            return ResponseEntity.ok(response);
        
    }
}
