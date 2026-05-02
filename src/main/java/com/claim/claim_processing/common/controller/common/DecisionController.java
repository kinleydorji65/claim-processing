package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.DecisionRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DecisionResponseDto;
import com.claim.claim_processing.common.service.common.DecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/common/decisions")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionService decisionService;

    @PostMapping
    public ResponseEntity<DecisionResponseDto> create(
            @RequestBody DecisionRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(decisionService.createDecision(requestDto));
    }


    @GetMapping
    public ResponseEntity<List<DecisionResponseDto>> getAll() {
        return ResponseEntity.ok(decisionService.getAll());
    }


    @GetMapping("/active")
    public ResponseEntity<List<DecisionResponseDto>> getAllActive() {
        return ResponseEntity.ok(decisionService.getAllActive());
    }


    @GetMapping("/{id}")
    public ResponseEntity<DecisionResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(decisionService.getById(id));
    }


    @GetMapping("/code/{code}")
    public ResponseEntity<DecisionResponseDto> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(decisionService.getByCode(code));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<DecisionResponseDto> update(
            @PathVariable Long id,
            @RequestBody DecisionRequestDto requestDto
    ) {
        return ResponseEntity.ok(decisionService.updateDecision(id, requestDto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        decisionService.deleteDecision(id);
        return ResponseEntity.noContent().build();
    }
}