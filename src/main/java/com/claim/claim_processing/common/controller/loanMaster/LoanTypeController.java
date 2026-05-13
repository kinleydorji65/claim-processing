package com.claim.claim_processing.common.controller.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanTypeResponseDto;
import com.claim.claim_processing.common.service.loanMaster.LoanTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master/loans/type-master")
@RequiredArgsConstructor
public class LoanTypeController {

    private final LoanTypeService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody LoanTypeRequestDto requestDto) {

        ApiResponseDTO<LoanTypeResponseDto> response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody LoanTypeRequestDto requestDto) {

        ApiResponseDTO<LoanTypeResponseDto> response = service.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<LoanTypeResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {

        ApiResponseDTO<LoanTypeResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<LoanTypeResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<LoanTypeResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}